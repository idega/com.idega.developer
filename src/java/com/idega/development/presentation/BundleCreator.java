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
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.PresentationUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class BundleCreator extends Block {

	private static final String NEW_BUNDLE_NAME_PARAMETER = "iw_b_i_n_b_n";

	public BundleCreator() {
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		doBusiness(iwc);

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("bundleCreator");
		add(topLayer);

		Form form = new Form();
		topLayer.add(form);

		FieldSet fieldSet = new FieldSet("Create bundle");
		form.add(fieldSet);
		
		TextInput name = new TextInput(NEW_BUNDLE_NAME_PARAMETER);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Bundle Identifier", name);
		formItem.add(label);
		formItem.add(name);
		fieldSet.add(formItem);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		fieldSet.add(buttonLayer);

		SubmitButton save = new SubmitButton("Create");
		save.setStyleClass("button");
		buttonLayer.add(save);

		FieldSet keySet = new FieldSet(new Legend("Created bundles"));
		keySet.setStyleClass("createdBundles");
		topLayer.add(keySet);

		keySet.add(getRegisteredBundles(iwc));
	}

	private Table2 getRegisteredBundles(IWContext iwc) {
		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth("100%");
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.add(new Text("Identifier"));

		cell = row.createHeaderCell();
		cell.add(new Text("Name"));

		cell = row.createHeaderCell();
		cell.add(new Text("Vendor"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Version"));

		group = table.createBodyRowGroup();

		List bundles = iwc.getIWMainApplication().getRegisteredBundles();
		Collections.sort(bundles);

		Iterator iter = bundles.iterator();
		while (iter.hasNext()) {
			row = group.createRow();
			IWBundle item = (IWBundle) iter.next();

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(new Text(item.getModuleIdentifier()));

			cell = row.createCell();
			cell.add(new Text(item.getModuleName()));

			cell = row.createCell();
			cell.add(new Text(item.getModuleVendor()));

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(new Text(item.getModuleVersion()));
		}

		return table;
	}

	private void doBusiness(IWContext iwc) throws Exception {
		if (iwc.isParameterSet(NEW_BUNDLE_NAME_PARAMETER)) {
			String bundleIdentifier = iwc.getParameter(NEW_BUNDLE_NAME_PARAMETER);
			IWMainApplication iwma = iwc.getIWMainApplication();
			iwma.registerBundle(bundleIdentifier, true);
			IWBundle iwb = iwma.getBundle(bundleIdentifier);
			iwb.storeState();
		}
	}
}