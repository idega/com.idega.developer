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

import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
import com.idega.data.IDOLookup;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.SingletonRepository;

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
	
	static Instantiator instantiator = new Instantiator() { 
		public Object getInstance() { 
			return new BundleComponentFactory();
		}
		public void unload() {
			lookup = null;
		}
	};
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
		return (BundleComponentFactory) SingletonRepository.getRepository().getInstance(BundleComponentFactory.class, instantiator);
	}
	
	public  BundleComponent getBundleComponent(String identifier)throws IllegalArgumentException{
		if(getLookupTable().containsKey(identifier)) {
			return (BundleComponent) getLookupTable().get(identifier);
		}
		else {
			throw new IllegalArgumentException("Argument "+identifier+" not recognized");
		}
	}
	
	public BundleComponent getBundleComponent(ICObject icobject)throws IllegalArgumentException{
		return getBundleComponent(icobject.getObjectType());
	}
	
	public void refreshCache() {
		lookup = new HashMap();
		createLookupTable();
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
			createLookupTableOld();
			e.printStackTrace();
		}
	}
	
	private Map getLookupTable(){
		if(lookup==null){
			lookup = new HashMap();
			createLookupTable();
		}
		return lookup;
	}
	
	private void createLookupTableOld(){
		BundleComponent comp = new IWBlockComponent();
		lookup.put(comp.type(),comp);
		comp = new IWElementComponent();
		lookup.put(comp.type(),comp);
		comp = new IWApplicationComponent();
		lookup.put(comp.type(),comp);
		comp = new IWAppCompComponent();
		lookup.put(comp.type(),comp);
		comp = new IWDataComponent();
		lookup.put(comp.type(),comp);
		comp = new IWHomeComponent();
		lookup.put(comp.type(),comp);
		comp = new IWHandlerComponent();
		lookup.put(comp.type(),comp);
		comp = new IWSearchPluginComponent();
		lookup.put(comp.type(),comp);
	}
	
}
