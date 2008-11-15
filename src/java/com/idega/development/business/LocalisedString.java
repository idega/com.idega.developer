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

public class LocalisedString {
	String index;
	String value;
	String storageIdentifier;
	
	public LocalisedString(String index, String value, String storageIdentifier) {
		this.index = index;
		this.value = value;
		this.storageIdentifier = storageIdentifier;
	}
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
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
