package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
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
	private static final String DEBUG_PARAMETER = "iw_d_p";

	public ApplicationPropertySetter() {
	}

	public void main(IWContext iwc) {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");

		doBusiness(iwc);

		IWMainApplication iwma = iwc.getApplication();
		//DropdownMenu bundles = getRegisteredBundlesDropdown(iwma, APPLICATION_SETTER_PARAMETER);

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(2, 11);
		table.setCellpadding(5);
		table.mergeCells(1, 1, 2, 1);
		table.mergeCells(1, 11, 2, 11);
		table.setAlignment(1, 11, "right");
		form.add(table);
		TextInput name = new TextInput(this.PROPERTY_KEY_NAME_PARAMETER);
		TextInput value = new TextInput(this.PROPERTY_VALUE_PARAMETER);

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
		if (iwma.getSettings().isAutoCreateStringsActive()) {
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

		DropdownMenu menu = new DropdownMenu(Page.MARKUP_LANGUAGE);
		menu.addMenuElement(Page.HTML, "HTML 4.01");
		menu.addMenuElement(Page.XHTML, "XHTML 1.0");
		menu.addMenuElement(Page.XHTML1_1, "XHTML 1.1 (Experimental)");
		menu.setSelectedElement(iwc.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML));
		table.add(IWDeveloper.getText("Markup Language:"), 1, 10);
		table.add(menu, 2, 10);

		table.add(new SubmitButton("Save", APPLICATION_SETTER_PARAMETER, "save"), 1, 11);
		table.add(new SubmitButton("Store Application state", APPLICATION_SETTER_PARAMETER, "store"), 1, 11);

		add(getParametersTable(iwma));
	}

	private void doBusiness(IWContext iwc) {
		String[] values = iwc.getParameterValues("property");
		if (values != null) {
			for (int a = 0; a < values.length; a++) {
				iwc.getApplication().getSettings().removeProperty(values[a]);
			}
		}
		String setterState = iwc.getParameter(APPLICATION_SETTER_PARAMETER);
		if (setterState != null) {
			String entityAutoCreate = iwc.getParameter(ENTITY_AUTOCREATE_PARAMETER);
			String autoCreateStrings = iwc.getParameter(AUTOCREATE_STRINGS_PARAMETER);
			String autoCreateProperties = iwc.getParameter(AUTOCREATE_PROPERTIES_PARAMETER);
			String entityBeanCache = iwc.getParameter(this.IDO_ENTITY_BEAN_CACHING_PARAMETER);
			String entityQueryCache = iwc.getParameter(this.IDO_ENTITY_QUERY_CACHING_PARAMETER);
			String debug = iwc.getParameter(DEBUG_PARAMETER);
			String KeyName = iwc.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
			String KeyValue = iwc.getParameter(this.PROPERTY_VALUE_PARAMETER);
			String markup = iwc.getParameter(Page.MARKUP_LANGUAGE);
			if (KeyName != null && KeyName.length() > 0)
				iwc.getApplication().getSettings().setProperty(KeyName, KeyValue);

			if (entityAutoCreate != null)
				iwc.getApplication().getSettings().setEntityAutoCreation(true);
			else
				iwc.getApplication().getSettings().setEntityAutoCreation(false);

			if (entityBeanCache != null)
				iwc.getApplication().getSettings().setEntityBeanCaching(true);
			else
				iwc.getApplication().getSettings().setEntityBeanCaching(false);

			if (entityQueryCache != null)
				iwc.getApplication().getSettings().setEntityQueryCaching(true);
			else
				iwc.getApplication().getSettings().setEntityQueryCaching(false);

			if (autoCreateStrings != null) {
				iwc.getApplication().getSettings().setAutoCreateStrings(true);
			}
			else
				iwc.getApplication().getSettings().setAutoCreateStrings(false);

			if (autoCreateProperties != null) {
				iwc.getApplication().getSettings().setAutoCreateProperties(true);
			}
			else
				iwc.getApplication().getSettings().setAutoCreateProperties(false);

			if (debug != null) {
				iwc.getApplication().getSettings().setDebug(true);
			}
			else {
				iwc.getApplication().getSettings().setDebug(false);
			}

			if (setterState.equalsIgnoreCase("store")) {
				iwc.getApplication().storeStatus();
			}
			iwc.getApplicationSettings().setProperty(Page.MARKUP_LANGUAGE, markup);

			add(IWDeveloper.getText("Status: "));
			add("Property set successfully");
		}
	}

	public static Form getParametersTable(IWMainApplication iwma) {
		java.util.Iterator iter = iwma.getSettings().getIWPropertyListIterator();

		Form form = new Form();
		Table table = new Table();

		String value;
		IWProperty property;
		int row = 1;
		while (iter.hasNext()) {
			property = (IWProperty) iter.next();
			table.add(new Text(property.getName(), true, false, false), 1, row);
			value = property.getValue();
			if (value != null)
				table.add(new Text(value, true, false, false), 2, row);
			table.add(new CheckBox("property", property.getName()), 3, row);
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
