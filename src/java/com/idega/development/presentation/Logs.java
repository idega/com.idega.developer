package com.idega.development.presentation;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageCacher;
import com.idega.business.IBOLookup;
import com.idega.data.IDOContainer;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.util.IWTimestamp;
import com.idega.util.FileUtil;
import java.io.File;
/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class Logs extends Block{
  private static final String PARAM_VIEW_OUT_LOG = "iw_dev_view_out_log";
  private static final String PARAM_VIEW_ERR_LOG = "iw_dev_view_err_log";
  private static final String PARAM_CLEAR_OUT_LOG = "iw_dev_clear_out_log";
  private static final String PARAM_CLEAR_ERR_LOG = "iw_dev_clear_err_log";

  public Logs(){}

  public void main(IWContext iwc) throws Exception
  {
          add(IWDeveloper.getTitleTable(this.getClass()));
          Form form = new Form();
          form.maintainParameter(IWDeveloper.actionParameter);
          add(form);
          Table table = new Table(1,2);
          table.setHeight(1,1,"30");
          table.setHeight(1,2,Table.HUNDRED_PERCENT);
          table.setAlignment(1,1,Table.HORIZONTAL_ALIGN_LEFT);
          table.setAlignment(1,2,Table.HORIZONTAL_ALIGN_LEFT);
          form.add(table);

          SubmitButton viewOut = new SubmitButton(PARAM_VIEW_OUT_LOG,"View Out Log");
          SubmitButton clearOut = new SubmitButton(PARAM_CLEAR_OUT_LOG,"Clear Out Log");
          SubmitButton viewErr = new SubmitButton(PARAM_VIEW_ERR_LOG,"View Error Log");
          SubmitButton clearErr = new SubmitButton(PARAM_VIEW_ERR_LOG,"Clear Error Log");
          table.add(viewOut, 1, 1);
          table.add(viewErr, 1, 1);
          table.add(clearOut, 1, 1);
          table.add(clearErr, 1, 1);

          processBusiness(iwc,table);


  }
  private void processBusiness(IWContext iwc,Table table) throws Exception{
    String tomcatLogDir = System.getProperty("user.dir")+FileUtil.getFileSeparator()+".."+FileUtil.getFileSeparator()+"logs"+FileUtil.getFileSeparator();

    if(iwc.isParameterSet(PARAM_VIEW_OUT_LOG)){
      tomcatLogDir = tomcatLogDir+"out.log";
      table.add("<pre>"+FileUtil.getStringFromFile(tomcatLogDir)+"</pre>",1,2);
    }
    else if( iwc.isParameterSet(PARAM_VIEW_ERR_LOG) ){
      tomcatLogDir = tomcatLogDir+"err.log";
      table.add("<pre>"+FileUtil.getStringFromFile(tomcatLogDir)+"</pre>",1,2);
    }
    else if( iwc.isParameterSet(PARAM_CLEAR_OUT_LOG) ){
      tomcatLogDir = tomcatLogDir+"out.log";
      FileUtil.delete(tomcatLogDir);
      FileUtil.createFile(tomcatLogDir);
      table.add("<b>Out log cleared!</b><br>",1,2);
      table.add("<pre>"+FileUtil.getStringFromFile(tomcatLogDir)+"</pre>",1,2);
    }
    else if( iwc.isParameterSet(PARAM_CLEAR_ERR_LOG) ){
      tomcatLogDir = tomcatLogDir+"err.log";
      FileUtil.delete(tomcatLogDir);
      FileUtil.createFile(tomcatLogDir);
      table.add("<b>Error log cleared!</b><br>",1,2);
      table.add("<pre>"+FileUtil.getStringFromFile(tomcatLogDir)+"</pre>",1,2);
    }


  }
}
