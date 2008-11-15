/**
 * 
 */
package com.idega.development.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.development.presentation.Localizer;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;
import com.idega.util.messages.ResourceLevelChangeEvent;


/**
 * <p>
 * TODO laddi Describe Type LocalizerBusinessBean
 * </p>
 *  Last modified: $Date: 2008/11/15 15:35:12 $ by $Author: anton $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
@Scope("singleton")
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
	
	public Object[] storeLocalizedStrings(String keyWithStorage, String newKey, String value, String bundleIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}
		
		String key = keyWithStorage.split(CoreConstants.SPACE)[0];
		
		if (newKey != null && newKey.length() > 0) {
			key = newKey;
		}
		
		Map<String, Object> setValues = getIWMainApplication().getMessageFactory().setLocalisedMessageToAutoInsertRes(key, value, bundleIdentifier, currentLocale);

		List<MessageResource> resourceList = getIWMainApplication().getMessageFactory().getResourceList();

		List<LocalisedString> newStrings = new ArrayList<LocalisedString>();
		int globalIndex = 0;
		for(MessageResource resource: resourceList) {
			Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResourceByIdentifier(resource.getIdentifier()).getAllLocalisedKeys(bundleIdentifier, currentLocale);
			
			Object[] keys = messageKeys.toArray();
			for (int i = 0; i < keys.length; i++) {
				
				Set<String> changedResources = setValues.keySet();
				for(String changedResource : changedResources) {
					
					if (key.equals(keys[i]) && changedResource.equals(resource.getIdentifier())) {
						LocalisedString str = new LocalisedString(String.valueOf(globalIndex), (String)setValues.get(changedResource), changedResource);
						newStrings.add(str);
					}
				}
				globalIndex++;
			}
		}
		
		return newStrings.toArray();
	}
	
	public String getLocalizedKey(int index, String bundleIdentifier) {
		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
		String key = bundle.getLocalizableStrings()[index];
		return key;
	}
	
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
			List<MessageResource> resourceList = getIWMainApplication().getMessageFactory().getResourceList();
			for(MessageResource resource : resourceList) {
				messageKeys = new TreeSet<Object>();
				String resourceIdentifier = resource.getIdentifier();
				messageKeys.addAll(getIWMainApplication().getMessageFactory().getResourceByIdentifier(resourceIdentifier).getAllLocalisedKeys(bundleIdentifier, currentLocale));
				messageKeysWithStorage.put(resourceIdentifier, messageKeys);
			}
		} else {
			messageKeys = new TreeSet<Object>();
			messageKeys.addAll(getIWMainApplication().getMessageFactory().getResourceByIdentifier(storageIdentifier).getAllLocalisedKeys(bundleIdentifier, currentLocale));
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
	
	public Object[] removeLocalizedKey(String keyWithStorage, String bundleIdentifier, String storageIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}
		
		String key = keyWithStorage.split(CoreConstants.SPACE)[0];
		
		List<MessageResource> resourceList = getIWMainApplication().getMessageFactory().getResourceList();

		int globalIndex = 0;
		List<Integer> removedStrings = new ArrayList<Integer>();
		for(MessageResource resource: resourceList) {
			if(!storageIdentifier.equals(Localizer.ALL_RESOURCES) && !resource.getIdentifier().equals(storageIdentifier)) {
				continue;
			}
			Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResourceByIdentifier(resource.getIdentifier()).getAllLocalisedKeys(bundleIdentifier, currentLocale);
			Object[] keys = messageKeys.toArray();

			for (int i = 0; i < keys.length; i++) {
				String tempKey = (String)keys[i];
				if (key.equals(tempKey) && resource.isAutoInsert()) {
					removedStrings.add(globalIndex);
				}
				globalIndex++;
			}
		}

		getIWMainApplication().getMessageFactory().removeLocalisedMessageFromAutoInsertRes(key, bundleIdentifier, currentLocale);
		
		return removedStrings.toArray();
	}
	
	/*Old method that was use in localiser*/
//	public String getLocalizedString(String key, String bundleIdentifier, String locale) {
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
	
	public Object getLocalizedString(String key, String bundleIdentifier, String locale, String storage) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		String value = (String)getIWMainApplication().getMessageFactory().getResourceByIdentifier(storage).getMessage(key, bundleIdentifier, currentLocale);
		if (value == null) {
			value = "";
		}
		
		LocalisedString returnObj = new LocalisedString(key, value, storage);
		return returnObj;
	}
	
	public int setPriorityLevel(String storageIdentifier, String levelValue) {
		MessageResource resource = getIWMainApplication().getMessageFactory().getResourceByIdentifier(storageIdentifier);
		resource.setLevel(MessageResourceImportanceLevel.getLevel(Integer.parseInt(levelValue)));
		ELUtil.getInstance().publishEvent(new ResourceLevelChangeEvent(this));
		return SUCCESS;
	}
	
	public int setAutoInsert(String storageIdentifier, String value) {
		MessageResource resource = getIWMainApplication().getMessageFactory().getResourceByIdentifier(storageIdentifier);
		resource.setAutoInsert(Boolean.parseBoolean(value));
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
}