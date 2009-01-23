/**
 * 
 */
package com.idega.development.business;


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

	public boolean doesPropertyExist(String key);
	
	public String getProperty(String key);
	
	public int setProperty(String key, String value);
	
	public void removeProperty(String key);
}