package com.idega.development.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;

import com.idega.util.StringHandler;

import com.idega.core.data.ICObject;

import java.util.List;
import java.util.Iterator;



/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class ComponentManager extends JModuleObject {

  private static final String BUNDLE_PARAMETER = "iw_b_p_s";
  //private static final String PROPERTY_KEY_NAME_PARAMETER="iw_b_p_s_k";
  //private static final String PROPERTY_VALUE_PARAMETER="iw_b_p_s_v";

  private static final String CLASS_INPUT_NAME = "iw_bundle_comp_class";
  private static final String TYPE_INPUT_NAME = "iw_bundle_comp_type";
  private static final String DELETE_CHECKBOX_NAME = "iw_bundle_comp_delete";


  public ComponentManager() {
  }

  public void main(ModuleInfo modinfo){
      add(IWDeveloper.getTitleTable(this.getClass()));

      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma,BUNDLE_PARAMETER);
      bundles.keepStatusOnAction();
      bundles.setToSubmit();

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table();
        table.setCellpadding(5);
      Table selectTable = new Table(3,1);
      form.add(selectTable);
      form.add(Text.getBreak());
      form.add(Text.getBreak());
      form.add(table);

      selectTable.add(IWDeveloper.getText("Bundle:"),1,1);
      selectTable.add(bundles,2,1);
      SubmitButton button1 = new SubmitButton("Go");
      selectTable.add(button1,3,1);

      String bundleIdentifier = modinfo.getParameter(BUNDLE_PARAMETER);

      if(bundleIdentifier!=null){

        IWBundle iwb = iwma.getBundle(bundleIdentifier);

        try{
          doBusiness(modinfo,iwb);
        }
        catch(Exception e){
          add("Error: "+e.getClass().getName()+" "+e.getMessage());
          e.printStackTrace();
        }

        DropdownMenu typesDrop = new DropdownMenu(this.TYPE_INPUT_NAME);
        List componentTypes = ICObject.getAvailableComponentTypes();
        Iterator iter = componentTypes.iterator();

        CheckBox deleteBox = new CheckBox(DELETE_CHECKBOX_NAME);

        while (iter.hasNext()) {
          String type = (String)iter.next();
          typesDrop.addMenuElement(type);
        }

        TextInput classesInput = new TextInput(CLASS_INPUT_NAME);

        int index = 2;

        table.add(IWDeveloper.getText("ClassName: "),1,1);
        table.add(IWDeveloper.getText("Type: "),2,1);
        table.add(IWDeveloper.getText("Remove?"),3,1);

        List compList = iwb.getComponentKeys();
        Iterator compIter = compList.iterator();
        while (compIter.hasNext()) {
          String className = (String)compIter.next();
          String type = iwb.getComponentType(className);

          table.add(getSmallText(className),1,index);
          table.add(getSmallText(type),2,index);

          CheckBox rowBox = (CheckBox)deleteBox.clone();
          rowBox.setContent(className);
          table.add(rowBox,3,index);

          index++;
        }

        table.add(classesInput,1,index);
        table.add(typesDrop,2,index);

        table.setAlignment(3,index+1,"right");
        table.add(new SubmitButton("Save","save"),3,index+1);
      }
  }

  private void doBusiness(ModuleInfo modinfo,IWBundle iwb)throws Exception{
      String save = modinfo.getParameter("Save");
      String reload = modinfo.getParameter("Reload");

      if((iwb!=null)&&(save!=null)){

        String newComponentClass = modinfo.getParameter(this.CLASS_INPUT_NAME);
        if(newComponentClass==null){
          newComponentClass=StringHandler.EMPTY_STRING;
        }

        String newComponentType = modinfo.getParameter(this.TYPE_INPUT_NAME);
        if(newComponentType==null){
          newComponentType=StringHandler.EMPTY_STRING;
        }

        String[] deletes = modinfo.getParameterValues(this.DELETE_CHECKBOX_NAME);
        if(deletes!=null){
          for (int i = 0; i < deletes.length; i++) {
            iwb.removeComponent(deletes[i]);
          }
        }

        String emptyString = StringHandler.EMPTY_STRING;

        if(!(emptyString.equals(newComponentClass) || emptyString.equals(newComponentType))){
            Class.forName(newComponentClass);
            iwb.addComponent(newComponentClass,newComponentType);
        }
      }
      else if( (iwb!= null) && (save==null) ){

      }
  }

  private Text getSmallText(String text) {
    Text T = new Text(text);
      T.setFontFace(Text.FONT_FACE_VERDANA);
      T.setFontSize(Text.FONT_SIZE_7_HTML_1);
    return T;
  }
}