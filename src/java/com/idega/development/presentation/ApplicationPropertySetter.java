package com.idega.development.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import java.util.List;
import java.util.Iterator;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class ApplicationPropertySetter extends JModuleObject {

  private static final String APPLICATION_SETTER_PARAMETER = "iw_a_p_s";
  private static final String PROPERTY_KEY_NAME_PARAMETER="iw_a_p_s_k";
  private static final String PROPERTY_VALUE_PARAMETER="iw_a_p_s_v";
  private static final String ENTITY_AUTOCREATE_PARAMETER="iw_e_a_c_p";


  public ApplicationPropertySetter() {
  }

  public void main(ModuleInfo modinfo){
      add(IWDeveloper.getTitleTable(this.getClass()));

      doBusiness(modinfo);

      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundles = getRegisteredBundlesDropdown(iwma,APPLICATION_SETTER_PARAMETER);

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(2,5);
        table.setCellpadding(5);
        table.mergeCells(1,1,2,1);
        table.mergeCells(1,5,2,5);
        table.setAlignment(1,5,"right");
      form.add(table);
      TextInput name = new TextInput(this.PROPERTY_KEY_NAME_PARAMETER);
      TextInput value = new TextInput(this.PROPERTY_VALUE_PARAMETER);

      table.add(IWDeveloper.getText("Set ApplicationProperty"),1,1);

      table.add(IWDeveloper.getText("Property Key Name:"),1,2);
      table.add(name,2,2);
      table.add(IWDeveloper.getText("Property Key Value:"),1,3);
      table.add(value,2,3);
      CheckBox box = new CheckBox(ENTITY_AUTOCREATE_PARAMETER);
      if(iwma.getSettings().getIfEntityAutoCreate()){
       box.setChecked(true);
      }
      table.add(IWDeveloper.getText("Autocreate Data Entities:"),1,4);
      table.add(box,2,4);
      table.add(new SubmitButton("Save",APPLICATION_SETTER_PARAMETER,"save"),1,5);
      table.add(new SubmitButton("Store Application state",APPLICATION_SETTER_PARAMETER,"store"),1,5);
  }

  private void doBusiness(ModuleInfo modinfo){
      String setterState = modinfo.getParameter(APPLICATION_SETTER_PARAMETER);
      if(setterState!=null){
        String entityAutoCreate = modinfo.getParameter(ENTITY_AUTOCREATE_PARAMETER);
        String KeyName = modinfo.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
        String KeyValue = modinfo.getParameter(this.PROPERTY_VALUE_PARAMETER);
        modinfo.getApplication().getSettings().setProperty(KeyName,KeyValue);
        if(entityAutoCreate!=null){
          if(entityAutoCreate.equalsIgnoreCase("Y")){
            modinfo.getApplication().getSettings().setEntityAutoCreation(true);
          }
          else{
            modinfo.getApplication().getSettings().setEntityAutoCreation(true);
          }
        }
        if(setterState.equalsIgnoreCase("store")){
          modinfo.getApplication().storeStatus();
        }

        add(IWDeveloper.getText("Status: "));
        add("Property set successfully");
      }
  }

  public static DropdownMenu getRegisteredBundlesDropdown(IWMainApplication iwma,String name){
    List locales = iwma.getRegisteredBundles();
    DropdownMenu down = new DropdownMenu(name);
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      IWBundle item = (IWBundle)iter.next();
      down.addMenuElement(item.getBundleIdentifier());
    }
    return down;
  }
}