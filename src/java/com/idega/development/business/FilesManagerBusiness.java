package com.idega.development.business;


import java.rmi.RemoteException;

import com.idega.business.IBOSession;

public interface FilesManagerBusiness extends IBOSession {
	/**
	 * @see com.idega.development.business.FilesManagerBusinessBean#copyFilesToRepository
	 */
	public boolean copyFilesToRepository() throws RemoteException;
}