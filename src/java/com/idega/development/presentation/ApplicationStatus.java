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
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */



public class ApplicationStatus extends Block {

  private static final String RESTART_PARAMETER = "iw_app_re";

  public ApplicationStatus() {
  }

  public void main(IWContext iwc)throws Exception{
      add(IWDeveloper.getTitleTable(this.getClass()));

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(1,2);
      table.setAlignment(1,2,"right");
      form.add(table);

      SubmitButton restart = new SubmitButton("Restart",RESTART_PARAMETER,"true");
      table.add(IWDeveloper.getText("Restart application"),1,1);
      table.add(restart,1,2);
      doBusiness(iwc);
  }

  private void doBusiness(IWContext iwc)throws Exception{
      String check = iwc.getParameter(RESTART_PARAMETER);
      if(check!=null){
        add(IWDeveloper.getText("Done Restarting!"));
        iwc.getApplication().restartApplication();
      }
  }
}