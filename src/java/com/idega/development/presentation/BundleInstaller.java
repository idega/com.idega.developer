package com.idega.development.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class BundleInstaller extends JModuleObject {

  private static final String NEW_BUNDLE_PARAMETER = "iw_b_i";
  private static final String NEW_BUNDLE_NAME_PARAMETER="iw_b_i_n_b_n";
  private static final String NEW_BUNDLE_PATH_PARAMETER="iw_b_i_n_b_p";

  public BundleInstaller() {
  }

  public void main(ModuleInfo modinfo){

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(3,2);
      form.add(table);
      TextInput name = new TextInput(this.NEW_BUNDLE_NAME_PARAMETER);
      TextInput path = new TextInput(this.NEW_BUNDLE_PATH_PARAMETER);

      table.add("Create New Bundle",1,1);
      table.add("Bundle Identifier",1,2);
      table.add(name,1,2);
      table.add("Bundle Path (relative to /idegaweb)",2,2);
      table.add(path,2,2);
      table.add(new SubmitButton("Create",this.NEW_BUNDLE_PARAMETER,"save"),3,2);

      doBusiness(modinfo);
  }

  private void doBusiness(ModuleInfo modinfo){
      String check = modinfo.getParameter(NEW_BUNDLE_PARAMETER);
      if(check!=null){
        String bundleIdentifier = modinfo.getParameter(this.NEW_BUNDLE_NAME_PARAMETER);
        String bundlePath = modinfo.getParameter(this.NEW_BUNDLE_PATH_PARAMETER);
        IWMainApplication iwma = modinfo.getApplication();
        iwma.registerBundle(bundleIdentifier,bundlePath);
        add("Creation Successful");
      }
  }


}