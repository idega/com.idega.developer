/**
 * 
 */
package com.idega.developer;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.util.CoreConstants;


/**
 * <p>
 * TODO tryggvil Describe Type IWBundleStarter
 * </p>
 *  Last modified: $Date: 2006/02/22 18:16:54 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWBundleStarter implements IWBundleStartable{

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		DeveloperViewManager viewMan = DeveloperViewManager.getInstance(starterBundle.getApplication());
		viewMan.getDeveloperViewNode();
		
		starterBundle.getApplication().getSettings().getBoolean(
				CoreConstants.DEVELOPEMENT_STATE_PROPERTY, false);
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		// TODO Auto-generated method stub
		
	}
	
}
