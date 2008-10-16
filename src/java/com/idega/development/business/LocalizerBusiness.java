package com.idega.development.business;


import java.util.Map;
import com.idega.business.IBOService;
import java.rmi.RemoteException;

public interface LocalizerBusiness extends IBOService {

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#storeLocalizedString
	 */
	public int storeLocalizedString(String key, String newKey, String value, String bundleIdentifier, String locale) throws RemoteException;

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedKey
	 */
	public String getLocalizedKey(int index, String bundleIdentifier) throws RemoteException;

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedStrings
	 */
	public Map getLocalizedStrings(String bundleIdentifier) throws RemoteException;

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#removeLocalizedKey
	 */
	public int removeLocalizedKey(String key, String bundleIdentifier) throws RemoteException;

	/**
	 * @see com.idega.development.business.LocalizerBusinessBean#getLocalizedString
	 */
	public String getLocalizedString(String key, String bundleIdentifier, String locale) throws RemoteException;
}