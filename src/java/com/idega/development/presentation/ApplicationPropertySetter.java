package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
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
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
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

public class ApplicationPropertySetter extends Block {

	private static final String APPLICATION_SETTER_PARAMETER = "iw_a_p_s";
	private static final String PROPERTY_KEY_NAME_PARAMETER = "iw_a_p_s_k";
	private static final String PROPERTY_VALUE_PARAMETER = "iw_a_p_s_v";
	private static final String ENTITY_AUTOCREATE_PARAMETER = "iw_e_a_c_p";
	private static final String AUTOCREATE_STRINGS_PARAMETER = "iw_a_c_s_p";
	private static final String AUTOCREATE_PROPERTIES_PARAMETER = "iw_a_c_p_p";
	private static final String IDO_ENTITY_BEAN_CACHING_PARAMETER = "iw_e_b_c_p";
	private static final String IDO_ENTITY_QUERY_CACHING_PARAMETER = "iw_e_q_c_p";
	private static final String IDO_USE_PREPARED_STATEMENT = "iw_a_u_p_s";
	private static final String DEBUG_PARAMETER = "iw_d_p";
	private static final String SESSION_POLLING_PARAMETER = "iw_s_p_p";

	public ApplicationPropertySetter() {
		// empty
	}

	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		getParentPage().addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/developer.css"));

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("applicationPropertySetter");
		add(topLayer);

		doBusiness(iwc, topLayer);
		
		IWMainApplication iwma = iwc.getIWMainApplication();

		FieldSet fieldSet = new FieldSet("Create application property");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		fieldSet.add(form);

		boolean keepValues = true;
		if (iwc.isParameterSet(APPLICATION_SETTER_PARAMETER)) {
			if (iwc.getParameter(APPLICATION_SETTER_PARAMETER).equals("store")) {
				keepValues = false;
			}
		}

		TextInput name = new TextInput(PROPERTY_KEY_NAME_PARAMETER);
		name.keepStatusOnAction(keepValues);

		TextInput value = new TextInput(PROPERTY_VALUE_PARAMETER);
		value.keepStatusOnAction(keepValues);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Property Key Name", name);
		formItem.add(label);
		formItem.add(name);
		form.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Property Key Value", value);
		formItem.add(label);
		formItem.add(value);
		form.add(formItem);

		DropdownMenu menu = new DropdownMenu(IWMainApplicationSettings.DEFAULT_MARKUP_LANGUAGE_KEY);
		menu.addMenuElement(Page.HTML, "HTML 4.01");
		menu.addMenuElement(Page.XHTML, "XHTML 1.0");
		menu.addMenuElement(Page.XHTML1_1, "XHTML 1.1 (Experimental)");
		menu.setSelectedElement(iwc.getApplicationSettings().getDefaultMarkupLanguage());

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Markup Language", menu);
		formItem.add(label);
		formItem.add(menu);
		form.add(formItem);

		CheckBox box = new CheckBox(ENTITY_AUTOCREATE_PARAMETER);
		if (iwma.getSettings().getIfEntityAutoCreate()) {
			box.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Autocreate Data Entities", box);
		formItem.add(box);
		formItem.add(label);
		form.add(formItem);

		CheckBox box3 = new CheckBox(AUTOCREATE_STRINGS_PARAMETER);
		if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
			box3.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Autocreate Localized Strings", box3);
		formItem.add(box3);
		formItem.add(label);
		form.add(formItem);

		CheckBox box4 = new CheckBox(AUTOCREATE_PROPERTIES_PARAMETER);
		if (iwma.getSettings().isAutoCreatePropertiesActive()) {
			box4.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Autocreate Properties", box4);
		formItem.add(box4);
		formItem.add(label);
		form.add(formItem);

		CheckBox box2 = new CheckBox(DEBUG_PARAMETER);
		if (iwma.getSettings().getIfDebug()) {
			box2.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Debug", box2);
		formItem.add(box2);
		formItem.add(label);
		form.add(formItem);

		CheckBox box6 = new CheckBox(IDO_ENTITY_BEAN_CACHING_PARAMETER);
		if (iwma.getSettings().getIfEntityBeanCaching()) {
			box6.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Entity Bean caching", box6);
		formItem.add(box6);
		formItem.add(label);
		form.add(formItem);

		CheckBox box7 = new CheckBox(IDO_ENTITY_QUERY_CACHING_PARAMETER);
		if (iwma.getSettings().getIfEntityQueryCaching()) {
			box7.setChecked(true);
		}
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Entity Query caching", box7);
		formItem.add(box7);
		formItem.add(label);
		form.add(formItem);

		CheckBox box8 = new CheckBox(IDO_USE_PREPARED_STATEMENT);
		if (iwma.getSettings().getIfUsePreparedStatement()) {
			box8.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Prepared statement", box8);
		formItem.add(box8);
		formItem.add(label);
		form.add(formItem);
		
		CheckBox box9 = new CheckBox(SESSION_POLLING_PARAMETER);
		if (iwma.getSettings().getIfUseSessionPolling()) {
			box9.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Enable session polling", box8);
		formItem.add(box9);
		formItem.add(label);
		form.add(formItem);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton save = new SubmitButton("Save", APPLICATION_SETTER_PARAMETER, "save");
		save.setStyleClass("button");
		save.setID("save");

		SubmitButton reload = new SubmitButton("Store Application state", APPLICATION_SETTER_PARAMETER, "store");
		reload.setStyleClass("button");
		reload.setID("reload");

		buttonLayer.add(save);
		buttonLayer.add(reload);

		FieldSet keySet = new FieldSet(new Legend("Available keys"));
		keySet.setStyleClass("keySet");
		topLayer.add(keySet);

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text("Available Keys"));
		keySet.add(paragraph);
		keySet.add(getParametersTable(iwma));
	}

	private void doBusiness(IWContext iwc, Layer topLayer) {
		String[] values = iwc.getParameterValues("property");
		if (values != null) {
			for (int a = 0; a < values.length; a++) {
				iwc.getApplicationSettings().removeProperty(values[a]);
			}
		}
		String setterState = iwc.getParameter(APPLICATION_SETTER_PARAMETER);
		if (setterState != null) {
			String enableSessionPolling = iwc.getParameter(SESSION_POLLING_PARAMETER);
			String entityAutoCreate = iwc.getParameter(ENTITY_AUTOCREATE_PARAMETER);
			String autoCreateStrings = iwc.getParameter(AUTOCREATE_STRINGS_PARAMETER);
			String autoCreateProperties = iwc.getParameter(AUTOCREATE_PROPERTIES_PARAMETER);
			String entityBeanCache = iwc.getParameter(IDO_ENTITY_BEAN_CACHING_PARAMETER);
			String entityQueryCache = iwc.getParameter(IDO_ENTITY_QUERY_CACHING_PARAMETER);
			String usePreparedStatement = iwc.getParameter(IDO_USE_PREPARED_STATEMENT);
			String debug = iwc.getParameter(DEBUG_PARAMETER);
			String KeyName = iwc.getParameter(PROPERTY_KEY_NAME_PARAMETER);
			String KeyValue = iwc.getParameter(PROPERTY_VALUE_PARAMETER);
			String markup = iwc.getParameter(IWMainApplicationSettings.DEFAULT_MARKUP_LANGUAGE_KEY);
			if (KeyName != null && KeyName.length() > 0) {
				iwc.getIWMainApplication().getSettings().setProperty(KeyName, KeyValue);
			}

			if (entityAutoCreate != null) {
				iwc.getIWMainApplication().getSettings().setEntityAutoCreation(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setEntityAutoCreation(false);
			}

			if (entityBeanCache != null) {
				iwc.getIWMainApplication().getSettings().setEntityBeanCaching(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setEntityBeanCaching(false);
			}
			if(enableSessionPolling != null) {
				iwc.getIWMainApplication().getSettings().setEnableSessionPolling(true);
			} else {
				iwc.getIWMainApplication().getSettings().setEnableSessionPolling(false);
			}
			if (entityQueryCache != null) {
				iwc.getIWMainApplication().getSettings().setEntityQueryCaching(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setEntityQueryCaching(false);
			}
			
			if (usePreparedStatement != null) {
				iwc.getIWMainApplication().getSettings().setUsePreparedStatement(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setUsePreparedStatement(false);
			}

			if (autoCreateStrings != null) {
				iwc.getIWMainApplication().getSettings().setAutoCreateStrings(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setAutoCreateStrings(false);
			}

			if (autoCreateProperties != null) {
				iwc.getIWMainApplication().getSettings().setAutoCreateProperties(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setAutoCreateProperties(false);
			}

			if (debug != null) {
				iwc.getIWMainApplication().getSettings().setDebug(true);
			}
			else {
				iwc.getIWMainApplication().getSettings().setDebug(false);
			}

			if (setterState.equalsIgnoreCase("store")) {
				iwc.getIWMainApplication().storeStatus();
			}
			iwc.getApplicationSettings().setProperty(IWMainApplicationSettings.DEFAULT_MARKUP_LANGUAGE_KEY, markup);

			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("statusLayer");
			topLayer.add(layer);
			
			layer.add(new Text("Property set successfully"));
		}
	}

	public static Form getParametersTable(IWMainApplication iwma) {
		IWMainApplicationSettings applicationSettings  = iwma.getSettings();
		java.util.Iterator iter = applicationSettings.keySet().iterator();

		Form form = new Form();
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
		while (iter.hasNext()) {
			row = group.createRow();

			String key = (String) iter.next();
			String value = applicationSettings.getProperty(key);
			if (value == null) {
				value = Text.NON_BREAKING_SPACE;
			}

			Link link = new Link(key);
			link.addParameter(PROPERTY_KEY_NAME_PARAMETER, key);
			link.addParameter(PROPERTY_VALUE_PARAMETER, value);
			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(link);

			cell = row.createCell();
			cell.add(new Text(value));

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(new CheckBox("property", key));

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

	public static DropdownMenu getRegisteredBundlesDropdown(IWMainApplication iwma, String name) {
		List locales = iwma.getRegisteredBundles();
		DropdownMenu down = new DropdownMenu(name);
		Iterator iter = locales.iterator();
		while (iter.hasNext()) {
			IWBundle item = (IWBundle) iter.next();
			down.addMenuElement(item.getBundleIdentifier());
		}
		return down;
	}
}
