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

    addToList(Localizer.class,image,"Localizer",IWDeveloper.frameName);
    addToList(LocaleSwitcher.class,image,"LocaleSwitcher",IWDeveloper.frameName);
    addToList(BundleCreator.class,image,"BundleCreator",IWDeveloper.frameName);
    addToList(BundlePropertySetter.class,image,"BundlePropertySetter",IWDeveloper.frameName);
    addToList(BundleComponentManager.class,image,"BundleComponents",IWDeveloper.frameName);
    addToList(ComponentManager.class,image,"ComponentManager",IWDeveloper.frameName);
    addToList(ApplicationPropertySetter.class,image,"ApplicationPropertySetter",IWDeveloper.frameName);
    addToList(DBPoolStatusViewer.class,image,"DBPoolStatusViewer",IWDeveloper.frameName);

    setZebraColors("#FFFFFF","#ECECEC");
  }
}