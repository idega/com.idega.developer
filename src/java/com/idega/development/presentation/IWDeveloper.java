package com.idega.development.presentation;


import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.app.*;
import com.idega.idegaweb.IWConstants;


/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWDeveloper extends com.idega.jmodule.object.app.IWApplication {

  private static final String localizerParameter = "iw_localizer";
  private static final String localeswitcherParameter = "iw_localeswitcher";
  private static final String bundleCreatorParameter = "iw_bundlecreator";
  private static final String bundleComponentManagerParameter = "iw_bundlecompmanager";
  private static final String applicationPropertiesParameter = "iw_application_properties_setter";
  private static final String bundlesPropertiesParameter = "iw_bundle_properties_setter";
  public static final String actionParameter = "iw_developer_action";
  public static final String dbPoolStatusViewerParameter = "iw_poolstatus_viewer";


  public IWDeveloper() {
    super("idegaWeb Developer");
    add(IWDeveloper.IWDevPage.class);
    super.setResizable(true);
    super.setWidth(800);
    super.setHeight(600);
  }


  public static class IWDevPage extends Page{

    public IWDevPage(){
    }

    private Table mainTable;
    private Table menuTable;
    private int count = 1;

    public void main(ModuleInfo modinfo)throws Exception{
      mainTable = new Table(2,1);
      add(mainTable);
      menuTable = new Table();
      mainTable.add(menuTable,1,1);
      mainTable.setVerticalAlignment(1,1,"top");
      mainTable.setColor(1,1,IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);

      /*addToMenu("Localizer",localizerParameter);
      addToMenu("Localeswitcher",localeswitcherParameter);
      addToMenu("BundleCreator",bundleCreatorParameter);
      addToMenu("BundlePropertySetter",bundlesPropertiesParameter);
      addToMenu("BundleComponents",bundleComponentManagerParameter);
      addToMenu("ApplicationPropertySetter",applicationPropertiesParameter);
      addToMenu("DB PoolStatusViewer",dbPoolStatusViewerParameter);
      */

      addToMenu(Localizer.class);
      addToMenu(LocaleSwitcher.class);
      addToMenu(BundleCreator.class);
      addToMenu(BundlePropertySetter.class);
      addToMenu(BundleComponentManager.class,"BundleComponents");
      addToMenu(ComponentManager.class);
      addToMenu(ApplicationPropertySetter.class);
      addToMenu(DBPoolStatusViewer.class);


      String action = modinfo.getParameter(actionParameter);
      if(action!=null){
        useDeveloperModule(action);
        /*
        if(action.equals(localizerParameter)){
          useDeveloperModule(new Localizer());
        }
        if(action.equals(localeswitcherParameter)){
          useDeveloperModule(new LocaleSwitcher());
        }
        if(action.equals(bundleCreatorParameter)){
          useDeveloperModule(new BundleCreator());
        }
        if(action.equals(bundlesPropertiesParameter)){
          useDeveloperModule(new BundlePropertySetter());
        }
        if(action.equals(applicationPropertiesParameter)){
          useDeveloperModule(new ApplicationPropertySetter());
        }
        if(action.equals(dbPoolStatusViewerParameter)){
          useDeveloperModule(new DBPoolStatusViewer());
        }
        if(action.equals(bundleComponentManagerParameter)){
          useDeveloperModule(new BundleComponentManager());
        }
        */
      }
    }

    private void useDeveloperModule(ModuleObject obj){
      mainTable.add(obj,2,1);
    }

    private void useDeveloperModule(String className){
      try{
        ModuleObject obj = (ModuleObject)Class.forName(className).newInstance();
        mainTable.add(obj,2,1);
      }
      catch(Exception e){
      }
    }

    private void addToMenu(String displayName, String parameter){
      Link link = new Link(displayName);
      link.addParameter(actionParameter,parameter);
      menuTable.add(link,1,count++);
    }

    private void addToMenu(Class component){
      String className = component.getName();
      addToMenu(component,className.substring(className.lastIndexOf(".")+1));
    }

    private void addToMenu(Class component,String displayName){
      Link link = new Link(displayName);
      link.addParameter(actionParameter,component.getName());
      menuTable.add(link,1,count++);
    }

  }

}