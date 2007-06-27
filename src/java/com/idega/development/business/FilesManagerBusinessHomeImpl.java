package com.idega.development.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class FilesManagerBusinessHomeImpl extends IBOHomeImpl implements FilesManagerBusinessHome {

	private static final long serialVersionUID = 3030880119353074554L;

	public Class getBeanInterfaceClass() {
		return FilesManagerBusiness.class;
	}

	public FilesManagerBusiness create() throws CreateException {
		return (FilesManagerBusiness) super.createIBO();
	}
}