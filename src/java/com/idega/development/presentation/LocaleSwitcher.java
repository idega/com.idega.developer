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
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import com.idega.util.LocaleUtil;
import com.idega.core.localisation.business.ICLocaleBusiness;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSwitcher extends com.idega.idegaweb.presentation.LocaleChanger {

  public final static String IW_BUNDLE_IDENTIFIER="com.idega.developer";



  public void make(IWContext iwc){
    if(showLinks)
      doLinkView(iwc);
    else
      doDeveloperView(iwc);

  }

  private void doDeveloperView(IWContext iwc){
    add(IWDeveloper.getTitleTable(this.getClass()));
    IWMainApplication iwma = iwc.getApplication();

    DropdownMenu localesDrop = Localizer.getAvailableLocalesDropdown(iwma,localesParameter);
    localesDrop.keepStatusOnAction();
    localesDrop.setToSubmit();
    localesDrop.setSelectedElement(iwc.getCurrentLocale().toString());

    Form form = new Form();
    form.maintainParameter(IWDeveloper.actionParameter);
    add(form);
    form.add(IWDeveloper.getText("Select language:&nbsp;&nbsp;"));
    form.add(localesDrop);

    Enumeration enum = iwc.getParameterNames();
    while (enum.hasMoreElements()) {
      form.maintainParameter((String)enum.nextElement());
    }

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

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

}
