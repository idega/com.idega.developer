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



public class BundleComponentManager extends JModuleObject {

  private static final String BUNDLE_PARAMETER = "iw_b_p_s";
  //private static final String PROPERTY_KEY_NAME_PARAMETER="iw_b_p_s_k";
  //private static final String PROPERTY_VALUE_PARAMETER="iw_b_p_s_v";

  private static final String CLASS_INPUT_NAME = "iw_bundle_comp_class";
  private static final String TYPE_INPUT_NAME = "iw_bundle_comp_type";
  private static final String DELETE_CHECKBOX_NAME = "iw_bundle_comp_delete";


  public BundleComponentManager() {
  }

  public void main(ModuleInfo modinfo){



      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma,BUNDLE_PARAMETER);
      bundles.keepStatusOnAction();
      bundles.setToSubmit();

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table();
      form.add(table);


      table.add("Bundle:",1,1);
      table.add(bundles,1,1);
      SubmitButton button1 = new SubmitButton("Go");
      table.add(button1,1,1);

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



        int index = 3;

        table.add("ClassName: ",1,2);
        table.add("Type: ",2,2);
        table.add("Remove ?",3,2);


        List compList = iwb.getComponentKeys();
        Iterator compIter = compList.iterator();
        while (compIter.hasNext()) {
          String className = (String)compIter.next();
          String type = iwb.getComponentType(className);

          table.add(className,1,index);
          table.add(type,2,index);

          CheckBox rowBox = (CheckBox)deleteBox.clone();
          rowBox.setContent(className);
          table.add(rowBox,3,index);

          index++;
        }


        table.add(classesInput,1,index);
        table.add(typesDrop,2,index);


        table.add(new SubmitButton("Save","save"),3,index+1);

      }

  }

  private void getClassEditView(ModuleInfo modinfo){

  }

  private void doBusiness(ModuleInfo modinfo,IWBundle iwb)throws Exception{

      String save = modinfo.getParameter("Save");
      String reload = modinfo.getParameter("Reload");
      //IWMainApplication iwma = modinfo.getApplication();

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

  /*
   public static Table getParametersTable(IWMainApplication iwma,String bundleIdentifier){
    IWBundle bundle = iwma.getBundle(bundleIdentifier);
    String[] strings = bundle.getAvailableProperties();

    Table table = new Table(2,strings.length);
    String localizedString;
    Text name;
    for (int i = 0; i < strings.length; i++) {
      name = new Text(strings[i],true,false,false);
      table.add(name,1,i+1);
      localizedString = bundle.getProperty( strings[i] );
      if (localizedString==null) localizedString = "";
      table.add(localizedString ,2,i+1);
    }

    table.setWidth(300);
    table.setColor("#9FA9B3");

    return table;
  }
  */
}