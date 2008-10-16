package com.idega.development.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class LocalizerBusinessHomeImpl extends IBOHomeImpl implements LocalizerBusinessHome {

	public Class getBeanInterfaceClass() {
		return LocalizerBusiness.class;
	}

	public LocalizerBusiness create() throws CreateException {
		return (LocalizerBusiness) super.createIBO();
	}
}