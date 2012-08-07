package com.idega.development.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.development.presentation.Localizer;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
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
	/*Old method that was use in localiser*/
//	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale) {
//		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
//		Locale currentLocale = null;
//		if (locale.length() > 2) {
//			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
//		}
//		else {
//			currentLocale = new Locale(locale);
//		}
//
//		if (newKey != null && newKey.length() > 0) {
//			key = newKey;
//		}
//
//		bundle.getResourceBundle(currentLocale).setLocalizedString(key, value);
//
//		String[] string = bundle.getLocalizableStrings();
//		for (int i = 0; i < string.length; i++) {
//			String tempKey = string[i];
//			if (key.equals(tempKey)) {
//				return i;
//			}
//		}
//
//		return 0;
//	}

	@Override
	public void storeLocalizedStrings(String keyWithStorage, String newKey, String value, String bundleIdentifier, String locale, String selectedStorageIdentifier) {
//		Locale currentLocale = null;
//		if (locale.length() > 2) {
//			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
//		}
//		else {
//			currentLocale = new Locale(locale);
//		}

		//key comes as a 'key (storage_identifier)' string
		String key = keyWithStorage.split(CoreConstants.SPACE)[0];

		if (newKey != null && newKey.length() > 0) {
			key = newKey;
		}

		//after inserting to all autoinsert resources we should get map<'modified storage_resource', 'new_value'>
		getIWMainApplication().getMessageFactory().setLocalisedMessageToAutoInsertRes(key, value, bundleIdentifier, LocaleUtil.getLocale(locale));

//		List<LocalisedString> stringListForView = new ArrayList<LocalisedString>();
//
//		List<MessageResource> resourceList = getResourceList(getIWMainApplication(), selectedStorageIdentifier, bundleIdentifier, LocaleUtil.getLocale(locale));
//
//		//creating a full list of localized strings
//		for(MessageResource resource : resourceList) {
//
//			Set<Object> localisedKeys = resource.getAllLocalisedKeys();
//			for(Object localizedKey : localisedKeys) {
//
//				String localizedValue = String.valueOf(resource.getMessage(localizedKey));
//				LocalisedString str = new LocalisedString(String.valueOf(localizedKey), localizedValue, resource.getIdentifier());
//				stringListForView.add(str);
//			}
//		}


//		int globalIndex = 0;
//		for(String type : resourceTypes) {
//			MessageResource resource = getIWMainApplication().getMessageFactory().getResource(type, bundleIdentifier, currentLocale);
//			if(resource == null)
//				continue;
//			Set<Object> resourceMessageKeys = resource.getAllLocalisedKeys();
//
//			for (Object resourceKey : resourceMessageKeys) {
//
//				Set<String> changedResources = setValues.keySet();
//				for(String changedResource : changedResources) {
//
//					if (key.equals(resourceKey) && changedResource.equals(type)) {
//						LocalisedString str = new LocalisedString(String.valueOf(globalIndex), (String)setValues.get(changedResource), changedResource);
//						newStrings.add(str);
//					}
//				}
//				globalIndex++;
//			}
//		}
//
//		return stringListForView.toArray();
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
				if(resource == null)
					continue;
				messageKeys.addAll(resource.getAllLocalisedKeys());
				messageKeysWithStorage.put(type, messageKeys);
			}
		} else {
			messageKeys = new TreeSet<Object>();
			messageKeys.addAll(getIWMainApplication().getMessageFactory().getResource(storageIdentifier, bundleIdentifier, currentLocale).getAllLocalisedKeys());
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

	/*Old method that was use in localiser*/
//	public int removeLocalizedKey(String key, String bundleIdentifier) {
//		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
//
//		String[] string = bundle.getLocalizableStrings();
//		int index = 0;
//		for (int i = 0; i < string.length; i++) {
//			String tempKey = string[i];
//			if (key.equals(tempKey)) {
//				index = i;
//			}
//		}
//
//		bundle.removeLocalizableString(key);
//		bundle.storeState();
//
//		return index;
//	}

	@Override
	public void removeLocalizedKey(String keyWithStorage, String bundleIdentifier, String storageIdentifier, String locale) {
//		Locale currentLocale = null;
//		if (locale.length() > 2) {
//			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
//		}
//		else {
//			currentLocale = new Locale(locale);
//		}

		String key = keyWithStorage.split(CoreConstants.SPACE)[0];
		getIWMainApplication().getMessageFactory().removeLocalisedMessageFromAutoInsertRes(key, bundleIdentifier, LocaleUtil.getLocale(locale));

//		List<MessageResource> resourceList = getIWMainApplication().getMessageFactory().getAvailableUninitializedMessageResources();

//		int globalIndex = 0;
//		List<Integer> removedStrings = new ArrayList<Integer>();
//		for(MessageResource resource: resourceList) {
//			if(!storageIdentifier.equals(Localizer.ALL_RESOURCES) && !resource.getIdentifier().equals(storageIdentifier)) {
//				continue;
//			}
//			Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResource(resource.getIdentifier(), bundleIdentifier, currentLocale).getAllLocalisedKeys();
//			Object[] keys = messageKeys.toArray();
//
//			for (int i = 0; i < keys.length; i++) {
//				String tempKey = (String)keys[i];
//				if (key.equals(tempKey) && resource.isAutoInsert()) {
//					removedStrings.add(globalIndex);
//				}
//				globalIndex++;
//			}
//		}
//
//		getIWMainApplication().getMessageFactory().removeLocalisedMessageFromAutoInsertRes(key, bundleIdentifier, currentLocale);
//
//		return removedStrings.toArray();
	}

	/*Old method that was use in localiser*/
//   public String getLocalizedString(String key, String bundleIdentifier, String locale) {
//		Locale currentLocale = null;
//		if (locale.length() > 2) {
//			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
//		}
//		else {
//			currentLocale = new Locale(locale);
//		}
//
//		String value = getIWMainApplication().getBundle(bundleIdentifier).getResourceBundle(currentLocale).getLocalizedString(key);
//		if (value == null) {
//			value = "";
//		}
//		return value;
//	}

	@Override
	public Object getLocalizedString(String key, String bundleIdentifier, String locale, String storage) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		String value = (String)getIWMainApplication().getMessageFactory().getResource(storage, bundleIdentifier, currentLocale).getMessage(key);
		if (value == null) {
			value = "";
		}

		LocalisedString returnObj = new LocalisedString(key, value, storage);
		return returnObj;
	}

	public Object[] updateLocalizedStringList(String selectedStorageIdentifier, String bundleIdentifier, String locale) {
		List<LocalisedString> stringListForView = new ArrayList<LocalisedString>();

		List<MessageResource> resourceList = getResourceList(getIWMainApplication(), selectedStorageIdentifier, bundleIdentifier, LocaleUtil.getLocale(locale));

		//creating a full list of localized strings
		for(MessageResource resource : resourceList) {

			Set<String> localisedKeys = resource.getAllLocalisedKeys();
			for(String localizedKey : localisedKeys) {

				String localizedValue = String.valueOf(resource.getMessage(localizedKey));
				LocalisedString str = new LocalisedString(String.valueOf(localizedKey), localizedValue, resource.getIdentifier());
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

		if(iwc != null)
			iwma = iwc.getIWMainApplication();
		else
			iwma = IWMainApplication.getDefaultIWMainApplication();

		return iwma;
	}

	private List<MessageResource> getResourceList(IWMainApplication iwma, String selectedStorageIdentifier, String bundleIdentifier, Locale locale) {
		List<MessageResource> resourceList;
		if(selectedStorageIdentifier.equals(Localizer.ALL_RESOURCES)) {
			resourceList = iwma.getMessageFactory().getResourceListByBundleAndLocale(bundleIdentifier, locale);
		} else {
			resourceList = new ArrayList<MessageResource>(1);
			MessageResource resource = iwma.getMessageFactory().getResource(selectedStorageIdentifier, bundleIdentifier, locale);
			if(resource != null)
				resourceList.add(resource);
		}
		return resourceList;
	}
}