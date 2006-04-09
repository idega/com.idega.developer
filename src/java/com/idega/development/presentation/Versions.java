/*
 * $Id: Versions.java,v 1.1 2004/07/20 15:20:06 thomas Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.development.presentation;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A class to get versions for all the bundles installed on the system.
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Versions extends Block {
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
  private Table table;

  public Versions() {
  }

  public void main(IWContext iwc){
    add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}

    IWMainApplication iwma = iwc.getIWMainApplication();
    List bundles = getRegisteredBundles(iwma);

		Text blockHeader = IWDeveloper.getText("Block");
		blockHeader.setFontColor("#0E2456");
		Text versionHeader = IWDeveloper.getText("Version");
		versionHeader.setFontColor("#0E2456");

		Package pack = Package.getPackage("com.idega.core");
		String defaultVersion = null;
		if (pack != null) {
			defaultVersion = pack.getImplementationVersion();
			if (defaultVersion == null) {
				defaultVersion = "No Implementation-Version definition for package com.idega.core in Manifest";
			}	
		}
		else {
			defaultVersion = "No Implementation-Version definition for package com.idega.core in Manifest";	
		}

    this.table = new Table(2,bundles.size() + 1);
    this.table.setWidth(1,"160");
    this.table.add(blockHeader,1,1);
    this.table.add(versionHeader,2,1);
    int row = 2;
    Iterator it = bundles.iterator();
    String bundleName = null;
    while (it.hasNext()) {
      IWBundle item = (IWBundle)it.next();
      bundleName = item.getBundleIdentifier();
      this.table.add(bundleName,1,row);
			pack = Package.getPackage(bundleName);
			String version = defaultVersion;
			if (pack != null) {
				version = pack.getImplementationVersion();
				if (version == null) {
					version = defaultVersion;
				}
			}
			else {
				version = defaultVersion;
			}
      
      this.table.add(version,2,row++);
      //table.add(defaultVersion,2,row++);
    }
    
    add(this.table);
    
//    Package packages[] = Package.getPackages();
//    for (int i = 0; i < packages.length; i++) {
//    	Package p = packages[i];
//    	System.out.println("Name : " + p.getName());	
//    }
  }

	/**
	 * Gets a List of all the bundles registered on the system.
	 * 
	 * 
	 */
  public static List getRegisteredBundles(IWMainApplication iwma){
    List bundles = iwma.getRegisteredBundles();
    Collections.sort(bundles);

    return bundles;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}