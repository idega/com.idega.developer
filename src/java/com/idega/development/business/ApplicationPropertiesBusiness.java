/**
 * 
 */
package com.idega.development.business;


/**
 * <p>
 * TODO laddi Describe Type ApplicationPropertiesBusiness
 * </p>
 *  Last modified: $Date: 2008/10/24 07:05:38 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface ApplicationPropertiesBusiness {

	public String getProperty(String key);
	
	public int setProperty(String key, String value);
	
	public void removeProperty(String key);
}