package com.idega.development.presentation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class BundleCreator extends Block {

	private static final String NEW_BUNDLE_PARAMETER = "iw_b_i";
	private static final String NEW_BUNDLE_NAME_PARAMETER = "iw_b_i_n_b_n";
	//private static final String NEW_BUNDLE_PATH_PARAMETER = "iw_b_i_n_b_p";

	public BundleCreator() {
	}

	public void main(IWContext iwc) throws Exception {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(2, 4);
		table.setAlignment(2, 3, "right");
		form.add(table);
		TextInput name = new TextInput(NEW_BUNDLE_NAME_PARAMETER);
		//TextInput path = new TextInput(this.NEW_BUNDLE_PATH_PARAMETER);

		table.add(IWDeveloper.getText("Create New Bundle"), 1, 1);
		table.add(IWDeveloper.getText("Bundle Identifier"), 1, 2);
		table.add(name, 2, 2);
		//table.add("Bundle Directory Name",2,2);
		//table.add(path,2,2);
		table.add(new Parameter(NEW_BUNDLE_PARAMETER, "dummy"));
		table.add(new SubmitButton("Create", NEW_BUNDLE_PARAMETER, "save"), 2, 3);

		doBusiness(iwc);

		table.add(getRegisteredBundles(iwc), 1, 4);
		table.mergeCells(1, 4, 2, 4);
	}

	private Table getRegisteredBundles(IWContext iwc) {
		Table T = new Table();
		int row = 1;
		T.add(IWDeveloper.getText("Registered bundles:"), 1, row++);
		List bundles = iwc.getIWMainApplication().getRegisteredBundles();
		Collections.sort(bundles);

		Iterator iter = bundles.iterator();
		while (iter.hasNext()) {
			IWBundle item = (IWBundle) iter.next();
			T.add(item.getBundleIdentifier(), 1, row++);
		}
		return T;
	}

	private void doBusiness(IWContext iwc) throws Exception {
		String check = iwc.getParameter(NEW_BUNDLE_PARAMETER);
		if (check != null) {
			String bundleIdentifier = iwc.getParameter(NEW_BUNDLE_NAME_PARAMETER);
			//String bundleDir = iwc.getParameter(this.NEW_BUNDLE_PATH_PARAMETER);
			//String bundleDir = bundleIdentifier + ".bundle";
			IWMainApplication iwma = iwc.getIWMainApplication();
			//if (bundleDir.indexOf(IWMainApplication.BUNDLES_STANDARD_DIRECTORY) == -1) {
				//bundleDir = IWMainApplication.BUNDLES_STANDARD_DIRECTORY + File.separator + bundleDir;
				//bundleDir = iwma.getBundlesRealPath() + File.separator + bundleDir;
			//}
			iwma.registerBundle(bundleIdentifier, true);
			IWBundle iwb = iwma.getBundle(bundleIdentifier);
			iwb.storeState();
			
			add(IWDeveloper.getText("Creation Successful"));
		}
	}
}
