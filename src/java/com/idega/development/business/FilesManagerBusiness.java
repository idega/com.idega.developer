package com.idega.development.business;


import com.idega.business.IBOSession;
import java.rmi.RemoteException;

public interface FilesManagerBusiness extends IBOSession {
	/**
	 * @see com.idega.development.business.FilesManagerBusinessBean#copyFilesToSlide
	 */
	public boolean copyFilesToSlide() throws RemoteException;
}