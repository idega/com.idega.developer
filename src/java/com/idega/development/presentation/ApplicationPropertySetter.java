package com.idega.development.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWProperty;
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



public class ApplicationPropertySetter extends Block {

  private static final String APPLICATION_SETTER_PARAMETER = "iw_a_p_s";
  private static final String PROPERTY_KEY_NAME_PARAMETER="iw_a_p_s_k";
  private static final String PROPERTY_VALUE_PARAMETER="iw_a_p_s_v";
  private static final String ENTITY_AUTOCREATE_PARAMETER="iw_e_a_c_p";


  public ApplicationPropertySetter() {
  }

  public void main(IWContext iwc){
      add(IWDeveloper.getTitleTable(this.getClass()));

      doBusiness(iwc);

      IWMainApplication iwma = iwc.getApplication();
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

      add(getParametersTable(iwma));
  }

  private void doBusiness(IWContext iwc){
      String setterState = iwc.getParameter(APPLICATION_SETTER_PARAMETER);
      if(setterState!=null){
        String entityAutoCreate = iwc.getParameter(ENTITY_AUTOCREATE_PARAMETER);
        String KeyName = iwc.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
        String KeyValue = iwc.getParameter(this.PROPERTY_VALUE_PARAMETER);
        iwc.getApplication().getSettings().setProperty(KeyName,KeyValue);
        if(entityAutoCreate!=null){
          /*if(entityAutoCreate.equalsIgnoreCase("Y")){
            iwc.getApplication().getSettings().setEntityAutoCreation(true);
          }
          else{
            iwc.getApplication().getSettings().setEntityAutoCreation(true);
          }*/
          // added by Aron 23.01.2001
          iwc.getApplication().getSettings().setEntityAutoCreation(true);
        }
        else
          iwc.getApplication().getSettings().setEntityAutoCreation(false);
        if(setterState.equalsIgnoreCase("store")){
          iwc.getApplication().storeStatus();
        }

        add(IWDeveloper.getText("Status: "));
        add("Property set successfully");
      }
  }

  public static Table getParametersTable(IWMainApplication iwma){
    java.util.Iterator iter =  iwma.getSettings().getIWPropertyListIterator();

    Table table = new Table();

    String localizedString;
    String name;
    String value;
    IWProperty property;
    int row = 1;
    while(iter.hasNext()){
      property = (IWProperty) iter.next();
      table.add(new Text(property.getName(),true,false,false),1,row);
      value = property.getValue();
      if(value!=null)
        table.add(new Text(value,true,false,false),2,row);
      row++;
    }
    /*
    for (int i = 0; i < strings.length; i++) {
      name = new Text(strings[i],true,false,false);
      table.add(name,1,i+1);
      localizedString = bundle.getProperty( strings[i] );
      if (localizedString==null) localizedString = "";
      table.add(localizedString ,2,i+1);
    }
*/
    table.setColumnVerticalAlignment(1,"top");
    table.setCellpadding(5);
    table.setWidth(400);
    table.setColor("#9FA9B3");

    return table;
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