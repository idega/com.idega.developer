/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectType;
import com.idega.core.data.ICObjectTypeHome;
import com.idega.data.IDOLookup;

/**
 * <p>Title: BundleComponentFactory</p>
 * <p>Description: Factory class to provide BundleComponent object 
 * 		given a object type identifier.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class BundleComponentFactory {
	
	static BundleComponentFactory factory = null;
	static Map lookup = null;
	
	private BundleComponentFactory(){
		lookup = new HashMap();
		createLookupTable();
	}
	/**
	 * Get a static instance of factory
	 * @return instance of factory
	 */
	public static BundleComponentFactory getInstance(){
		if(factory == null)
			factory = new BundleComponentFactory();
		return factory;
	}
	
	public  BundleComponent getBundleComponent(String identifier)throws IllegalArgumentException{
		if(lookup.containsKey(identifier))
			return (BundleComponent) lookup.get(identifier);
		else
			throw new IllegalArgumentException("Argument "+identifier+" not recognized");
	}
	
	public BundleComponent getBundleComponent(ICObject icobject)throws IllegalArgumentException{
		return getBundleComponent(icobject.getObjectType());
	}
	
	private void createLookupTable(){
		try {
			ICObjectTypeHome home = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			Collection allObjectTypes = home.findAll();
			Iterator iter = allObjectTypes.iterator();
			String type;
			while (iter.hasNext()) {
				type = (String) iter.next();
				BundleComponent comp = home.findByPrimaryKey(type);
				lookup.put(type,comp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
