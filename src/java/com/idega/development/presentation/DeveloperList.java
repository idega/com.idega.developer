package com.idega.development.presentation;

import com.idega.presentation.FrameList;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.idegaweb.IWBundle;

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

  public DeveloperList() {
  }

  public void main(IWContext iwc) {
    iwb = getBundle(iwc);
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 11px; text-decoration: none;color:#000000;");

    Image image = iwb.getImage("/developer/listbutton.gif","",13,13);

    addToList(Localizer.class,image,"Localizer",IWDeveloper.frameName);
    addToList(LocaleSwitcher.class,image,"Locale Switcher",IWDeveloper.frameName);
    addToList(LocaleSetter.class,image,"Locale Setter",IWDeveloper.frameName);
    addToList(BundleCreator.class,image,"Bundle Creator",IWDeveloper.frameName);
    addToList(BundlePropertySetter.class,image,"Bundle Property Setter",IWDeveloper.frameName);
    addToList(BundleComponentManager.class,image,"Bundle Components",IWDeveloper.frameName);
    addToList(ComponentManager.class,image,"Component Manager",IWDeveloper.frameName);
    addToList(ApplicationPropertySetter.class,image,"Application Property Setter",IWDeveloper.frameName);
    addToList(DBPoolStatusViewer.class,image,"DBPool Status Viewer",IWDeveloper.frameName);
    addToList(SQLQueryer.class,image,"SQL Queryer",IWDeveloper.frameName);
    addToList(ApplicationStatus.class,image,"Application Status",IWDeveloper.frameName);
    addToList(Caches.class,image,"Caches",IWDeveloper.frameName);
    addToList(Logs.class,image,"Logs",IWDeveloper.frameName);
    addToList(Versions.class,image,"Versions",IWDeveloper.frameName);
    setZebraColors("#B0B29D","#B0B29D");
  }

  private String getClassName(Class listClass) {
    return listClass.getName().substring(listClass.getName().lastIndexOf(".")+1);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}
