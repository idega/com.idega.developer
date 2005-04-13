/*
 * $Id: OwnerGroupImage.java,v 1.1 2005/04/13 18:06:00 gummi Exp $
 * Created on 12.4.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.development.presentation;

import javax.ejb.FinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;


/**
 * 
 *  Last modified: $Date: 2005/04/13 18:06:00 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class OwnerGroupImage extends Image {

	/**
	 * 
	 */
	public OwnerGroupImage() {
		super();
	}

	
	public void main(IWContext iwc) {
		Page page = this.getParentPage();
		if(page != null) {
			int rootPageID = page.getDynamicPageTrigger().getRootPage();;
			if(rootPageID != -1) {
				try {
					Group gr = ((GroupHome)IDOLookup.getHome(Group.class)).findByHomePageID(rootPageID);
					
					String sImageId = gr.getMetaData("group_image");
					try {
						int imageID = Integer.parseInt(sImageId);
						setImageID(imageID);
					} catch (NumberFormatException e){
						System.err.println("Group image is "+sImageId+" but not Integer for page("+rootPageID+"): "+page.getName());
					} catch (NullPointerException e){
						System.err.println("Group image is not set for page("+rootPageID+"): "+page.getName());
					}
				} catch (FinderException e) {
					// No Group found
					System.out.println("["+this.getClassName()+"]: no Group has this page("+rootPageID+") as homepage");
				}
				catch (IDOLookupException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
