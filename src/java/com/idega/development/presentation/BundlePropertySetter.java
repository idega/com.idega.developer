package com.idega.development.presentation;

import java.util.Arrays;
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
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
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

public class BundlePropertySetter extends Block {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	private static final String BUNDLE_PARAMETER = "iw_b_p_s";
	private static final String PROPERTY_KEY_NAME_PARAMETER = "iw_b_p_s_k";
	private static final String PROPERTY_VALUE_PARAMETER = "iw_b_p_s_v";

	public BundlePropertySetter() {
	}

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("bundlePropertySetter");
		add(topLayer);
		
		IWMainApplication iwma = iwc.getIWMainApplication();

		String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);
		IWBundle bundle = null;
		if (bundleIdentifier != null) {
			bundle = iwma.getBundle(bundleIdentifier);
		}

		boolean saved = false;
		boolean deleted = false;
		if (iwc.isParameterSet("Delete")) {
			String[] values = iwc.getParameterValues("property");
			if (values != null && bundle != null) {
				for (int a = 0; a < values.length; a++) {
					bundle.removeProperty(values[a]);
				}
				bundle.storeState(false);
				deleted = true;
			}
		}
		if ((bundleIdentifier != null) && iwc.isParameterSet("Save")) {
			String KeyName = iwc.getParameter(BundlePropertySetter.PROPERTY_KEY_NAME_PARAMETER);
			String KeyValue = iwc.getParameter(BundlePropertySetter.PROPERTY_VALUE_PARAMETER);
			if (KeyName != null && KeyName.length() > 0){
				bundle.setProperty(KeyName, KeyValue);
				bundle.storeState(false);
				saved = true;
			}
			bundle.storeState(false);
			
			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("statusLayer");
			topLayer.add(layer);
			
			layer.add(new Text("Property set successfully and saved to files"));
		}
		else if ((bundleIdentifier != null) && !iwc.isParameterSet("Save")) {
			if (iwc.isParameterSet("Reload")) {
				iwma.getBundle(bundleIdentifier).reloadBundle();

				Layer layer = new Layer(Layer.DIV);
				layer.setStyleClass("statusLayer");
				topLayer.add(layer);
				
				layer.add(new Text("Bundle reloaded from files"));
			}
		}

		FieldSet fieldSet = new FieldSet("Create bundle property");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		fieldSet.add(form);

		DropdownMenu bundles = getRegisteredBundlesDropdown(iwma, BUNDLE_PARAMETER);
		bundles.keepStatusOnAction();
		bundles.setToSubmit();

		TextInput name = new TextInput(BundlePropertySetter.PROPERTY_KEY_NAME_PARAMETER);
		if (!saved && !deleted) {
			name.keepStatusOnAction(true);
		}
		
		TextInput value = new TextInput(BundlePropertySetter.PROPERTY_VALUE_PARAMETER);
		if (!saved && !deleted) {
			value.keepStatusOnAction(true);
		}

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Bundle", bundles);
		formItem.add(label);
		formItem.add(bundles);
		form.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Property Key Name", name);
		formItem.add(label);
		formItem.add(name);
		form.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Property Key Value", value);
		formItem.add(label);
		formItem.add(value);
		form.add(formItem);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton save = new SubmitButton("Save", "save");
		save.setStyleClass("button");
		save.setID("save");

		SubmitButton reload = new SubmitButton("Reload", "reload");
		reload.setStyleClass("button");
		reload.setID("reload");

		buttonLayer.add(save);
		buttonLayer.add(reload);

		if (bundleIdentifier != null) {
			FieldSet keySet = new FieldSet(new Legend("Available keys"));
			keySet.setStyleClass("keySet");
			topLayer.add(keySet);

			Paragraph paragraph = new Paragraph();
			paragraph.add(new Text("Available Keys"));
			keySet.add(paragraph);
			keySet.add(getParametersTable(bundle, bundleIdentifier));
		}
	}

	public static DropdownMenu getRegisteredBundlesDropdown(IWMainApplication iwma, String name) {
		List bundles = iwma.getRegisteredBundles();
		Collections.sort(bundles);
		DropdownMenu down = new DropdownMenu(name);
		Iterator iter = bundles.iterator();
		while (iter.hasNext()) {
			IWBundle item = (IWBundle) iter.next();
			down.addMenuElement(item.getBundleIdentifier());
		}
		return down;
	}

	public static Form getParametersTable(IWBundle bundle, String bundleIdentifier) {
		String[] strings = bundle.getAvailableProperties();
		List list = Arrays.asList(strings);
		Collections.sort(list);

		Form form = new Form();
		form.add(new HiddenInput(BUNDLE_PARAMETER, bundleIdentifier));
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		
		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		form.add(table);
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.add(new Text("Property key"));

		cell = row.createHeaderCell();
		cell.add(new Text("Property value"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Delete"));

		group = table.createBodyRowGroup();
		
		int i = 0;

		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			row = group.createRow();
			
			String name = (String) iter.next();
			String localizedString = bundle.getProperty(strings[i]);
			if (localizedString == null) {
				localizedString = "";
			}

			Link link = new Link(name);
			link.addParameter(BundlePropertySetter.PROPERTY_KEY_NAME_PARAMETER, name);
			link.addParameter(BundlePropertySetter.PROPERTY_VALUE_PARAMETER, localizedString);
			link.addParameter(BUNDLE_PARAMETER, bundleIdentifier);
			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(link);

			cell = row.createCell();
			cell.add(new Text(localizedString));

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(new CheckBox("property", strings[i]));

			i++;

			if (i % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton delete = new SubmitButton("Delete", "delete");
		delete.setStyleClass("button");
		delete.setID("delete");

		buttonLayer.add(delete);

		return form;
	}

	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

}
