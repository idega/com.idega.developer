package com.idega.development.presentation;

import com.idega.jmodule.object.FrameList;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Image;
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

  public void main(ModuleInfo modinfo) {
    super.main(modinfo);
    iwb = getBundle(modinfo);
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 7pt; text-decoration: none;");

    Image image = iwb.getImage("/developer/listbutton.gif","",13,13);

    addToList(Localizer.class,image,getClassName(Localizer.class),IWDeveloper.frameName);
    addToList(LocaleSwitcher.class,image,getClassName(LocaleSwitcher.class),IWDeveloper.frameName);
    addToList(BundleCreator.class,image,getClassName(BundleCreator.class),IWDeveloper.frameName);
    addToList(BundlePropertySetter.class,image,getClassName(BundlePropertySetter.class),IWDeveloper.frameName);
    addToList(ComponentManager.class,image,getClassName(ComponentManager.class),IWDeveloper.frameName);
    addToList(ApplicationPropertySetter.class,image,getClassName(ApplicationPropertySetter.class),IWDeveloper.frameName);
    addToList(DBPoolStatusViewer.class,image,getClassName(DBPoolStatusViewer.class),IWDeveloper.frameName);

    setZebraColors("#FFFFFF","#ECECEC");
  }

  private String getClassName(Class listClass) {
    return listClass.getName().substring(listClass.getName().lastIndexOf(".")+1);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}