/**
 * 
 */
package com.idega.development.business;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
 *  Last modified: $Date: 2008/11/10 12:15:07 $ by $Author: anton $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
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
	
	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale, String storeIdentifier) {

		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}
		
		if (newKey != null && newKey.length() > 0) {
			key = newKey;
		}
		getIWMainApplication().getMessageFactory().getResourceByIdentifier(storeIdentifier).setMessage(key, value, bundleIdentifier, currentLocale);
		
		Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResourceByIdentifier(storeIdentifier).getAllLocalisedKeys(bundleIdentifier, currentLocale);
		
		Object[] keys = messageKeys.toArray();
		for (int i = 0; i < keys.length; i++) {
			String tempKey = (String)keys[i];
			if (key.equals(tempKey)) {
				return i;
			}
		}
		
		return 0;
	}
	
	public String getLocalizedKey(int index, String bundleIdentifier) {
		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
		String key = bundle.getLocalizableStrings()[index];
		return key;
	}
	
//	public Map<String, String> getLocalizedStrings(String bundleIdentifier) {
//		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//		map.put(CoreConstants.EMPTY, CoreConstants.EMPTY);
//
//		String[] strings = getIWMainApplication().getBundle(bundleIdentifier).getLocalizableStrings();
//		if (strings != null) {
//			for (int i = 0; i < strings.length; i++) {
//				String string = strings[i];
//				map.put(string, string);
//			}
//		}
//		
//		return map;
//	}
	
	public Map<String, String> getLocalizedStrings(String bundleIdentifier, String storageIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}
		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(CoreConstants.EMPTY, CoreConstants.EMPTY);

		Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResourceByIdentifier(storageIdentifier).getAllLocalisedKeys(bundleIdentifier, currentLocale);
		
		Object[] keys = messageKeys.toArray();
		for (int i = 0; i < keys.length; i++) {
			String string = (String)keys[i];
			map.put(string, string);
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
	
	public int removeLocalizedKey(String key, String bundleIdentifier, String storeIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}
		
		Set<Object> messageKeys = getIWMainApplication().getMessageFactory().getResourceByIdentifier(storeIdentifier).getAllLocalisedKeys(bundleIdentifier, currentLocale);

		Object[] keys = messageKeys.toArray();
		int index = 0;
		for (int i = 0; i < keys.length; i++) {
			String tempKey = (String)keys[i];
			if (key.equals(tempKey)) {
				index = i;
				break;
			}
		}

		getIWMainApplication().getMessageFactory().getResourceByIdentifier(storeIdentifier).removeMessage(key, bundleIdentifier, currentLocale);
		
		return index;
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
	
	public String getLocalizedString(String key, String bundleIdentifier, String locale, String storage) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		String value = (String)getIWMainApplication().getMessageFactory().getResourceByIdentifier(storage).getMessage(key, CoreConstants.EMPTY, bundleIdentifier, currentLocale);
		if (value == null) {
			value = "";
		}
		return value;
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