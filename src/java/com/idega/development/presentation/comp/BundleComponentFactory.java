/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import java.util.HashMap;
import java.util.Map;
import com.idega.core.data.ICObject;

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
	}
	
}
