package com.idega.development.presentation;

import com.idega.core.localisation.presentation.LocaleSwitcher;
import com.idega.idegaweb.IWBundle;
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

    addToList(getClassLink(iwc, Localizer.class,"Localizer"),image);
    addToList(getClassLink(iwc, LocaleSwitcher.class,"Locale Switcher"),image);
    addToList(getClassLink(iwc, LocaleSetter.class,"Locale Setter"),image);
    addToList(getClassLink(iwc, ObjectTypeManager.class,"Object Types"), image);
    addToList(getClassLink(iwc, BundleCreator.class,"Bundle Creator"),image);
    addToList(getClassLink(iwc, BundlePropertySetter.class,"Bundle Property Setter"),image);
    addToList(getClassLink(iwc, BundleComponentManager.class,"Bundle Components"),image);
    addToList(getClassLink(iwc, ComponentManager.class,"Component Manager"),image);
    addToList(getClassLink(iwc, ApplicationPropertySetter.class,"Application Property Setter"),image);
    addToList(getClassLink(iwc, DBPoolStatusViewer.class,"DBPool Status Viewer"),image);
    addToList(getClassLink(iwc, SQLQueryer.class,"SQL Queryer"),image);
    addToList(getClassLink(iwc, ApplicationStatus.class,"Application Status"),image);
    addToList(getClassLink(iwc, Caches.class,"Caches"),image);
    addToList(getClassLink(iwc, Logs.class,"Logs"),image);
    addToList(getClassLink(iwc, Versions.class,"Versions"),image);
    setZebraColors("#B0B29D","#B0B29D");
  }

  private Link getClassLink(IWContext iwc, Class linkClass, String linkName) {
  	Link link = new Link(linkName);
  	link.setStyleClass(styleName);
  	link.addParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc.getApplication().getEncryptedClassName(linkClass));
  	return link;
  }
  
  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}
