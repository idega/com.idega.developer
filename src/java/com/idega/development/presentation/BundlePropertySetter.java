package com.idega.development.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import com.idega.idegaweb.IWBundle;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class BundlePropertySetter extends Block {

  private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
  private static final String BUNDLE_PARAMETER = "iw_b_p_s";
  private static final String PROPERTY_KEY_NAME_PARAMETER="iw_b_p_s_k";
  private static final String PROPERTY_VALUE_PARAMETER="iw_b_p_s_v";
  private Table table;
  private IWBundle iwb;

  public BundlePropertySetter() {
  }

  public void main(IWContext iwc){
    iwb = getBundle(iwc);
    add(IWDeveloper.getTitleTable(this.getClass()));

    IWMainApplication iwma = iwc.getApplication();
    DropdownMenu bundles = getRegisteredBundlesDropdown(iwma,BUNDLE_PARAMETER);
    bundles.keepStatusOnAction();
    bundles.setToSubmit();

    Form form = new Form();
    form.maintainParameter(IWDeveloper.actionParameter);
    add(form);
    table = new Table(2,5);
      table.setWidth(1,"160");
      table.mergeCells(1,1,2,1);
      table.setAlignment(2,5,"right");
    form.add(table);
    //form.setMethod("GET");
    TextInput name = new TextInput(this.PROPERTY_KEY_NAME_PARAMETER);
    TextInput value = new TextInput(this.PROPERTY_VALUE_PARAMETER);

    table.add(IWDeveloper.getText("Set BundleProperty"),1,1);
    table.add(IWDeveloper.getText("Bundle:"),1,2);
    table.add(bundles,2,2);

    table.add(IWDeveloper.getText("Property Key Name:"),1,3);
    table.add(name,2,3);
    table.add(IWDeveloper.getText("Property Key Value:"),1,4);
    table.add(value,2,4);

    table.add(new SubmitButton("Save","save"),2,5);
    table.add(new SubmitButton("Reload","reload"),2,5);

    doBusiness(iwc);
  }

  private void doBusiness(IWContext iwc){
      String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);
      String save = iwc.getParameter("Save");
      String reload = iwc.getParameter("Reload");
      String mode = iwc.getParameter("mode");
      IWMainApplication iwma = iwc.getApplication();
      IWBundle bundle = null;
      if ( bundleIdentifier != null )
	bundle = iwma.getBundle(bundleIdentifier);

      if ( mode != null ) {
	String[] values = iwc.getParameterValues("property");
	if ( values != null && bundle != null ) {
	  for ( int a = 0; a < values.length; a++ ) {
	    bundle.removeProperty(values[a]);
	  }
	}
      }

      if((bundleIdentifier!=null)&&(save!=null)){
	String KeyName = iwc.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
	String KeyValue = iwc.getParameter(this.PROPERTY_VALUE_PARAMETER);
	bundle.setProperty(KeyName,KeyValue);
	bundle.storeState();
	add(IWDeveloper.getText("Status: "));
	add("Property set successfully and saved to files");
	add(Text.getBreak());
	add(Text.getBreak());
	add(IWDeveloper.getText("Available Keys:"));
	add(getParametersTable(bundle,bundleIdentifier));
      }
      else if( (bundleIdentifier!= null) && (save==null) ){
	  if(reload!=null){
	    iwma.getBundle(bundleIdentifier).reloadBundle();
	    add(IWDeveloper.getText("Status: "));
	    add("Bundle reloaded from files");
	    add(Text.getBreak());
	    add(Text.getBreak());
	  }
	  add(IWDeveloper.getText("Available BundleProperties:"));
	 add(getParametersTable(bundle,bundleIdentifier));
      }
  }

  public static DropdownMenu getRegisteredBundlesDropdown(IWMainApplication iwma,String name){
    List bundles = iwma.getRegisteredBundles();
    Collections.sort(bundles);
    DropdownMenu down = new DropdownMenu(name);
    Iterator iter = bundles.iterator();
    while (iter.hasNext()) {
      IWBundle item = (IWBundle)iter.next();
      down.addMenuElement(item.getBundleIdentifier());
    }
    return down;
  }

   public static Form getParametersTable(IWBundle bundle,String bundleIdentifier){
    String[] strings = bundle.getAvailableProperties();

    Form form = new Form();
    form.setMethod("get");
    form.add(new HiddenInput(BUNDLE_PARAMETER,bundleIdentifier));

    Table table = new Table(3,strings.length+1);
      table.setColumnVerticalAlignment(1,"top");
      table.setCellpadding(5);
      table.setCellspacing(0);
      table.setColumnAlignment(3,"center");
    String localizedString;
    Text name;
    for (int i = 0; i < strings.length; i++) {
      name = new Text(strings[i],true,false,false);
      table.add(name,1,i+1);
      localizedString = bundle.getProperty( strings[i] );
      if (localizedString==null) localizedString = "";
      table.add(localizedString ,2,i+1);
      table.add(new CheckBox("property",strings[i]),3,i+1);
    }

    table.setWidth(400);
    table.setColor("#9FA9B3");
    table.setRowColor(strings.length+1,"#FFFFFF");
    table.add(new SubmitButton("Delete","mode","delete"),3,strings.length+1);
    form.add(table);

    return form;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

}