package com.idega.development.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface FilesManagerBusinessHome extends IBOHome {
	public FilesManagerBusiness create() throws CreateException, RemoteException;
}