package com.idega.development.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;

import com.idega.util.database.PoolManager;
import com.idega.presentation.text.*;

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
      add(IWDeveloper.getTitleTable(this.getClass()));
			if (!iwc.isIE()) {
				getParentPage().setBackgroundColor("#FFFFFF");
			}
      add(Text.getBreak());
      add(Text.getBreak());

      PoolManager poolMgr = PoolManager.getInstance();
      add(poolMgr.getStats());
  }

  private Text getText(String text) {
    Text T = new Text(text);
      T.setBold();
      T.setFontFace(Text.FONT_FACE_VERDANA);
      T.setFontSize(Text.FONT_SIZE_10_HTML_2);
    return T;
  }

}
