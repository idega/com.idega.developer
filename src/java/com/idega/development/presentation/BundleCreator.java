package com.idega.development.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import java.io.File;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class BundleCreator extends Block {

  private static final String NEW_BUNDLE_PARAMETER = "iw_b_i";
  private static final String NEW_BUNDLE_NAME_PARAMETER="iw_b_i_n_b_n";
  private static final String NEW_BUNDLE_PATH_PARAMETER="iw_b_i_n_b_p";

  public BundleCreator() {
  }

  public void main(IWContext iwc)throws Exception{
      add(IWDeveloper.getTitleTable(this.getClass()));

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(2,3);
        table.setAlignment(2,3,"right");
      form.add(table);
      TextInput name = new TextInput(this.NEW_BUNDLE_NAME_PARAMETER);
      //TextInput path = new TextInput(this.NEW_BUNDLE_PATH_PARAMETER);

      table.add(IWDeveloper.getText("Create New Bundle"),1,1);
      table.add(IWDeveloper.getText("Bundle Identifier"),1,2);
      table.add(name,2,2);
      //table.add("Bundle Directory Name",2,2);
      //table.add(path,2,2);
      table.add(new Parameter(NEW_BUNDLE_PARAMETER,"dummy"));
      table.add(new SubmitButton("Create",this.NEW_BUNDLE_PARAMETER,"save"),2,3);
      doBusiness(iwc);
  }

  private void doBusiness(IWContext iwc)throws Exception{
      String check = iwc.getParameter(NEW_BUNDLE_PARAMETER);
      if(check!=null){
        String bundleIdentifier = iwc.getParameter(this.NEW_BUNDLE_NAME_PARAMETER);
        //String bundleDir = iwc.getParameter(this.NEW_BUNDLE_PATH_PARAMETER);
        String bundleDir = bundleIdentifier+".bundle";
        IWMainApplication iwma = iwc.getApplication();
        if(bundleDir.indexOf(IWMainApplication.BUNDLES_STANDARD_DIRECTORY)==-1){
          bundleDir=IWMainApplication.BUNDLES_STANDARD_DIRECTORY + File.separator + bundleDir;
        }
        iwma.registerBundle(bundleIdentifier,bundleDir,true);
        add(IWDeveloper.getText("Creation Successful"));
      }
  }
}