package com.idega.development.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.idega.util.ListUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceFactory;
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
	
	@Autowired 
	private MessageResourceFactory messageFactory;

	private IWMainApplication application;

	private IWMainApplication getApplication() {
		if (this.application == null) {
			this.application = IWMainApplication.getDefaultIWMainApplication();
		}

		return this.application;
	}


	private MessageResourceFactory getMessageResourceFactory() {
		if (this.messageFactory == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.messageFactory;
	}

	public Localizer() {}

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

			stringsDrop = getLocalizeableStringsMenu(selectedBundle, selectedStorage, selectedLocale, stringsParameter);
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

			keySet.add(getLocalizeableStringsTableByStorageType(selectedBundle, selectedLocale, selectedStorage));
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

	private TreeMap<String, Map<MessageResource, String>> getLocalizedStrings(List<MessageResource> resources) {
		TreeMap<String, Map<MessageResource, String>> localizedStrings = new TreeMap<>();
	
		if (!ListUtil.isEmpty(resources)) {
			for (MessageResource resource : resources) {
				Set<String> keys = resource.getAllLocalizedKeys();
				if (!ListUtil.isEmpty(keys)) {
					for (String key : keys) {
						String value = resource.getMessage(key);
						if (!StringUtil.isEmpty(value)) {
							Map<MessageResource, String> valueMap = localizedStrings.get(key);
							if (MapUtil.isEmpty(valueMap)) {
								valueMap = new HashMap<>();
								localizedStrings.put(key, valueMap);
							}

							valueMap.put(resource, value);
						}
					}
				}
			}
		}

		return localizedStrings;
	}

	private Table2 getLocalizeableStringsTableByStorageType(
			String bundleIdentifier, 
			String selectedLocale, 
			String selectedStorageIdentifier) {


		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
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

		boolean isEven = Boolean.FALSE;
		List<MessageResource> resourceList = getResourceList(selectedStorageIdentifier, bundleIdentifier, LocaleUtil.getLocale(selectedLocale));
		TreeMap<String, Map<MessageResource, String>> localizedStrings = getLocalizedStrings(resourceList);
		for (String key: localizedStrings.keySet()) {
			Map<MessageResource, String> valueMap = localizedStrings.get(key);
			if (!MapUtil.isEmpty(valueMap)) {
				ArrayList<MessageResource> resources = new ArrayList<>(valueMap.keySet());
				Collections.sort(resources, new Comparator<MessageResource>() {

					@Override
					public int compare(MessageResource o1, MessageResource o2) {
						if (o1.getLevel().intValue() > o2.getLevel().intValue()) {
							return -1;
						}

						if (o2.getLevel().intValue() > o1.getLevel().intValue()) {
							return 1;
						}
						
						return 0;
					}
				});

				for (MessageResource resource : resources) {
					row = group.createRow();
					boolean isModificationDisabled = !resource.isModificationAllowed();
					if (isModificationDisabled) {
						row.setStyleClass("disabled");
					}

					cell = row.createCell();
					cell.setStyleClass("firstColumn");

					if (!isModificationDisabled) {
						Link keyLink = new Link(String.valueOf(key));
						keyLink.setURL(CoreConstants.HASH);
						keyLink.setStyleClass("keyLink");
						cell.add(keyLink);
					} else {
						cell.add(new Span(new Text(String.valueOf(key))));
					}

					if (resources.indexOf(resource) == 0 && resources.size() > 1) {
						Text usageText = new Text("in use");
						usageText.setStyleClass("green-label");
						cell.add(usageText);
					}

					cell = row.createCell();

					String localizedString = valueMap.get(resource);
					if (localizedString == null){
						String defaultString = CoreConstants.EMPTY;
						defaultString = TextSoap.formatText(defaultString);
						localizedString = defaultString;
						cell.setStyleClass("isEmpty");
					} else {
						localizedString = TextSoap.formatText(localizedString);
					}

					Span span = new Span(new Text(String.valueOf(localizedString)));
					if (!isModificationDisabled) {
						span.setStyleClass("stringValue");
					}

					cell.add(span);

					cell = row.createCell();
					cell.setStyleClass("lastColumn");
					span = new Span(new Text(resource.getIdentifier()));
					span.setStyleClass("storageKey");
					cell.add(span);

					if (isEven) {
						row.setStyleClass("evenRow");
						isEven = Boolean.FALSE;
					} else {
						row.setStyleClass("oddRow");
						isEven = Boolean.TRUE;
					}
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

	public DropdownMenu getLocalizeableStringsMenu(
			String bundleIdentifier, 
			String storageIdentifier, 
			String selectedLocale, 
			String name) {
		DropdownMenu down = new DropdownMenu(name);

		List<MessageResource> resources = getResourceList(
				storageIdentifier, 
				bundleIdentifier, 
				LocaleUtil.getLocale(selectedLocale));
		for(MessageResource resource : resources) {
			if(resource == null) {
				continue;
			}

			Set<String> localizedKeys = resource.getAllLocalizedKeys();
			for (String key : localizedKeys) {
				down.addMenuElement(new StringBuilder(key)
						.append(CoreConstants.SPACE)
						.append(CoreConstants.BRACKET_LEFT)
						.append(resource.getIdentifier())
						.append(CoreConstants.BRACKET_RIGHT).toString());
			}
		}

		return down;
	}

	private List<MessageResource> getResourceList(
			String selectedStorageIdentifier, 
			String bundleIdentifier, 
			Locale locale) {
		List<MessageResource> resourceList;

		if(selectedStorageIdentifier.equals(ALL_RESOURCES)) {
			resourceList = getMessageResourceFactory().getResourceListByBundleAndLocale(bundleIdentifier, locale);
		} else {
			resourceList = new ArrayList<MessageResource>(1);
			MessageResource resource = getMessageResourceFactory().getResource(selectedStorageIdentifier, bundleIdentifier, locale);
			if(resource != null) {
				resourceList.add(resource);
			}
		}

		return resourceList;
	}

	private Web2Business getWeb2Business(IWApplicationContext iwac) {
		try {
			return IBOLookup.getServiceInstance(iwac, Web2Business.class);
		} catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
}