/**
 * 
 */
package com.idega.development.business;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;


/**
 * <p>
 * TODO laddi Describe Type ApplicationPropertiesBusinessBean
 * </p>
 *  Last modified: $Date: 2009/01/23 15:19:19 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
@Scope("singleton")
@Service("applicationProperties")
public class ApplicationPropertiesBusinessBean implements ApplicationPropertiesBusiness {

	/* (non-Javadoc)
	 * @see com.idega.development.business.ApplicationPropertiesBusiness#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {
		return getIWMainApplication().getSettings().getProperty(key, "");
	}
	
	public boolean doesPropertyExist(String key) {
		return getIWMainApplication().getSettings().keySet().contains(key);
	}

	/* (non-Javadoc)
	 * @see com.idega.development.business.ApplicationPropertiesBusiness#setProperty(java.lang.String, java.lang.String)
	 */
	public int setProperty(String key, String value) {
		if (key.equals(IWMainApplicationSettings.ENTITY_AUTO_CREATE)) {
			getIWMainApplication().getSettings().setEntityAutoCreation(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.IDO_ENTITY_BEAN_CACHING_KEY)) {
			getIWMainApplication().getSettings().setEntityBeanCaching(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.IDO_ENTITY_QUERY_CACHING_KEY)) {
			getIWMainApplication().getSettings().setEntityQueryCaching(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.SESSION_POLLING_KEY)) {
			getIWMainApplication().getSettings().setEnableSessionPolling(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.USE_PREPARED_STATEMENT)) {
			getIWMainApplication().getSettings().setUsePreparedStatement(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.AUTO_CREATE_LOCALIZED_STRINGS_KEY)) {
			getIWMainApplication().getSettings().setAutoCreateStrings(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.AUTO_CREATE_PROPERTIES_KEY)) {
			getIWMainApplication().getSettings().setAutoCreateProperties(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.USE_DEBUG_MODE)) {
			getIWMainApplication().getSettings().setDebug(value != null);
		}
		else {
			getIWMainApplication().getSettings().setProperty(key, value);
		}
		getIWMainApplication().storeStatus();

		ArrayList<String> keys = new ArrayList(getIWMainApplication().getSettings().keySet());
		return keys.indexOf(key);
	}

	/* (non-Javadoc)
	 * @see com.idega.development.business.ApplicationPropertiesBusiness#removeProperty(java.lang.String)
	 */
	public void removeProperty(String key) {
		getIWMainApplication().getSettings().removeProperty(key);
		getIWMainApplication().storeStatus();
	}
	
	private IWMainApplication getIWMainApplication() {
		final IWContext iwc = IWContext.getCurrentInstance();
		final IWMainApplication iwma;
		
		if(iwc != null)
			iwma = iwc.getIWMainApplication();
		else
			iwma = IWMainApplication.getDefaultIWMainApplication();
		
		return iwma;
	}
}