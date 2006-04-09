/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import com.idega.core.component.data.BundleComponent;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IWBaseComponent implements BundleComponent {
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#type()
	 */
	public String type() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getRequiredInterfaces()
	 */
	public Class[] getRequiredInterfaces() {
		return null;
	}
	/* (non-Javadoc)
		 * @see com.idega.development.presentation.comp.BundleComponent#getRequiredSuperClasses()
		 */
	public Class getRequiredSuperClass() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getFinalReflectionClass()
	 */
	public Class getFinalReflectionClass() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getReflectionFilters()
	 */
	public String[] getMethodStartFilters() {
		String[] filters = new String[1];
		filters[0] = "set";
		return filters;
	}
	public boolean validateInterfaces(Class validatingClass) {
		Class[] requiredInterfaces = this.getRequiredInterfaces();
		boolean returner = false;
		if (requiredInterfaces != null) {
			Class[] implementedInterfaces = validatingClass.getInterfaces();
			for (int i = 0; i < requiredInterfaces.length; i++) {
				//System.out.println("checking req "+requiredInterfaces[i].getName());
				for (int j = 0; j < implementedInterfaces.length; j++) {
					//System.out.println("checking imp"+implementedInterfaces[i].getName());
					if (requiredInterfaces[i].getName().equals(implementedInterfaces[i].getName())) {
						returner = true;
					}
				}
				// if we don't have a match after for this round we exit
				if (!returner) {
					return returner;
				}
			}
		}
		else{
			return true;
		}
		return returner;
	}
	
	public boolean validateSuperClasses(Class validatingClass) {
		if(getRequiredSuperClass()==null){
			return true;
		}
		return validatingClass.isAssignableFrom(getRequiredSuperClass());
	}
}
