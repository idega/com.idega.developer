package com.idega.development.presentation;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.LocaleChanger;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.CoreConstants;
import com.idega.util.LocaleUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringHandler;
import com.idega.util.text.TextSoap;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Localizer extends Block {

	private static String bundlesParameter = "iw_availablebundles";
	private static String localesParameter = "iw_locales";
	private static String stringsParameter = "iw_localestrings";
	private static String areaParameter = "iw_stringsarea";
	private static String subAction = "iw_localizer_sub_action";
	private static String newStringKeyParameter = "iw_new_string_key";

	private static String ACTION_SAVE="save";
	private static String ACTION_DELETE="delete";
	
	public Localizer() {
	}

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesStyleSheet());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/Localizer.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/jquery.scrollTo-min.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/localizer.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesScript());
		
		IWMainApplication iwma = iwc.getIWMainApplication();
		
		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("localizer");
		add(topLayer);

		FieldSet fieldSet = new FieldSet("Localizer");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		fieldSet.add(form);

		DropdownMenu bundlesDrop = getRegisteredDropdown(iwma, bundlesParameter);
		bundlesDrop.setID("localizerBundle");
		bundlesDrop.keepStatusOnAction();
		bundlesDrop.setToSubmit();
		
		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, localesParameter);
		localesDrop.setID("localizerLocale");
		localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();

		DropdownMenu stringsDrop;

		String selectedBundle = iwc.getParameter(bundlesParameter);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Bundle", bundlesDrop);
		formItem.add(label);
		formItem.add(bundlesDrop);
		form.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Locale", localesDrop);
		formItem.add(label);
		formItem.add(localesDrop);
		form.add(formItem);

		if (selectedBundle != null) {
			iwb = iwma.getBundle(selectedBundle);
			IWResourceBundle iwrb = iwb.getResourceBundle(LocaleUtil.getLocale(iwc.getParameter(localesParameter)));

			TextInput newInput = new TextInput(newStringKeyParameter);
			newInput.setID("localizerNewKey");
			TextArea area = new TextArea(areaParameter);
			area.setID("localizerValue");

			stringsDrop = Localizer.getLocalizeableStringsMenu(iwma, selectedBundle, stringsParameter);
			stringsDrop.addMenuElementFirst("", "");
			stringsDrop.setID("localizerKey");

			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label("String", stringsDrop);
			formItem.add(label);
			formItem.add(stringsDrop);
			form.add(formItem);

			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label("New String key", newInput);
			formItem.add(label);
			formItem.add(newInput);
			form.add(formItem);

			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label("New String value", area);
			formItem.add(label);
			formItem.add(area);
			form.add(formItem);

			Layer buttonLayer = new Layer(Layer.DIV);
			buttonLayer.setStyleClass("buttonLayer");
			form.add(buttonLayer);

			GenericButton save = new GenericButton("Save", ACTION_SAVE);
			save.setStyleClass("button");
			save.setID("localizerSave");

			GenericButton delete = new GenericButton("Delete", ACTION_DELETE);
			delete.setStyleClass("button");
			delete.setID("localizerDelete");

			buttonLayer.add(save);
			buttonLayer.add(delete);
			
			FieldSet keySet = new FieldSet(new Legend("Available Strings"));
			keySet.setStyleClass("stringsSet");
			topLayer.add(keySet);

			keySet.add(Localizer.getLocalizeableStringsTable(iwc, iwma, selectedBundle, iwrb, stringsParameter));
		}
		else {
			Layer buttonLayer = new Layer(Layer.DIV);
			buttonLayer.setStyleClass("buttonLayer");
			form.add(buttonLayer);

			SubmitButton choose = new SubmitButton("Get available keys", subAction, "choose");
			choose.setStyleClass("button");
			choose.setID("chooseLocalizer");

			buttonLayer.add(choose);
		}
	}

	public static Form getAvailableLocalesForm(IWContext iwc) {
		IWMainApplication iwma = iwc.getIWMainApplication();

		Form myForm = new Form();
		myForm.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
		DropdownMenu down = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, LocaleChanger.localesParameter);
		down.keepStatusOnAction();
		down.setToSubmit();
		myForm.add(down);

		return myForm;
	}

	public static Table2 getLocalizeableStringsTable(IWContext iwc, IWMainApplication iwma, String bundleIdentifier, IWResourceBundle iwrb, String parameterName) {
		IWBundle bundle = iwma.getBundle(bundleIdentifier);
		String[] strings = bundle.getLocalizableStrings();
		
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
		cell.add(new Text("Key"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("String"));

		group = table.createBodyRowGroup();
		
		for (int i = 0; i < strings.length; i++) {
			String key = strings[i];

			row = group.createRow();

			cell = row.createCell();
			cell.setStyleClass("firstColumn");

			Link keyLink = new Link(key);
			keyLink.setURL("#");
			keyLink.setStyleClass("keyLink");
			cell.add(keyLink);

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			String localizedString = iwrb.getLocalizedString(key);
			if (localizedString == null || StringHandler.EMPTY_STRING.equals(localizedString)){
				String defaultString = bundle.getLocalizableStringDefaultValue(key);
				defaultString = TextSoap.formatText(defaultString);
				localizedString = defaultString;
				cell.setStyleClass("isEmpty");
			}
			else{
				localizedString = TextSoap.formatText(localizedString);
			}
			cell.add(new Text(localizedString));

			if (i % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}

		return table;
	}

	public static DropdownMenu getRegisteredDropdown(IWMainApplication iwma, String name) {
		return BundlePropertySetter.getRegisteredBundlesDropdown(iwma, name);
	}

	public static DropdownMenu getLocalizeableStringsMenu(IWMainApplication iwma, String bundleIdentifier, String name) {
		IWBundle bundle = iwma.getBundle(bundleIdentifier);
		String[] strings = bundle.getLocalizableStrings();
		DropdownMenu down = new DropdownMenu(name);
		for (int i = 0; i < strings.length; i++) {
			down.addMenuElement(strings[i]);
		}
		return down;
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