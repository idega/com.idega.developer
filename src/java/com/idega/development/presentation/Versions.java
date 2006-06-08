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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Legend;

/**
 * A class to get versions for all the bundles installed on the system.
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Versions extends Block {

	private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";

  public Versions() {
  }

  public void main(IWContext iwc){
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		getParentPage().addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/developer.css"));

    IWMainApplication iwma = iwc.getIWMainApplication();
    List bundles = getRegisteredBundles(iwma);

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

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("versions");
		add(topLayer);

		FieldSet set = new FieldSet(new Legend("Versions"));
		set.setStyleClass("versions");
		topLayer.add(set);

		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		set.add(table);
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.add(new Text("Block"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Version"));

		group = table.createBodyRowGroup();
		
		int i = 0;

    Iterator it = bundles.iterator();
    String bundleName = null;
    while (it.hasNext()) {
			row = group.createRow();
			
      IWBundle item = (IWBundle)it.next();
      bundleName = item.getBundleIdentifier();
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

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(new Text(bundleName));

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(new Text(version));

			i++;

			if (i % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}
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