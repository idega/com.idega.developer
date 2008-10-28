package com.idega.development.business;


import java.util.Map;

public interface LocalizerBusiness {

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#storeLocalizedString
	 */
//	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale);
	
	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale, String storeIdentifier);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedKey
	 */
	public String getLocalizedKey(int index, String bundleIdentifier);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedStrings
	 */
//	public Map<String, String> getLocalizedStrings(String bundleIdentifier);
	
	public Map<String, String> getLocalizedStrings(String bundleIdentifier, String storageIdentifier, String locale);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#removeLocalizedKey
	 */
//	public int removeLocalizedKey(String key, String bundleIdentifier);
	
	public int removeLocalizedKey(String key, String bundleIdentifier, String storeIdentifier, String locale);

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedString
	 */
//	public String getLocalizedString(String key, String bundleIdentifier, String locale);
	
	public String getLocalizedString(String key, String bundleIdentifier, String locale, String storage);
	
	public int setPriorityLevel(String storageIdentifier, String levelValue);
	
	public int setAutoInsert(String storageIdentifier, String value);
}