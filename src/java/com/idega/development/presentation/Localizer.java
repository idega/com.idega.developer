package com.idega.development.presentation;

import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.business.IBOLookup;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.core.localisation.presentation.LocaleSwitcher;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.LocaleUtil;
import com.idega.util.StringHandler;
import com.idega.versioncontrol.business.UpdateService;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Localizer extends PresentationObjectContainer {

	private static String bundlesParameter = "iw_availablebundles";
	private static String localesParameter = "iw_locales";
	private static String stringsParameter = "iw_localestrings";
	private static String areaParameter = "iw_stringsarea";
	private static String subAction = "iw_localizer_sub_action";
	private static String newStringKeyParameter = "iw_new_string_key";
	private static String ACTION_COMMIT_REPO="iw_commit_repos";
	private static String ACTION_SAVE="save";
	private static String ACTION_DELETE="delete";
	
	public Localizer() {
	}

	public void main(IWContext iwc) {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");

		IWMainApplication iwma = iwc.getIWMainApplication();
		DropdownMenu bundlesDrop = getRegisteredDropdown(iwma, bundlesParameter);
		bundlesDrop.keepStatusOnAction();
		bundlesDrop.setToSubmit();
		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, localesParameter);
		localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();

		DropdownMenu stringsDrop;

		String selectedBundle = iwc.getParameter(bundlesParameter);

		Link templateLink = new Link();
		templateLink.maintainParameter(IWDeveloper.actionParameter, iwc);
		templateLink.maintainParameter(localesParameter, iwc);
		templateLink.maintainParameter(bundlesParameter, iwc);
		templateLink.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc);
		//templateLink.setTarget(IWDeveloper.frameName);

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table Frame = new Table();
		Table table = new Table(2, 6);
		table.setAlignment(2, 6, "right");
		table.setColumnVerticalAlignment(1, "top");
		table.setWidth(1, "150");
		Frame.add(table, 1, 1);
		form.add(Frame);
		table.add(IWDeveloper.getText("Bundle:"), 1, 1);
		table.add(bundlesDrop, 2, 1);
		table.add(IWDeveloper.getText("Locale:"), 1, 2);
		table.add(localesDrop, 2, 2);

		if (selectedBundle == null) {
			//stringsDrop = new DropdownMenu(stringsParameter);
			table.setAlignment(2, 3, "right");
			table.add(new SubmitButton("Get Available Keys", subAction, "choose"), 2, 3);
		}
		else {
			
			IWBundle iwb = iwma.getBundle(selectedBundle);
			IWResourceBundle iwrb = iwb.getResourceBundle(LocaleUtil.getLocale(iwc.getParameter(localesParameter)));
			String stringsKey = iwc.getParameter(stringsParameter);
			String areaText = iwc.getParameter(areaParameter);
			String newStringsKey = iwc.getParameter(this.newStringKeyParameter);
			
			if (this.isCommitting(iwc)) {
				this.commitLocalizationFile(iwc);
			}
			
			if (stringsKey == null && newStringsKey != null) {
				stringsKey = newStringsKey;
			}

			if (stringsKey != null) {
				String oldStringValue = iwrb.getLocalizedString(stringsKey);
				if (this.isDeleting(iwc)) {
					iwb.removeLocalizableString(stringsKey);
					//boolean b = iwrb.removeString(stringsKey);
					iwrb.storeState();
				}
				
				if (areaText == null) {
					PresentationObject area = getTextArea(areaParameter, oldStringValue);
					table.add(area, 2, 5);
				}
				else {
					if (areaText.equals("")) {
						PresentationObject area;
						if (oldStringValue != null) {
							area = getTextArea(areaParameter, oldStringValue);
						}
						else {
							area = getTextArea(areaParameter, "");
						}
						table.add(area, 2, 5);
					}
					else {
						PresentationObject area;
						/**
						 * Saving possible
						 */
						if (this.isSaving(iwc)) {
							String newKey = iwc.getParameter(newStringKeyParameter);

							if (newKey != null) {
								if (newKey.equals("")) {
									iwrb.setString(stringsKey, areaText);
								}
								else {
									iwrb.setString(newKey, areaText);
								}
							}
							area = getTextArea(areaParameter, areaText);
						}
						/**
						 * Not Saving
						 */
						else {

							//String areaValue = iwrb.getStringChecked(stringsKey);
							String areaValue = iwc.getParameter(this.areaParameter);
							if (areaValue == null) {
								area = getTextArea(areaParameter, "");
							}
							else {
								if (oldStringValue == null) {
									area = getTextArea(areaParameter, "");
								}
								else {
									area = getTextArea(areaParameter, oldStringValue);
								}
							}
						}
						table.add(area, 2, 5);
					}
					IBAddModuleWindow.removeAttributes(iwc);

				}
				table.add(new SubmitButton("Save", subAction, ACTION_SAVE), 2, 6);
				table.add(new SubmitButton("Commit to repository", subAction, ACTION_COMMIT_REPO), 2, 6);
				table.add(new SubmitButton("Delete", subAction, ACTION_DELETE), 2, 6);
				table.add(IWDeveloper.getText("New String key:"), 1, 4);
				table.add(IWDeveloper.getText("New String value:"), 1, 5);
				TextInput newInput = new TextInput(newStringKeyParameter);
				table.add(newInput, 2, 4);
			}
			else {
				table.add(getTextArea(areaParameter, ""), 2, 5);
				table.add(new SubmitButton("Save", subAction, ACTION_SAVE), 2, 6);
				table.add(new SubmitButton("Commit to repository", subAction, ACTION_COMMIT_REPO), 2, 6);
				table.add(IWDeveloper.getText("New String key:"), 1, 4);
				table.add(IWDeveloper.getText("New String value:"), 1, 5);
				TextInput newInput = new TextInput(newStringKeyParameter);
				table.add(newInput, 2, 4);
			}

			//table.add(new SubmitButton("Select Locale",subAction,"select"),2,1);
			table.add(IWDeveloper.getText("String:"), 1, 3);
			stringsDrop = this.getLocalizeableStringsMenu(iwma, selectedBundle, stringsParameter);
			stringsDrop.keepStatusOnAction();
			stringsDrop.setToSubmit();
			table.add(stringsDrop, 2, 3);
			//table.add(new SubmitButton("Choose String",subAction,"choose"),3,1);

			Frame.add(IWDeveloper.getText("Available Strings:"), 1, 3);
			Frame.add(Text.getBreak(), 1, 3);
			Frame.add(this.getLocalizeableStringsTable(iwc, iwma, selectedBundle, iwrb, stringsParameter, templateLink), 1, 3);

		}
	}

	public static Form getAvailableLocalesForm(IWContext iwc) {
		IWMainApplication iwma = iwc.getIWMainApplication();

		Form myForm = new Form();
		myForm.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
		DropdownMenu down = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, LocaleSwitcher.localesParameter);
		down.keepStatusOnAction();
		down.setToSubmit();
		myForm.add(down);

		return myForm;
	}

	public static Table getLocalizeableStringsTable(IWContext iwc, IWMainApplication iwma, String bundleIdentifier, IWResourceBundle iwrb, String parameterName, Link templateLink) {
		IWBundle bundle = iwma.getBundle(bundleIdentifier);
		String[] strings = bundle.getLocalizableStrings();
		Table table = new Table(2, strings.length);
		table.setColumnVerticalAlignment(1, "top");
		table.setCellpadding(5);
		String localizedString;
		Link keyLink;
		Text stringValueText;
		for (int i = 0; i < strings.length; i++) {
			//name = new Text(strings[i],true,false,false);
			//name = new Link(strings[i]);
			keyLink = (Link) templateLink.clone();
			String key = strings[i];
			localizedString = iwrb.getLocalizedString(key);
			if (localizedString == null || StringHandler.EMPTY_STRING.equals(localizedString)){
				String defaultString = bundle.getLocalizableStringDefaultValue(key);
				stringValueText = new Text(defaultString);
				stringValueText.setFontColor("#FF0000");
				keyLink.setFontColor("#FF0000");
			}
			else{
				stringValueText = new Text(localizedString);
			}
			
			keyLink.setText(strings[i]);
			keyLink.setBold();
			keyLink.addParameter(parameterName, strings[i]);
			keyLink.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc);
			//name.setTarget(IWDeveloper.frameName);
			//name.setClassToInstanciate(Localizer.class);
			table.add(keyLink, 1, i + 1);

			table.add(stringValueText, 2, i + 1);
		}
		table.setWidth(400);
		table.setColor("#9FA9B3");
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

	private boolean isSaving(IWContext iwc) {
		String subActioner = iwc.getParameter(subAction);
		if (subActioner == null) {
			return false;
		}
		else {
			if (subActioner.equals(ACTION_SAVE)) {
				return true;
			}
			return false;
		}
	}

	private boolean isDeleting(IWContext iwc) {
		String subActioner = iwc.getParameter(subAction);
		if (subActioner == null) {
			return false;
		}
		else {
			if (subActioner.equals(ACTION_DELETE)) {
				return true;
			}
			return false;
		}
	}
	
	private boolean isCommitting(IWContext iwc) {
		String subActioner = iwc.getParameter(subAction);
		if (subActioner == null) {
			return false;
		}
		else {
			if (subActioner.equals(ACTION_COMMIT_REPO)) {
				return true;
			}
			return false;
		}
	}	

	private PresentationObject getTextArea(String name, String startValue) {
		TextArea area = new TextArea(name, startValue);
		area.setWidth(30);
		return area;
	}
	
	private void commitLocalizationFile(IWContext iwc){
	
		String bundleIdentifier = iwc.getParameter(this.bundlesParameter);
		String localeString = iwc.getParameter(this.localesParameter);
		
		UpdateService updateservice;
		boolean succeeded=false;
		try {
			updateservice = (UpdateService)IBOLookup.getServiceInstance(iwc,UpdateService.class);
			succeeded = updateservice.commitLocalizationFile(bundleIdentifier,localeString);
		}
		catch (Exception e) {
			log(e);
		}
		if(succeeded){
			add("Commit successful");
		}
		else{
			add("Commit failed");			
		}
	}
}
