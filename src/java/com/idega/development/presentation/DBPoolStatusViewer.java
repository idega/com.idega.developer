package com.idega.development.presentation;

import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.util.PresentationUtil;
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


  @Override
	public void main(IWContext iwc){
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
      PoolManager poolMgr = PoolManager.getInstance();
      add(poolMgr.getStats());
  }

}
