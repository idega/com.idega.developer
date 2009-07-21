package com.idega.development.presentation;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.development.business.DeveloperConstants;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Span;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
import com.idega.presentation.ui.TextInput;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ApplicationPropertySetter extends Block {

	private static final String PROPERTY_KEY_NAME_PARAMETER = "iw_a_p_s_k";
	private static final String PROPERTY_VALUE_PARAMETER = "iw_a_p_s_v";

	public ApplicationPropertySetter() {
		// empty
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = iwc.getIWMainApplication().getBundle(DeveloperConstants.BUNDLE_IDENTIFIER);
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesStyleSheet());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/ApplicationProperties.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/jquery.scrollTo-min.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/applicationProperties.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesScript());
		
		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("applicationPropertySetter");
		add(topLayer);

		//doBusiness(iwc, topLayer);
		
		IWMainApplication iwma = iwc.getIWMainApplication();

		FieldSet fieldSet = new FieldSet("Create application property");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		fieldSet.add(form);

		TextInput name = new TextInput(PROPERTY_KEY_NAME_PARAMETER);
		name.setID("applicationPropertyKey");

		TextInput value = new TextInput(PROPERTY_VALUE_PARAMETER);
		value.setID("applicationPropertyValue");

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
		menu.setID("applicationPropertyMarkupKey");

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Markup Language", menu);
		formItem.add(label);
		formItem.add(menu);
		form.add(formItem);

		CheckBox box = new CheckBox(IWMainApplicationSettings.ENTITY_AUTO_CREATE);
		box.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box3 = new CheckBox(IWMainApplicationSettings.AUTO_CREATE_LOCALIZED_STRINGS_KEY);
		box3.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box4 = new CheckBox(IWMainApplicationSettings.AUTO_CREATE_PROPERTIES_KEY);
		box4.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box2 = new CheckBox(IWMainApplicationSettings.USE_DEBUG_MODE);
		box2.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box6 = new CheckBox(IWMainApplicationSettings.IDO_ENTITY_BEAN_CACHING_KEY);
		box6.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box7 = new CheckBox(IWMainApplicationSettings.IDO_ENTITY_QUERY_CACHING_KEY);
		box7.setStyleClass("setApplicationPropertyCheck");
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

		CheckBox box8 = new CheckBox(IWMainApplicationSettings.USE_PREPARED_STATEMENT);
		box8.setStyleClass("setApplicationPropertyCheck");
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
		
		CheckBox box9 = new CheckBox(IWMainApplicationSettings.SESSION_POLLING_KEY);
		box9.setStyleClass("setApplicationPropertyCheck");
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
		
		CheckBox box10 = new CheckBox(IWMainApplicationSettings.REVERSE_AJAX_KEY);
		box10.setStyleClass("setApplicationPropertyCheck");
		if (iwma.getSettings().isReverseAjaxEnabled()) {
			box10.setChecked(true);
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("checkBoxItem");
		label = new Label("Enable reverse Ajax for ALL pages", box8);
		formItem.add(box10);
		formItem.add(label);
		form.add(formItem);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		GenericButton save = new GenericButton("Save");
		save.setStyleClass("button");
		save.setID("save");

		buttonLayer.add(save);

		FieldSet keySet = new FieldSet(new Legend("Available keys"));
		keySet.setStyleClass("keySet");
		topLayer.add(keySet);

		keySet.add(getParametersTable(iwma));
	}

	public static Table2 getParametersTable(IWMainApplication iwma) {
		IWMainApplicationSettings applicationSettings  = iwma.getSettings();
		java.util.Iterator iter = applicationSettings.keySet().iterator();

		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		
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
			link.setURL("#");
			link.setStyleClass("keyLink");
			
			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(link);

			Span span = new Span();
			span.setStyleClass("keyValue");
			span.add(new Text(value));
			
			cell = row.createCell();
			cell.add(span);

			CheckBox checkbox = new CheckBox("property", key);
			checkbox.setStyleClass("removeApplicationPropertyCheck");
			
			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(checkbox);

			i++;

			if (i % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}

		return table;
	}

	private Web2Business getWeb2Business(IWApplicationContext iwac) {
		try {
			return (Web2Business) IBOLookup.getServiceInstance(iwac, Web2Business.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
}