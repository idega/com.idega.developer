/**
 * 
 */
package com.idega.development.business;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;


/**
 * <p>
 * TODO laddi Describe Type ApplicationPropertiesBusinessBean
 * </p>
 *  Last modified: $Date: 2008/10/24 07:05:38 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
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

	/* (non-Javadoc)
	 * @see com.idega.development.business.ApplicationPropertiesBusiness#setProperty(java.lang.String, java.lang.String)
	 */
	public int setProperty(String key, String value) {
		int index = -1;
		
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
			Collection<String> keys = getIWMainApplication().getSettings().keySet();
			if (keys.contains(key)) {
				getIWMainApplication().getSettings().setProperty(key, value);
				getIWMainApplication().storeStatus();
			}
			else {
				getIWMainApplication().getSettings().setProperty(key, value);
				getIWMainApplication().storeStatus();

				keys = getIWMainApplication().getSettings().keySet();
				int i = 0;
				for (String oldKey : keys) {
					if (oldKey.equals(key)) {
						index = i;
						break;
					}
					i++;
				}
			}
		}
		
		return index;
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