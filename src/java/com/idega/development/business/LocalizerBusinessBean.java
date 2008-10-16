/**
 * 
 */
package com.idega.development.business;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWBundle;


/**
 * <p>
 * TODO laddi Describe Type LocalizerBusinessBean
 * </p>
 *  Last modified: $Date: 2008/10/16 20:06:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class LocalizerBusinessBean extends IBOServiceBean implements LocalizerBusiness {

	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale) {
		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
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
		
		bundle.getResourceBundle(currentLocale).setLocalizedString(key, value);

		String[] string = bundle.getLocalizableStrings();
		for (int i = 0; i < string.length; i++) {
			String tempKey = string[i];
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
	
	public Map getLocalizedStrings(String bundleIdentifier) {
		Map map = new LinkedHashMap();
		map.put("", "");

		String[] strings = getIWMainApplication().getBundle(bundleIdentifier).getLocalizableStrings();
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				String string = strings[i];
				map.put(string, string);
			}
		}
		
		return map;
	}
	
	public int removeLocalizedKey(String key, String bundleIdentifier) {
		IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);

		String[] string = bundle.getLocalizableStrings();
		int index = 0;
		for (int i = 0; i < string.length; i++) {
			String tempKey = string[i];
			if (key.equals(tempKey)) {
				index = i;
			}
		}

		bundle.removeLocalizableString(key);
		bundle.storeState();
		
		return index;
	}
	
	public String getLocalizedString(String key, String bundleIdentifier, String locale) {
		Locale currentLocale = null;
		if (locale.length() > 2) {
			currentLocale = new Locale(locale.substring(0, 2), locale.substring(3));
		}
		else {
			currentLocale = new Locale(locale);
		}

		String value = getIWMainApplication().getBundle(bundleIdentifier).getResourceBundle(currentLocale).getLocalizedString(key);
		if (value == null) {
			value = "";
		}
		return value;
	}
}