package com.idega.development.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.presentation.LocaleChanger;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Span;
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
import com.idega.util.expression.ELUtil;
import com.idega.util.messages.MessageResource;
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
	private static String storageParameter = "iw_available_storage_resources";
	private static String stringsParameter = "iw_localestrings";
	private static String areaParameter = "iw_stringsarea";
	private static String subAction = "iw_localizer_sub_action";
	private static String newStringKeyParameter = "iw_new_string_key";

	private static String ACTION_SAVE="save";
	private static String ACTION_DELETE="delete";

	public static String ALL_RESOURCES = "All";

	public Localizer() {
	}

	@Autowired
	private JQuery jQuery;

	@Override
	public void main(IWContext iwc) throws Exception {
		ELUtil.getInstance().autowire(this);

		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesStyleSheet());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, jQuery.getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/Localizer.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/jquery.scrollTo-min.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/localizer.js"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesScript());

		IWMainApplication iwma = iwc.getIWMainApplication();

		String selectedBundle = iwc.getParameter(bundlesParameter);
		String selectedLocale = iwc.getParameter(localesParameter);
		String selectedStorage = iwc.getParameter(storageParameter);

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
		bundlesDrop.addMenuElementFirst(MessageResource.NO_BUNDLE, MessageResource.NO_BUNDLE);

		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, localesParameter);
		localesDrop.setID("localizerLocale");
		localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();
		localesDrop.setSelectedOption(iwc.getCurrentLocale().toString());

		DropdownMenu storeDrop = getMessageStorageResources(iwma, storageParameter);
		storeDrop.setID("localizerStorage");
		storeDrop.keepStatusOnAction();
		storeDrop.setToSubmit();

		DropdownMenu stringsDrop;

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

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label("Storage resource", storeDrop);
		formItem.add(label);
		formItem.add(storeDrop);
		form.add(formItem);

		if (selectedBundle != null) {
			TextInput newInput = new TextInput(newStringKeyParameter);
			newInput.setID("localizerNewKey");
			TextArea area = new TextArea(areaParameter);
			area.setID("localizerValue");

			stringsDrop = getLocalizeableStringsMenu(iwma, selectedBundle, selectedStorage, selectedLocale, stringsParameter);
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

			keySet.add(getLocalizeableStringsTableByStorageType(iwma, selectedBundle, selectedLocale, selectedStorage));
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

//	private Table2 getLocalizeableStringsTable(IWContext iwc, IWMainApplication iwma, String bundleIdentifier, IWResourceBundle iwrb) {
//		IWBundle bundle = iwma.getBundle(bundleIdentifier);
//		String[] strings = bundle.getLocalizableStrings();
//
//		Table2 table = new Table2();
//		table.setCellpadding(0);
//		table.setCellspacing(0);
//		table.setWidth("100%");
//		table.setStyleClass("developerTable");
//		table.setStyleClass("ruler");
//
//		TableRowGroup group = table.createHeaderRowGroup();
//		TableRow row = group.createRow();
//
//		TableCell2 cell = row.createHeaderCell();
//		cell.setStyleClass("firstColumn");
//		cell.add(new Text("Key"));
//
//		cell = row.createHeaderCell();
//		cell.setStyleClass("lastColumn");
//		cell.add(new Text("String"));
//
//		group = table.createBodyRowGroup();
//
//		for (int i = 0; i < strings.length; i++) {
//			String key = strings[i];
//
//			row = group.createRow();
//
//			cell = row.createCell();
//			cell.setStyleClass("firstColumn");
//
//			Link keyLink = new Link(key);
//			keyLink.setURL(CoreConstants.NUMBER_SIGN);
//			keyLink.setStyleClass("keyLink");
//			cell.add(keyLink);
//
//			cell = row.createCell();
//			cell.setStyleClass("lastColumn");
//			String localizedString = iwrb.getLocalizedString(key);
//			if (localizedString == null || StringHandler.EMPTY_STRING.equals(localizedString)){
//				String defaultString = bundle.getLocalizableStringDefaultValue(key);
//				defaultString = TextSoap.formatText(defaultString);
//				localizedString = defaultString;
//				cell.setStyleClass("isEmpty");
//			}
//			else{
//				localizedString = TextSoap.formatText(localizedString);
//			}
//			cell.add(new Text(localizedString));
//
//			if (i % 2 == 0) {
//				row.setStyleClass("evenRow");
//			}
//			else {
//				row.setStyleClass("oddRow");
//			}
//		}
//
//		return table;
//	}

//	public void refreshTebleData(String bundleIdentifier, String selectedLocale, String selectedStorageIdentifier) {
//		FieldSet keySet = new FieldSet(new Legend("Available Strings"));
//		keySet.setStyleClass("stringsSet");
//		topLayer.add(keySet);
//
////		keySet.add(getLocalizeableStringsTable(iwc, iwma, selectedBundle, iwrb));
//		keySet.add(getLocalizeableStringsTableByStorageType(iwma, selectedBundle, selectedLocale, selectedStorage));
//	}

	private Table2 getLocalizeableStringsTableByStorageType(IWMainApplication iwma, String bundleIdentifier, String selectedLocale, String selectedStorageIdentifier) {
		List<MessageResource> resourceList = getResourceList(iwma, selectedStorageIdentifier, bundleIdentifier, LocaleUtil.getLocale(selectedLocale));

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
		cell.add(new Text("String"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Resource"));

		group = table.createBodyRowGroup();

		for(MessageResource resource : resourceList) {

			Set<String> localisedKeys = resource.getAllLocalisedKeys();
			Object[] strings = localisedKeys.toArray();
			for (int i = 0; i < strings.length; i++) {
				Object key = strings[i];

				row = group.createRow();

				cell = row.createCell();
				cell.setStyleClass("firstColumn");

				Link keyLink = new Link(String.valueOf(key));
				keyLink.setURL(CoreConstants.HASH);
				keyLink.setStyleClass("keyLink");
				cell.add(keyLink);

				cell = row.createCell();

				Object localizedString = resource.getMessage(key);
				if (localizedString == null){
					String defaultString = CoreConstants.EMPTY;
					defaultString = TextSoap.formatText(defaultString);
					localizedString = defaultString;
					cell.setStyleClass("isEmpty");
				}
				else{
					localizedString = TextSoap.formatText(String.valueOf(localizedString));
				}

				Span span = new Span(new Text(String.valueOf(localizedString)));
				span.setStyleClass("stringValue");

				cell.add(span);

				cell = row.createCell();
				cell.setStyleClass("lastColumn");
				span = new Span(new Text(resource.getIdentifier()));
				span.setStyleClass("storageKey");
				cell.add(span);

				if (i % 2 == 0) {
					row.setStyleClass("evenRow");
				}
				else {
					row.setStyleClass("oddRow");
				}
			}

		}
		return table;
	}

	public static DropdownMenu getRegisteredDropdown(IWMainApplication iwma, String name) {
		return BundlePropertySetter.getRegisteredBundlesDropdown(iwma, name);
	}

	public static DropdownMenu getMessageStorageResources(IWMainApplication iwma, String name) {
		List<String> resources = iwma.getAvailableMessageStorageTypes();
		DropdownMenu down = new DropdownMenu(name);

		down.addMenuElement(ALL_RESOURCES);
		for(String resource : resources) {
			down.addMenuElement(resource);
		}
		return down;
	}

	public  DropdownMenu getLocalizeableStringsMenu(IWMainApplication iwma, String bundleIdentifier, String storageIdentifier, String selectedLocale, String name) {
		List<MessageResource> resources = getResourceList(iwma, storageIdentifier, bundleIdentifier, LocaleUtil.getLocale(selectedLocale));

		DropdownMenu down = new DropdownMenu(name);
		for(MessageResource resource : resources) {
			if(resource == null)
				continue;

			Set<String> localisedKeys = resource.getAllLocalisedKeys();
			for (String key : localisedKeys) {
				down.addMenuElement(new StringBuilder(key).append(CoreConstants.SPACE).append(CoreConstants.BRACKET_LEFT).append(resource.getIdentifier())
						.append(CoreConstants.BRACKET_RIGHT).toString());
			}
		}
		return down;
	}

	private List<MessageResource> getResourceList(IWMainApplication iwma, String selectedStorageIdentifier, String bundleIdentifier, Locale locale) {
		List<MessageResource> resourceList;
		if(selectedStorageIdentifier.equals(ALL_RESOURCES)) {
			resourceList = iwma.getMessageFactory().getResourceListByBundleAndLocale(bundleIdentifier, locale);
		} else {
			resourceList = new ArrayList<MessageResource>(1);
			MessageResource resource = iwma.getMessageFactory().getResource(selectedStorageIdentifier, bundleIdentifier, locale);
			if(resource != null)
				resourceList.add(resource);
		}
		return resourceList;
	}

	private Web2Business getWeb2Business(IWApplicationContext iwac) {
		try {
			return IBOLookup.getServiceInstance(iwac, Web2Business.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
}