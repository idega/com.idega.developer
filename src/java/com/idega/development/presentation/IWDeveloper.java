package com.idega.development.presentation;


import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.app.*;
import com.idega.idegaweb.IWConstants;


/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWDeveloper extends com.idega.jmodule.object.app.IWApplication {

  private static final String localizerParameter = "iw_localizer";
  private static final String localeswitcherParameter = "iw_localeswitcher";
  private static final String bundleCreatorParameter = "iw_bundlecreator";
  private static final String bundleComponentManagerParameter = "iw_bundlecompmanager";
  private static final String applicationPropertiesParameter = "iw_application_properties_setter";
  private static final String bundlesPropertiesParameter = "iw_bundle_properties_setter";
  public static final String actionParameter = "iw_developer_action";
  public static final String dbPoolStatusViewerParameter = "iw_poolstatus_viewer";
  public static final String frameName = "rightFrame";


  public IWDeveloper() {
    super("idegaWeb Developer");
    add(IWDeveloper.IWDevPage.class);
    super.setResizable(true);
    super.setScrollbar(false);
    super.setScrolling(1,false);
    super.setWidth(800);
    super.setHeight(600);
  }

  public static class IWDevPage extends com.idega.idegaweb.presentation.IWAdminWindow{

    public IWDevPage(){
    }

    private Table mainTable;
    private Table objectTable;
    private IFrame rightFrame;
    private int count = 1;

    public void main(ModuleInfo modinfo)throws Exception{
      super.main(modinfo);

      mainTable = new Table(2,1);
        mainTable.setHeight("100%");
        mainTable.setWidth("100%");
        mainTable.setWidth(1,"200");
        mainTable.setWidth(2,"100%");
        mainTable.setCellpadding(3);
        mainTable.setCellspacing(0);
        mainTable.setAlignment(1,1,"center");
        mainTable.setVerticalAlignment(1,1,"top");
        mainTable.setVerticalAlignment(2,1,"top");
        mainTable.setColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      add(mainTable);

      IFrame menuFrame = new IFrame("menu",DeveloperList.class);
        menuFrame.setWidth(200);
        menuFrame.setHeight(150);
        menuFrame.setScrolling(IFrame.SCROLLING_YES);
      mainTable.add(menuFrame,1,1);

      rightFrame = new IFrame(frameName);
        rightFrame.setWidth("100%");
        rightFrame.setHeight("100%");
        rightFrame.setScrolling(IFrame.SCROLLING_YES);
      mainTable.add(rightFrame,2,1);

    }
  }

  public static Table getTitleTable(String displayString, Image image) {
    Table titleTable = new Table(1,2);
      titleTable.setCellpadding(0);
      titleTable.setCellspacing(0);
      titleTable.setWidth("100%");

    Text headline = getText(displayString);
      headline.setFontSize(Text.FONT_SIZE_14_HTML_4);
      headline.setFontColor("#0E2456");

    if ( image != null ) {
      image.setHorizontalSpacing(5);
      titleTable.add(image,1,1);
    }

    titleTable.add(headline,1,1);
    titleTable.add(new HorizontalRule("100%",2,"color: #FF9310",true),1,2);

    return titleTable;
  }

  public static Table getTitleTable(String displayString) {
    return getTitleTable(displayString,null);
  }

  public static Table getTitleTable(Class classToUse, Image image) {
    return getTitleTable(classToUse.getName().substring(classToUse.getName().lastIndexOf(".")+1),image);
  }

  public static Table getTitleTable(Class classToUse) {
    return getTitleTable(classToUse,null);
  }

  public static Text getText(String text) {
    Text T = new Text(text);
      T.setBold();
      T.setFontFace(Text.FONT_FACE_VERDANA);
      T.setFontSize(Text.FONT_SIZE_10_HTML_2);
    return T;
  }
}