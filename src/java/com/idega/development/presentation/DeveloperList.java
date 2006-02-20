package com.idega.development.presentation;

import com.idega.core.localisation.presentation.LocaleSwitcher;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.FrameList;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.text.Link;

/**
 * Title: DeveloperList
 * Description: Extension of FrameList to suit IWDeveloper
 * Copyright:  Copyright (c) 2001
 * Company: idega
 * @author Laddi
 * @version 1.0
 */

public class DeveloperList extends FrameList {

private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
private IWBundle iwb;
private static String styleName = "iwdClass";

  public DeveloperList() {
  }

  public void main(IWContext iwc) {
    iwb = getBundle(iwc);
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 11px; text-decoration: none;color:#000000;");
		getParentPage().setStyleDefinition("."+styleName, "font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 10px; text-decoration: none;color:#000000;");
		getParentPage().setStyleDefinition("."+styleName+":hover", "font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 10px; text-decoration: none;color:#999999;");

    Image image = iwb.getImage("/developer/listbutton.gif","",13,13);

    addToList(getClassLink(iwc, Localizer.class,"Localizer"),(Image) image.clone());
    addToList(getClassLink(iwc, LocaleSwitcher.class,"Locale Switcher"),(Image) image.clone());
    addToList(getClassLink(iwc, LocaleSetter.class,"Locale Setter"),(Image) image.clone());
    addToList(getClassLink(iwc, ObjectTypeManager.class,"Object Types"), (Image) image.clone());
    addToList(getClassLink(iwc, BundleCreator.class,"Bundle Creator"),(Image) image.clone());
    addToList(getClassLink(iwc, BundlePropertySetter.class,"Bundle Property Setter"),(Image) image.clone());
    addToList(getClassLink(iwc, BundleResourceManager.class,"Bundle Resource Manager"),(Image) image.clone());
    addToList(getClassLink(iwc, BundleComponentManager.class,"Bundle Components"),(Image) image.clone());
    addToList(getClassLink(iwc, ComponentManager.class,"Component Manager"),(Image) image.clone());
    addToList(getClassLink(iwc, ApplicationPropertySetter.class,"Application Property Setter"),(Image) image.clone());
    addToList(getClassLink(iwc, DBPoolStatusViewer.class,"DBPool Status Viewer"),(Image) image.clone());
    addToList(getClassLink(iwc, SQLQueryer.class,"SQL Queryer"),(Image) image.clone());
    addToList(getClassLink(iwc, ApplicationStatus.class,"Application Status"),(Image) image.clone());
    addToList(getClassLink(iwc, Caches.class,"Caches"),(Image) image.clone());
    addToList(getClassLink(iwc, Logs.class,"Logs"),(Image) image.clone());
    addToList(getClassLink(iwc, Versions.class,"Versions"),(Image) image.clone());
	addToList(getClassLink(iwc, UpdateManager.class,"UpdateManager"),(Image) image.clone());
	addToList(getClassLink(iwc, HomePageGenerator.class,"Homepage Generator"),(Image) image.clone());
	addToList(getClassLink(iwc, PageObjects.class,"Page Object Viewer"),(Image) image.clone());
	addToList(getClassLink(iwc, ScriptManager.class,"Script Manager"),(Image) image.clone());
	addToList(getClassLink(iwc, LDAPManager.class,"LDAP Manager"),(Image) image.clone());
	
    setZebraColors("#B0B29D","#B0B29D");
  }

  private Link getClassLink(IWContext iwc, Class linkClass, String linkName) {
  	Link link = new Link(linkName);
  	link.setStyleClass(styleName);
  	link.addParameter(IWDeveloper.PARAMETER_CLASS_NAME, IWMainApplication.getEncryptedClassName(linkClass));
  	return link;
  }
  
  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}
