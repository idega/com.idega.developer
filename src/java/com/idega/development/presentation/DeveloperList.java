package com.idega.development.presentation;

import com.idega.jmodule.object.FrameList;
import com.idega.jmodule.object.ModuleInfo;

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
    setLinkStyle("font-family: Verdana, Arial, sans-serif; font-weight: bold; font-size: 8pt; text-decoration: none;");

    addToMenu(Localizer.class,"Localizer",IWDeveloper.frameName);
    addToMenu(LocaleSwitcher.class,"LocaleSwitcher",IWDeveloper.frameName);
    addToMenu(BundleCreator.class,"BundleCreator",IWDeveloper.frameName);
    addToMenu(BundlePropertySetter.class,"BundlePropertySetter",IWDeveloper.frameName);
    addToMenu(BundleComponentManager.class,"BundleComponents",IWDeveloper.frameName);
    addToMenu(ComponentManager.class,"ComponentManager",IWDeveloper.frameName);
    addToMenu(ApplicationPropertySetter.class,"ApplicationPropertySetter",IWDeveloper.frameName);
    addToMenu(DBPoolStatusViewer.class,"DBPoolStatusViewer",IWDeveloper.frameName);

    setZebraColors("#FFFFFF","#ECECEC");
  }
}