/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.search.business.SearchPlugin;

/**
 * Adds the iw.searchplugin type
 * @author eiki
 * @version 1.0
 */
public class IWSearchPluginComponent extends IWBaseComponent implements BundleComponent {
	
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#type()
	 */
	public String type() {
		return ICObjectBMPBean.COMPONENT_TYPE_SEARCH_PLUGIN;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.component.data.BundleComponent#getRequiredInterfaces()
	 */
	public Class[] getRequiredInterfaces() {
		Class[] array= new Class[1];
		array[0] = SearchPlugin.class;
		return array;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.component.data.BundleComponent#getMethodStartFilters()
	 */
	public String[] getMethodStartFilters() {
		// TODO Auto-generated method stub
		return null;
	}

}
