package com.idega.development.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.util.database.PoolManager;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class DBPoolStatusViewer extends PresentationObjectContainer {

  public DBPoolStatusViewer() {
  }


  public void main(IWContext iwc){
      PoolManager poolMgr = PoolManager.getInstance();
      add(poolMgr.getStats());
  }

}
