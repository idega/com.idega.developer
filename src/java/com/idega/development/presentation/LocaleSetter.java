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
import com.idega.util.LocaleUtil;
import com.idega.core.data.ICLocale;
import com.idega.core.localisation.business.ICLocaleBusiness;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSetter extends PresentationObjectContainer {


  public static String localesParameter="iw_localeswitcher_locale";
  private static String action = "iw_localeswitcher_sub_action";
  private int count = 0;
  private IWResourceBundle iwrb = null;

  public LocaleSetter(){
  }

  public void main(IWContext iwc){
      add(IWDeveloper.getTitleTable(this.getClass()));
      iwrb = getResourceBundle(iwc);

      if(iwc.getParameter("save")!= null)
        save(iwc);

			Locale defLocale = iwc.getApplicationSettings().getDefaultLocale();
			ICLocale icDefLocale = ICLocaleBusiness.getICLocale(defLocale);

      Form form = new Form();
      Table T = new Table();
      T.add(IWDeveloper.getText("Use"),1,1);
      T.add(IWDeveloper.getText("Country"),2,1);
      T.add(IWDeveloper.getText("Language"),3,1);
			T.add(IWDeveloper.getText("Region"),4,1);
			T.add(IWDeveloper.getText("Default"),5,1);

      count = 1;
      addToTable(T,ICLocaleBusiness.listOfLocales(true),icDefLocale);
      SubmitButton save = new SubmitButton("save","Save");
      count++;
      T.add(save,1,count);
      count++;
      addToTable(T,ICLocaleBusiness.listOfLocales(false),null);
      T.add(new HiddenInput("loc_count",String.valueOf(count)));
      T.setCellpadding(2);
      //T.setBorder(1);
      form.add(T);
      add(form);
  }

  private void addToTable(Table T,List listOfLocales,ICLocale defLocale){
    if(listOfLocales != null){
      CheckBox chk;
			RadioButton rb;
      ICLocale icLocale;
      Locale javaLocale;
      Iterator I = listOfLocales.iterator();
      while(I.hasNext()){
        count++;
        icLocale = (ICLocale) I.next();
        javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());
        chk = new CheckBox("loc_chk"+count,String.valueOf(icLocale.getID()));
        chk.setChecked(icLocale.getInUse());
        T.add(chk,1,count);
        T.add(IWDeveloper.getText(javaLocale.getDisplayCountry()),2,count);
        T.add(IWDeveloper.getText(javaLocale.getDisplayLanguage()),3,count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayVariant()),4,count);
				if(defLocale != null && icLocale.getInUse() ){
					rb = new RadioButton("default_locale",icLocale.getName());
					T.add(rb,5,count);
					if( defLocale.getID() == icLocale.getID() )
					  rb.setSelected();
				}
      }
    }
  }

  private void save( IWContext iwc){
    String sCount = iwc.getParameter("loc_count");

    if(sCount != null){
      java.util.Vector V = new java.util.Vector();
      int count = Integer.parseInt(sCount);
      String chk;
      for (int i = 0; i < count; i++) {
        chk = iwc.getParameter("loc_chk"+i);
        if(chk != null){
          V.add(chk);
        }
      }
      ICLocaleBusiness.makeLocalesInUse(V);

			String sDefLocale = iwc.getParameter("default_locale");
			if(sDefLocale != null )
			  iwc.getApplicationSettings().setDefaultLocale(ICLocaleBusiness.getLocaleFromLocaleString(sDefLocale));
    }
  }

}