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



public class BundlePropertySetter extends JModuleObject {

  private static final String BUNDLE_PARAMETER = "iw_b_p_s";
  private static final String PROPERTY_KEY_NAME_PARAMETER="iw_b_p_s_k";
  private static final String PROPERTY_VALUE_PARAMETER="iw_b_p_s_v";

  public BundlePropertySetter() {
  }

  public void main(ModuleInfo modinfo){

      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundles = getRegisteredBundlesDropdown(iwma,BUNDLE_PARAMETER);


      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(3,2);
      form.add(table);
      TextInput name = new TextInput(this.PROPERTY_KEY_NAME_PARAMETER);
      TextInput value = new TextInput(this.PROPERTY_VALUE_PARAMETER);

      table.add("Set BundleProperty",1,1);
      table.add("Bundle:",2,1);
      table.add(bundles,2,1);

      table.add("Property Key Name",1,2);
      table.add(name,1,2);
      table.add("Property Key Value",2,2);
      table.add(value,2,2);
      table.add(new SubmitButton("Save","save"),3,2);

      doBusiness(modinfo);
  }

  private void doBusiness(ModuleInfo modinfo){
      String bundleIdentifier = modinfo.getParameter(BUNDLE_PARAMETER);
      if(bundleIdentifier!=null){
        String KeyName = modinfo.getParameter(this.PROPERTY_KEY_NAME_PARAMETER);
        String KeyValue = modinfo.getParameter(this.PROPERTY_VALUE_PARAMETER);
        IWMainApplication iwma = modinfo.getApplication();
        iwma.getBundle(bundleIdentifier).setProperty(KeyName,KeyValue);
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