package com.idega.development.business;

/**
 *
 *
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 *
 * Last modified: Nov 13, 2008 by Author: Anton
 *
 */

public class LocalizedString {

	String key;
	String value;
	String storageIdentifier;

	public LocalizedString(String key, String value, String storageIdentifier) {
		this.key = key;
		this.value = value;
		this.storageIdentifier = storageIdentifier;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getStorageIdentifier() {
		return storageIdentifier;
	}

	public void setStorageIdentifier(String storageIdentifier) {
		this.storageIdentifier = storageIdentifier;
	}

}
