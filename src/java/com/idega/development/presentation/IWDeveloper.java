package com.idega.development.presentation;


import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;


/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWDeveloper extends JModuleObject {

  private static String localizerParameter = "iw_localizer";
  private static String localeswitcherParameter = "iw_localeswitcher";
  private static String bundleCreatorParameter = "iw_bundlecreator";
  private static String applicationPropertiesParameter = "iw_application_properties_setter";
  private static String bundlesPropertiesParameter = "iw_bundle_properties_setter";
  public static String actionParameter = "iw_developer_action";
  private Table mainTable;
  private Table menuTable;
  private int count = 1;

  public IWDeveloper() {
  }

  public void main(ModuleInfo modinfo){
    mainTable = new Table(2,1);
    add(mainTable);
    menuTable = new Table();
    mainTable.add(menuTable,1,1);
    mainTable.setVerticalAlignment(1,1,"top");

    addToMenu("Localizer",localizerParameter);
    addToMenu("Localeswitcher",localeswitcherParameter);
    addToMenu("BundleCreator",bundleCreatorParameter);
    addToMenu("BundlePropertySetter",bundlesPropertiesParameter);
    addToMenu("ApplicationPropertySetter",applicationPropertiesParameter);


    String action = modinfo.getParameter(actionParameter);
    if(action!=null){
      if(action.equals(localizerParameter)){
        useDeveloperModule(new Localizer());
      }
      if(action.equals(localeswitcherParameter)){
        useDeveloperModule(new LocaleSwitcher());
      }
      if(action.equals(this.bundleCreatorParameter)){
        useDeveloperModule(new BundleCreator());
      }
      if(action.equals(this.bundlesPropertiesParameter)){
        useDeveloperModule(new BundlePropertySetter());
      }
      if(action.equals(this.applicationPropertiesParameter)){
        useDeveloperModule(new ApplicationPropertySetter());
      }
    }
  }

  private void useDeveloperModule(ModuleObject obj){
    mainTable.add(obj,2,1);
  }

  private void addToMenu(String displayName, String parameter){
    Link link = new Link(displayName);
    link.addParameter(actionParameter,parameter);
    menuTable.add(link,1,count++);
  }





}