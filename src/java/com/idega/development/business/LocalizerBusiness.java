package com.idega.development.business;


import java.util.Map;

public interface LocalizerBusiness {

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#storeLocalizedString
	 */
	public Object[] storeLocalizedStrings(String key, String newKey, String value, String bundleIdentifier, String locale);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedKey
	 */
	public String getLocalizedKey(int index, String bundleIdentifier);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedStrings
	 */

	public Map<String, String> getLocalizedStringProperties(String bundleIdentifier, String storageIdentifier, String locale);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#removeLocalizedKey
	 */
//	public int removeLocalizedKey(String key, String bundleIdentifier, String storeIdentifier, String locale);
	
	public Object[] removeLocalizedKey(String key, String bundleIdentifier, String storageIdentifier, String locale);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedString
	 */
	public Object getLocalizedString(String key, String bundleIdentifier, String locale, String storage);
	
	public int setPriorityLevel(String storageIdentifier, String levelValue);
	
	public int setAutoInsert(String storageIdentifier, String value);
}