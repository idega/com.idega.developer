/**
 *
 */
package com.idega.development.business;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;


/**
 * <p>
 * </p>
 *  Last modified: $Date: 2009/01/23 15:19:19 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service("applicationProperties")
public class ApplicationPropertiesBusinessBean implements ApplicationPropertiesBusiness {

	@Override
	public String getProperty(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		IWContext iwc = getContext(request, response, context);
		if (iwc == null) {
			return null;
		}

		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		return settings.getProperty(key, CoreConstants.EMPTY);
	}

	@Override
	public boolean doesPropertyExist(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		IWContext iwc = getContext(request, response, context);
		if (iwc == null) {
			return false;
		}

		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		return settings.keySet().contains(key);
	}

	@Override
	public int setProperty(String key, String value, HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		IWContext iwc = getContext(request, response, context);
		if (iwc == null) {
			return -1;
		}

		IWMainApplicationSettings settings = iwc.getApplicationSettings();

		if (key.equals(IWMainApplicationSettings.ENTITY_AUTO_CREATE)) {
			settings.setEntityAutoCreation(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.IDO_ENTITY_BEAN_CACHING_KEY)) {
			settings.setEntityBeanCaching(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.IDO_ENTITY_QUERY_CACHING_KEY)) {
			settings.setEntityQueryCaching(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.SESSION_POLLING_KEY)) {
			settings.setEnableSessionPolling(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.USE_PREPARED_STATEMENT)) {
			settings.setUsePreparedStatement(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.AUTO_CREATE_LOCALIZED_STRINGS_KEY)) {
			settings.setAutoCreateStrings(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.AUTO_CREATE_PROPERTIES_KEY)) {
			settings.setAutoCreateProperties(value != null);
		}
		else if (key.equals(IWMainApplicationSettings.USE_DEBUG_MODE)) {
			settings.setDebug(value != null);
		}
		else {
			settings.setProperty(key, value);
		}
		iwc.getIWMainApplication().storeStatus();

		List<String> keys = new ArrayList<>(settings.keySet());
		return keys.indexOf(key);
	}

	@Override
	public void removeProperty(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		IWContext iwc = getContext(request, response, context);
		if (iwc == null) {
			return;
		}

		IWMainApplication iwma = iwc.getIWMainApplication();
		iwma.getSettings().removeProperty(key);
		iwma.storeStatus();
	}

	private IWContext getContext(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		IWContext iwc = request == null || response == null || context == null ?
				null :
				new IWContext(request, response, context);
		return iwc != null && iwc.isLoggedOn() && (iwc.isSuperAdmin() || iwc.hasRole(StandardRoles.ROLE_KEY_DEVELOPER)) ?
				iwc :
				null;
	}

}