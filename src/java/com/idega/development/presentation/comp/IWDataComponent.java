/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.data.IDOEntity;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IWDataComponent extends IWBaseComponent implements BundleComponent {
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#type()
	 */
	public String type() {
		return ICObjectBMPBean.COMPONENT_TYPE_DATA;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getRequiredInterfaces()
	 */
	public Class[] getRequiredInterfaces() {
		Class[] array= new Class[1];
		array[0] = IDOEntity.class;
		return array;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getFinalReflectionClass()
	 */
	public Class getFinalReflectionClass() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getMethodStartFilters()
	 */
	public String[] getMethodStartFilters() {
		String[] filters = new String[2];
		filters[0] = "get";
		filters[1] = "set";
		return filters;
	}
}
