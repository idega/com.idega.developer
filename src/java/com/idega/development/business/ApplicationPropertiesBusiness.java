/**
 *
 */
package com.idega.development.business;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * TODO laddi Describe Type ApplicationPropertiesBusiness
 * </p>
 *  Last modified: $Date: 2009/01/23 15:19:19 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationPropertiesBusiness {

	public boolean doesPropertyExist(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context);

	public String getProperty(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context);

	public int setProperty(String key, String value, HttpServletRequest request, HttpServletResponse response, ServletContext context);

	public void removeProperty(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context);

}