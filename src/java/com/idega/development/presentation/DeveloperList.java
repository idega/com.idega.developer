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
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 7pt; text-decoration: none;");

    Image image = iwb.getImage("/developer/listbutton.gif","",13,13);

    addToList(Localizer.class,image,getClassName(Localizer.class),IWDeveloper.frameName);
    addToList(LocaleSwitcher.class,image,getClassName(LocaleSwitcher.class),IWDeveloper.frameName);
    addToList(LocaleSetter.class,image,getClassName(LocaleSetter.class),IWDeveloper.frameName);
    addToList(BundleCreator.class,image,getClassName(BundleCreator.class),IWDeveloper.frameName);
    addToList(BundlePropertySetter.class,image,getClassName(BundlePropertySetter.class),IWDeveloper.frameName);
    addToList(BundleComponentManager.class,image,"BundleComponents",IWDeveloper.frameName);
    addToList(ComponentManager.class,image,getClassName(ComponentManager.class),IWDeveloper.frameName);
    addToList(ApplicationPropertySetter.class,image,getClassName(ApplicationPropertySetter.class),IWDeveloper.frameName);
    addToList(DBPoolStatusViewer.class,image,getClassName(DBPoolStatusViewer.class),IWDeveloper.frameName);
    addToList(SQLQueryer.class,image,getClassName(SQLQueryer.class),IWDeveloper.frameName);
    addToList(ApplicationStatus.class,image,getClassName(ApplicationStatus.class),IWDeveloper.frameName);

    setZebraColors("#FFFFFF","#ECECEC");
  }

  private String getClassName(Class listClass) {
    return listClass.getName().substring(listClass.getName().lastIndexOf(".")+1);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}