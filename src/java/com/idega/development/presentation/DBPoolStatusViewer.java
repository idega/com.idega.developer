package com.idega.development.presentation;

import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.util.database.PoolManager;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class DBPoolStatusViewer extends ModuleObjectContainer {

  public DBPoolStatusViewer() {
  }


  public void main(ModuleInfo modinfo){
      /*IWMainApplication iwma = modinfo.getApplication();

      DropdownMenu localesDrop = Localizer.getAvailableLocalesDropdown(iwma,localesParameter);
      localesDrop.keepStatusOnAction();
      localesDrop.setToSubmit();

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      form.add(localesDrop);

      doBusiness(modinfo);*/
      PoolManager poolMgr = PoolManager.getInstance();
      add(poolMgr.getStats());

  }


}