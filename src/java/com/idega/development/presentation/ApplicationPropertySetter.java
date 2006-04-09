package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
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

	public ApplicationPropertySetter() {
		// empty
	}

	public void main(IWContext iwc) {
		//add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}

		doBusiness(iwc);

		IWMainApplication iwma = iwc.getIWMainApplication();
		//DropdownMenu bundles = getRegisteredBundlesDropdown(iwma, APPLICATION_SETTER_PARAMETER);

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(2, 12);
		table.setCellpadding(5);
		table.mergeCells(1, 1, 2, 1);
		table.mergeCells(1, 12, 2, 12);
		table.setAlignment(1, 12, "right");
		form.add(table);
		TextInput name = new TextInput(PROPERTY_KEY_NAME_PARAMETER);
		TextInput value = new TextInput(PROPERTY_VALUE_PARAMETER);

		table.add(IWDeveloper.getText("Set ApplicationProperty"), 1, 1);

		table.add(IWDeveloper.getText("Property Key Name:"), 1, 2);
		table.add(name, 2, 2);
		table.add(IWDeveloper.getText("Property Key Value:"), 1, 3);
		table.add(value, 2, 3);

		CheckBox box = new CheckBox(ENTITY_AUTOCREATE_PARAMETER);
		if (iwma.getSettings().getIfEntityAutoCreate()) {
			box.setChecked(true);
		}
		table.add(IWDeveloper.getText("Autocreate Data Entities:"), 1, 4);
		table.add(box, 2, 4);

		CheckBox box3 = new CheckBox(AUTOCREATE_STRINGS_PARAMETER);
		if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
			box3.setChecked(true);
		}
		table.add(IWDeveloper.getText("Autocreate Localized Strings:"), 1, 5);
		table.add(box3, 2, 5);

		CheckBox box4 = new CheckBox(AUTOCREATE_PROPERTIES_PARAMETER);
		if (iwma.getSettings().isAutoCreatePropertiesActive()) {
			box4.setChecked(true);
		}
		table.add(IWDeveloper.getText("Autocreate Properties:"), 1, 6);
		table.add(box4, 2, 6);

		CheckBox box2 = new CheckBox(DEBUG_PARAMETER);
		if (iwma.getSettings().getIfDebug()) {
			box2.setChecked(true);
		}
		table.add(IWDeveloper.getText("Debug:"), 1, 7);
		table.add(box2, 2, 7);

		CheckBox box6 = new CheckBox(IDO_ENTITY_BEAN_CACHING_PARAMETER);
		if (iwma.getSettings().getIfEntityBeanCaching()) {
			box6.setChecked(true);
		}
		table.add(IWDeveloper.getText("Entity Bean caching:"), 1, 8);
		table.add(box6, 2, 8);

		CheckBox box7 = new CheckBox(IDO_ENTITY_QUERY_CACHING_PARAMETER);
		if (iwma.getSettings().getIfEntityQueryCaching()) {
			box7.setChecked(true);
		}
		table.add(IWDeveloper.getText("Entity Query caching:"), 1, 9);
		table.add(box7, 2, 9);
		
		CheckBox box8 = new CheckBox(IDO_USE_PREPARED_STATEMENT);
		if (iwma.getSettings().getIfUsePreparedStatement()) {
			box8.setChecked(true);
		}
		table.add(IWDeveloper.getText("Prepared statement:"), 1, 10);
		table.add(box8, 2, 10);

		DropdownMenu menu = new DropdownMenu(IWMainApplicationSettings.DEFAULT_MARKUP_LANGUAGE_KEY);
		menu.addMenuElement(Page.HTML, "HTML 4.01");
		menu.addMenuElement(Page.XHTML, "XHTML 1.0");
		menu.addMenuElement(Page.XHTML1_1, "XHTML 1.1 (Experimental)");
		menu.setSelectedElement(iwc.getApplicationSettings().getDefaultMarkupLanguage());
		table.add(IWDeveloper.getText("Markup Language:"), 1, 11);
		table.add(menu, 2, 11);

		table.add(new SubmitButton("Save", APPLICATION_SETTER_PARAMETER, "save"), 1, 12);
		table.add(new SubmitButton("Store Application state", APPLICATION_SETTER_PARAMETER, "store"), 1, 12);

		add(getParametersTable(iwma));
	}

	private void doBusiness(IWContext iwc) {
		String[] values = iwc.getParameterValues("property");
		if (values != null) {
			for (int a = 0; a < values.length; a++) {
				iwc.getApplicationSettings().removeProperty(values[a]);
			}
		}
		String setterState = iwc.getParameter(APPLICATION_SETTER_PARAMETER);
		if (setterState != null) {
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

			add(IWDeveloper.getText("Status: "));
			add("Property set successfully");
		}
	}

	public static Form getParametersTable(IWMainApplication iwma) {
		IWMainApplicationSettings applicationSettings  = iwma.getSettings();
		java.util.Iterator iter = applicationSettings.keySet().iterator();

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		Table table = new Table();

		String value;
		String key;
		int row = 1;
		while (iter.hasNext()) {
			key = (String) iter.next();
			table.add(new Text(key, true, false, false), 1, row);
			value = applicationSettings.getProperty(key);
			if (value != null) {
				table.add(new Text(value, true, false, false), 2, row);
			}
			table.add(new CheckBox("property", key), 3, row);
			row++;
		}
		/*
		for (int i = 0; i < strings.length; i++) {
		  name = new Text(strings[i],true,false,false);
		  table.add(name,1,i+1);
		  localizedString = bundle.getProperty( strings[i] );
		  if (localizedString==null) localizedString = "";
		  table.add(localizedString ,2,i+1);
		}
		*/
		table.setColumnVerticalAlignment(1, "top");
		table.setCellpadding(5);
		table.setCellspacing(0);
		table.setWidth(400);
		table.setColor("#9FA9B3");
		table.setRowColor(table.getRows() + 1, "#FFFFFF");
		table.add(new SubmitButton("Delete", "mode", "delete"), 3, table.getRows());
		table.setColumnAlignment(3, "center");
		form.add(table);

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
