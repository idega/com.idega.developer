package com.idega.development.presentation;

import com.idega.jmodule.object.FrameList;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Image;

/**
 * Title: DeveloperList
 * Description: Extension of FrameList to suit IWDeveloper
 * Copyright:  Copyright (c) 2001
 * Company: idega
 * @author Laddi
 * @version 1.0
 */

public class DeveloperList extends FrameList {

  public DeveloperList() {
  }

  public void main(ModuleInfo modinfo) {
    super.main(modinfo);
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 7pt; text-decoration: none;");

    Image image = new Image("/pics/next.gif");

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
}