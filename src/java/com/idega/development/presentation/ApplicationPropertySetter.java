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
  private static final String AUTOCREATE_STRINGS_PARAMETER="iw_a_c_s_p";
  private static final String AUTOCREATE_PROPERTIES_PARAMETER="iw_a_c_p_p";
  private static final String DEBUG_PARAMETER="iw_d_p";


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
      Table table = new Table(2,8);
	table.setCellpadding(5);
	table.mergeCells(1,1,2,1);
	table.mergeCells(1,8,2,8);
	table.setAlignment(1,8,"right");
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

      CheckBox box3 = new CheckBox(AUTOCREATE_STRINGS_PARAMETER);
      if(iwma.getSettings().isAutoCreateStringsActive()){
       box3.setChecked(true);
      }
      table.add(IWDeveloper.getText("Autocreate Localized Strings:"),1,5);
      table.add(box3,2,5);

      CheckBox box4 = new CheckBox(AUTOCREATE_PROPERTIES_PARAMETER);
      if(iwma.getSettings().isAutoCreatePropertiesActive()){
       box4.setChecked(true);
      }
      table.add(IWDeveloper.getText("Autocreate Properties:"),1,6);
      table.add(box4,2,6);

      CheckBox box2 = new CheckBox(DEBUG_PARAMETER);
      if(iwma.getSettings().getIfDebug()){
       box2.setChecked(true);
      }
      table.add(IWDeveloper.getText("Debug:"),1,7);
      table.add(box2,2,7);
      table.add(new SubmitButton("Save",APPLICATION_SETTER_PARAMETER,"save"),1,8);
      table.add(new SubmitButton("Store Application state",APPLICATION_SETTER_PARAMETER,"store"),1,8);

      add(getParametersTable(iwma));
  }

  private void doBusiness(IWContext iwc){
      String[] values = iwc.getParameterValues("property");
      if ( values != null ) {
	for ( int a = 0; a < values.length; a++ ) {
	  iwc.getApplication().getSettings().removeProperty(values[a]);
	}
      }
      String setterState = iwc.getParameter(APPLICATION_SETTER_PARAMETER);
      if(setterState!=null){
	String entityAutoCreate = iwc.getParameter(ENTITY_AUTOCREATE_PARAMETER);
	String autoCreateStrings = iwc.getParameter(AUTOCREATE_STRINGS_PARAMETER);
	String autoCreateProperties = iwc.getParameter(AUTOCREATE_PROPERTIES_PARAMETER);
	String debug = iwc.getParameter(DEBUG_PARAMETER);
	String KeyName = iwc.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
	String KeyValue = iwc.getParameter(this.PROPERTY_VALUE_PARAMETER);
	if ( KeyName != null && KeyName.length() > 0 )
	  iwc.getApplication().getSettings().setProperty(KeyName,KeyValue);

	if(entityAutoCreate!=null){
	  iwc.getApplication().getSettings().setEntityAutoCreation(true);
	}
	else
	  iwc.getApplication().getSettings().setEntityAutoCreation(false);

	if(autoCreateStrings!=null){
	  iwc.getApplication().getSettings().setAutoCreateStrings(true);
	}
	else
	  iwc.getApplication().getSettings().setAutoCreateStrings(false);

	if(autoCreateProperties!=null){
	  iwc.getApplication().getSettings().setAutoCreateProperties(true);
	}
	else
	  iwc.getApplication().getSettings().setAutoCreateProperties(false);

	if(debug!=null){
	  iwc.getApplication().getSettings().setDebug(true);
	}
	else {
	  iwc.getApplication().getSettings().setDebug(false);
	}

	if(setterState.equalsIgnoreCase("store")){
	  iwc.getApplication().storeStatus();
	}

	add(IWDeveloper.getText("Status: "));
	add("Property set successfully");
      }
  }

  public static Form getParametersTable(IWMainApplication iwma){
    java.util.Iterator iter =  iwma.getSettings().getIWPropertyListIterator();

    Form form = new Form();
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
      table.add(new CheckBox("property",property.getName()),3,row);
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
    table.setCellspacing(0);
    table.setWidth(400);
    table.setColor("#9FA9B3");
    table.setRowColor(table.getRows()+1,"#FFFFFF");
    table.add(new SubmitButton("Delete","mode","delete"),3,table.getRows());
    table.setColumnAlignment(3,"center");
    form.add(table);

    return form;
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
