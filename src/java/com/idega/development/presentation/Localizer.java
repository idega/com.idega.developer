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

public class Localizer extends PresentationObjectContainer {

  private static String bundlesParameter="iw_availablebundles";
  private static String localesParameter="iw_locales";
  private static String stringsParameter="iw_localestrings";
  private static String areaParameter = "iw_stringsarea";
  private static String subAction = "iw_localizer_sub_action";
  private static String newStringKeyParameter = "iw_new_string_key";

  public Localizer(){
  }

  public void main(IWContext iwc){
      add(IWDeveloper.getTitleTable(this.getClass()));

      IWMainApplication iwma = iwc.getApplication();
      DropdownMenu bundlesDrop = getRegisteredDropdown(iwma,bundlesParameter);
      bundlesDrop.keepStatusOnAction();
      bundlesDrop.setToSubmit();
      DropdownMenu localesDrop = getAvailableLocalesDropdown(iwma,localesParameter);
      localesDrop.keepStatusOnAction();
      localesDrop.setToSubmit();

      DropdownMenu stringsDrop;

      String selectedLocale = iwc.getParameter(localesParameter);
      String selectedBundle = iwc.getParameter(bundlesParameter);

      Link templateLink = new Link();
      templateLink.maintainParameter(IWDeveloper.actionParameter,iwc);
      templateLink.maintainParameter(localesParameter,iwc);
      templateLink.maintainParameter(bundlesParameter,iwc);

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table Frame = new Table();
      Table table = new Table(2,6);
        table.setAlignment(2,6,"right");
        table.setColumnVerticalAlignment(1,"top");
        table.setWidth(1,"150");
      Frame.add(table,1,1);
      form.add(Frame);
      table.add(IWDeveloper.getText("Bundle:"),1,1);
      table.add(bundlesDrop,2,1);
      table.add(IWDeveloper.getText("Locale:"),1,2);
      table.add(localesDrop,2,2);

      if(selectedBundle ==null){
        //stringsDrop = new DropdownMenu(stringsParameter);
        table.setAlignment(2,3,"right");
        table.add(new SubmitButton("Get Available Keys",subAction,"choose"),2,3);
      }
      else{

        IWBundle iwb = iwma.getBundle(selectedBundle);
        IWResourceBundle iwrb = iwb.getResourceBundle(LocaleUtil.getLocale(iwc.getParameter(localesParameter)));
        String stringsKey = iwc.getParameter(stringsParameter);
        String areaText = iwc.getParameter(areaParameter);
        String newStringsKey = iwc.getParameter(this.newStringKeyParameter);
        if(stringsKey==null && newStringsKey!=null){
          stringsKey=newStringsKey;
        }

        if(stringsKey!=null){
          String oldStringValue = iwrb.getLocalizedString(stringsKey);
          if(areaText==null){
            PresentationObject area = getTextArea(areaParameter,oldStringValue);
            table.add(area,2,5);
          }
          else{
            if(areaText.equals("")){
              PresentationObject area;
              if(oldStringValue!=null){
                area = getTextArea(areaParameter,oldStringValue);
              }
              else{
                area = getTextArea(areaParameter,"");
              }
              table.add(area,2,5);
            }
           else if(this.isDeleteable(iwc)){
              iwb.removeLocalizableString(stringsKey);
              //boolean b = iwrb.removeString(stringsKey);
              iwrb.storeState();
            }
            else{
              PresentationObject area;
              /**
               * Saving possible
               */
              if(this.isSaveable(iwc)){
                String newKey = iwc.getParameter(newStringKeyParameter);

                if(newKey !=null){
                  if(newKey.equals("")){
                    iwrb.setString(stringsKey,areaText);
                  }
                  else{
                    iwrb.setString(newKey,areaText);
                  }
                }
                area = getTextArea(areaParameter,areaText);
              }
              /**
               * Not Saving
               */
              else{

                //String areaValue = iwrb.getStringChecked(stringsKey);
                String areaValue = iwc.getParameter(this.areaParameter);
                if(areaValue==null){
                  area = getTextArea(areaParameter,"");
                }
                else{
                  if(oldStringValue==null){
                    area = getTextArea(areaParameter,"");
                  }
                  else{
                    area = getTextArea(areaParameter,oldStringValue);
                  }
                }
              }
               table.add(area,2,5);
            }

          }
          table.add(new SubmitButton("Save",subAction,"save"),2,6);
          table.add(new SubmitButton("Delete",subAction,"delete"),2,6);
          table.add(IWDeveloper.getText("New String key:"),1,4);
          table.add(IWDeveloper.getText("New String value:"),1,5);
          TextInput newInput = new TextInput(newStringKeyParameter);
          table.add(newInput,2,4);
        }
        else{
          table.add(getTextArea(areaParameter,""),2,5);
          table.add(new SubmitButton("Save",subAction,"save"),2,6);
          table.add(IWDeveloper.getText("New String key:"),1,4);
          table.add(IWDeveloper.getText("New String value:"),1,5);
          TextInput newInput = new TextInput(newStringKeyParameter);
          table.add(newInput,2,4);
        }

        //table.add(new SubmitButton("Select Locale",subAction,"select"),2,1);
        table.add(IWDeveloper.getText("String:"),1,3);
        stringsDrop = this.getLocalizeableStringsMenu(iwma,selectedBundle,stringsParameter);
        stringsDrop.keepStatusOnAction();
        stringsDrop.setToSubmit();
        table.add(stringsDrop,2,3);
        //table.add(new SubmitButton("Choose String",subAction,"choose"),3,1);

        Frame.add(IWDeveloper.getText("Available Strings:"),1,3);
        Frame.add(Text.getBreak(),1,3);
        Frame.add(this.getLocalizeableStringsTable(iwma,selectedBundle,iwrb,stringsParameter,templateLink),1,3);

      }
  }

  public static DropdownMenu getAvailableLocalesDropdown(IWMainApplication iwma,String name){
    List locales = iwma.getAvailableLocales();
    DropdownMenu down = new DropdownMenu(name);
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      Locale item = (Locale)iter.next();
      down.addMenuElement(item.toString(),item.getDisplayLanguage());
    }
    return down;
  }

  public static Form getAvailableLocalesForm(IWContext iwc) {
    IWMainApplication iwma = iwc.getApplication();

    Form myForm = new Form();
      myForm.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
    DropdownMenu down = getAvailableLocalesDropdown(iwma,LocaleSwitcher.localesParameter);
      down.keepStatusOnAction();
      down.setToSubmit();
      myForm.add(down);

    return myForm;
  }

  public static DropdownMenu getAvailableLocalesDropdown(IWContext iwc) {
    IWMainApplication iwma = iwc.getApplication();

    DropdownMenu down = getAvailableLocalesDropdown(iwma,com.idega.core.localisation.business.LocaleSwitcher.languageParameterString);
      down.keepStatusOnAction();
      down.setToSubmit();

    return down;
  }

   public static Table getLocalizeableStringsTable(IWMainApplication iwma,String bundleIdentifier, IWResourceBundle iwrb,String parameterName,Link templateLink){
    IWBundle bundle = iwma.getBundle(bundleIdentifier);
    String[] strings = bundle.getLocalizableStrings();
    Table table = new Table(2,strings.length);
      table.setColumnVerticalAlignment(1,"top");
      table.setCellpadding(5);
    String localizedString;
    Link name;
    for (int i = 0; i < strings.length; i++) {
      //name = new Text(strings[i],true,false,false);
      //name = new Link(strings[i]);
      name = (Link)templateLink.clone();
      name.setText(strings[i]);
      name.setBold();
      name.addParameter(parameterName,strings[i]);
      table.add(name,1,i+1);
      localizedString = iwrb.getLocalizedString(strings[i]);
      if (localizedString==null) localizedString = "";
      table.add(localizedString ,2,i+1);
    }
    table.setWidth(400);
    table.setColor("#9FA9B3");
    return table;
  }

  public static DropdownMenu getRegisteredDropdown(IWMainApplication iwma,String name){
    return BundlePropertySetter.getRegisteredBundlesDropdown(iwma,name);
  }

  public static DropdownMenu getLocalizeableStringsMenu(IWMainApplication iwma,String bundleIdentifier,String name){
    IWBundle bundle = iwma.getBundle(bundleIdentifier);
    String[] strings = bundle.getLocalizableStrings();
    DropdownMenu down = new DropdownMenu(name);
    for (int i = 0; i < strings.length; i++) {
      down.addMenuElement(strings[i]);
    }
    return down;
  }

  private boolean isSaveable(IWContext iwc){
      String subActioner = iwc.getParameter(subAction);
      if(subActioner==null){
        return false;
      }
      else{
        if(subActioner.equals("save")){
          return true;
        }
        return false;
      }
  }

  private boolean isDeleteable(IWContext iwc){
      String subActioner = iwc.getParameter(subAction);
      if(subActioner==null){
        return false;
      }
      else{
        if(subActioner.equals("delete")){
          return true;
        }
        return false;
      }
  }

  private PresentationObject getTextArea(String name,String startValue){
      TextArea area = new TextArea(name,startValue);
      area.setWidth(30);
      return area;
  }
}