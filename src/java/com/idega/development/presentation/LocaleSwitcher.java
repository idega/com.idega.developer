package com.idega.development.presentation;

import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.jmodule.object.interfaceobject.DropdownMenu;
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

public class LocaleSwitcher extends ModuleObjectContainer {


  public static String localesParameter="iw_localeswitcher_locale";

  private static String action = "iw_localeswitcher_sub_action";


  public LocaleSwitcher(){
  }

  public void main(ModuleInfo modinfo){
      IWMainApplication iwma = modinfo.getApplication();

      DropdownMenu localesDrop = Localizer.getAvailableLocalesDropdown(iwma,localesParameter);
      localesDrop.keepStatusOnAction();
      localesDrop.setToSubmit();

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      form.add(localesDrop);

      doBusiness(modinfo);

      add("Current Locale: "+modinfo.getCurrentLocale().getDisplayName()+" ("+modinfo.getCurrentLocale().toString()+")");
  }

  private void doBusiness(ModuleInfo modinfo){
      String localeValue = modinfo.getParameter(localesParameter);
      if(localeValue!=null){
        Locale locale = LocaleUtil.getLocale(localeValue);
        if(locale!=null){
          modinfo.setCurrentLocale(locale);
        }
      }
  }

}