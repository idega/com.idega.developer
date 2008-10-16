package com.idega.development.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface LocalizerBusinessHome extends IBOHome {

	public LocalizerBusiness create() throws CreateException, RemoteException;
}