package com.idega.development.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.development.event.LocalizationChangedEvent;
import com.idega.development.presentation.Localizer;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;
import com.idega.util.messages.ResourceLevelChangeEvent;


/**
 * <p>
 * TODO laddi Describe Type LocalizerBusinessBean
 * </p>
 *  Last modified: $Date: 2009/01/05 10:27:23 $ by $Author: anton $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.10 $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service("localizer")
public class LocalizerBusinessBean implements LocalizerBusiness {

	private static final int SUCCESS = 1;

	@Override
	public void storeLocalizedStrings(String keyWithStorage, String newKey, String value, String bundleIdentifier, String locale, String selectedStorageIdentifier) {
		//key comes as a 'key (storage_identifier)' string
		String key = keyWithStorage == null ? null : keyWithStorage.split(CoreConstants.SPACE)[0];

		if (newKey != null && newKey.length() > 0) {
			key = newKey;
		}

		//after inserting to all autoinsert resources we should get map<'modified storage_resource', 'new_value'>
		Map<String, String> localizations = getIWMainApplication().getMessageFactory().setLocalizedMessageToAutoInsertRes(
				key,
				value,
				bundleIdentifier,
				LocaleUtil.getLocale(locale)
		);
		ELUtil.getInstance().publishEvent(new LocalizationChangedEvent(this, localizations));
	}

	@Override
	public String getLocalizedKey(int index, String bundleIdentifier) {
		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
		String key = bundle.getLocalizableStrings()[index];
		return key;
	}

	@Override
	public Map<String, String> getLocalizedStringProperties(String bundleIdentifier, String storageIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(CoreConstants.EMPTY, CoreConstants.EMPTY);
		Map<String, Set<Object>> messageKeysWithStorage = new LinkedHashMap<String, Set<Object>>();
		Set<Object> messageKeys;
		if(storageIdentifier.equals(Localizer.ALL_RESOURCES)) {
			List<String> resourceTypes = getIWMainApplication().getAvailableMessageStorageTypes();
			for(String type : resourceTypes) {
				messageKeys = new TreeSet<Object>();
				MessageResource resource = getIWMainApplication().getMessageFactory().getResource(type, bundleIdentifier, currentLocale);
				if(resource == null) {
					continue;
				}
				messageKeys.addAll(resource.getAllLocalizedKeys());
				messageKeysWithStorage.put(type, messageKeys);
			}
		} else {
			messageKeys = new TreeSet<Object>();
			messageKeys.addAll(getIWMainApplication().getMessageFactory().getResource(storageIdentifier, bundleIdentifier, currentLocale).getAllLocalizedKeys());
			messageKeysWithStorage.put(storageIdentifier, messageKeys);
		}

		for(String storageKey : messageKeysWithStorage.keySet()) {
			Object[] keys = messageKeysWithStorage.get(storageKey).toArray();
			for (int i = 0; i < keys.length; i++) {
				String string = (String)keys[i];
				string = addStorage(string, storageKey);
				map.put(string, string);
			}
		}

		return map;
	}

	@Override
	public void removeLocalizedKey(String keyWithStorage, String bundleIdentifier, String storageIdentifier, String locale) {
		String key = keyWithStorage.split(CoreConstants.SPACE)[0];
		getIWMainApplication().getMessageFactory().removeLocalizedMessageFromAutoInsertRes(key, bundleIdentifier, LocaleUtil.getLocale(locale));
	}

	@Override
	public Object getLocalizedString(String key, String bundleIdentifier, String locale, String storage) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		String value = getIWMainApplication().getMessageFactory().getResource(storage, bundleIdentifier, currentLocale).getMessage(key);
		if (value == null) {
			value = CoreConstants.EMPTY;
		}

		LocalizedString returnObj = new LocalizedString(key, value, storage);
		return returnObj;
	}

	public Object[] updateLocalizedStringList(String selectedStorageIdentifier, String bundleIdentifier, String locale) {
		List<LocalizedString> stringListForView = new ArrayList<LocalizedString>();

		List<MessageResource> resourceList = getResourceList(getIWMainApplication(), selectedStorageIdentifier, bundleIdentifier, LocaleUtil.getLocale(locale));

		//	Creating a full list of localized strings
		for (MessageResource resource : resourceList) {
			Set<String> localizedKeys = resource.getAllLocalizedKeys();
			if (ListUtil.isEmpty(localizedKeys)) {
				continue;
			}

			Set<String> keysCopy = new HashSet<String>(localizedKeys);
			for (String localizedKey: keysCopy) {
				String localizedValue = String.valueOf(resource.getMessage(localizedKey));
				LocalizedString str = new LocalizedString(String.valueOf(localizedKey), localizedValue, resource.getIdentifier());
				stringListForView.add(str);
			}
		}

		return stringListForView.toArray();
	}

	//TODO change to make change to each resource specified by bundle and locale
	@Override
	public int setPriorityLevel(String storageIdentifier, String levelValue) {
		List<MessageResource> resources = getIWMainApplication().getMessageFactory().getResourceListByStorageIdentifier(storageIdentifier);
		for(MessageResource resource : resources) {
			resource.setLevel(MessageResourceImportanceLevel.getLevel(Integer.parseInt(levelValue)));
		}
		ELUtil.getInstance().publishEvent(new ResourceLevelChangeEvent(this));
		return SUCCESS;
	}

	//TODO change to make change to each resource specified by bundle and locale
	@Override
	public int setAutoInsert(String storageIdentifier, String value) {
		List<MessageResource> resources = getIWMainApplication().getMessageFactory().getResourceListByStorageIdentifier(storageIdentifier);
		for(MessageResource resource : resources) {
			resource.setAutoInsert(Boolean.parseBoolean(value));
		}
		return SUCCESS;
	}

	private String addStorage(String value, String storageIdentifier) {
		StringBuffer str = new StringBuffer(value);
		return str.append(" (").append(storageIdentifier).append(")").toString();
	}

	private IWMainApplication getIWMainApplication() {
		final IWContext iwc = IWContext.getCurrentInstance();
		final IWMainApplication iwma;

		if(iwc != null) {
			iwma = iwc.getIWMainApplication();
		} else {
			iwma = IWMainApplication.getDefaultIWMainApplication();
		}

		return iwma;
	}

	private List<MessageResource> getResourceList(IWMainApplication iwma, String selectedStorageIdentifier, String bundleIdentifier, Locale locale) {
		List<MessageResource> resourceList;
		if(selectedStorageIdentifier.equals(Localizer.ALL_RESOURCES)) {
			resourceList = iwma.getMessageFactory().getResourceListByBundleAndLocale(bundleIdentifier, locale);
		} else {
			resourceList = new ArrayList<MessageResource>(1);
			MessageResource resource = iwma.getMessageFactory().getResource(selectedStorageIdentifier, bundleIdentifier, locale);
			if(resource != null) {
				resourceList.add(resource);
			}
		}
		return resourceList;
	}
}