package com.idega.development.presentation;

import com.idega.presentation.ui.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.DropdownMenu;
import java.util.Locale;
import java.util.List;
import java.util.Iterator;
import com.idega.util.LocaleUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSwitcher extends PresentationObjectContainer {


  public static String localesParameter="iw_localeswitcher_locale";

  private static String action = "iw_localeswitcher_sub_action";


  public LocaleSwitcher(){
  }

  public void main(IWContext iwc){
      add(IWDeveloper.getTitleTable(this.getClass()));

      IWMainApplication iwma = iwc.getApplication();

      DropdownMenu localesDrop = Localizer.getAvailableLocalesDropdown(iwma,localesParameter);
      localesDrop.keepStatusOnAction();
      localesDrop.setToSubmit();

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      form.add(IWDeveloper.getText("Select language:&nbsp;&nbsp;"));
      form.add(localesDrop);

      doBusiness(iwc);

      add(IWDeveloper.getText("Current Locale:&nbsp;&nbsp;"));
      add(iwc.getCurrentLocale().getDisplayName()+" ("+iwc.getCurrentLocale().toString()+")");
  }

  private void doBusiness(IWContext iwc){
      String localeValue = iwc.getParameter(localesParameter);
      if(localeValue!=null){
        Locale locale = LocaleUtil.getLocale(localeValue);
        if(locale!=null){
          iwc.setCurrentLocale(locale);
        }
      }
  }
}