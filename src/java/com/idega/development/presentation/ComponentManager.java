package com.idega.development.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;
import com.idega.builder.business.IBPropertyHandler;

import com.idega.util.StringHandler;

import com.idega.core.data.ICObject;

import com.idega.util.reflect.MethodFinder;

import java.util.List;
import java.util.Iterator;
import java.lang.reflect.Method;

import java.beans.*;
import java.beans.beancontext.*;


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
  private static final String CLASS_PARAMETER = "iw_bundle_comp_class";
  private static final String DELETE_CHECKBOX_NAME = "iw_bundle_comp_meth_delete";
  private static final String METHOD_PARAMETER = "iw_method_par";
  private static final String METHOD_DESCRIPTION_PARAMETER = "iw_method_desc_par";

  public ComponentManager() {
  }

<<<<<<< ComponentManager.java
  public void main(ModuleInfo modinfo)throws Exception{
=======
  public void main(ModuleInfo modinfo){
      add(IWDeveloper.getTitleTable(this.getClass()));
>>>>>>> 1.2

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

<<<<<<< ComponentManager.java
      int yindex = 1;

      table.add("Bundle:",1,yindex);
      table.add(bundles,2,yindex);
      SubmitButton button1 = new SubmitButton("Select");
      table.add(button1,2,yindex);
=======
      selectTable.add(IWDeveloper.getText("Bundle:"),1,1);
      selectTable.add(bundles,2,1);
      SubmitButton button1 = new SubmitButton("Go");
      selectTable.add(button1,3,1);
>>>>>>> 1.2

      String bundleIdentifier = modinfo.getParameter(BUNDLE_PARAMETER);

      if(bundleIdentifier!=null){

        IWBundle iwb = iwma.getBundle(bundleIdentifier);

<<<<<<< ComponentManager.java
        yindex++;
=======
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
>>>>>>> 1.2

        List componentNames = iwb.getComponentKeys();
        DropdownMenu componentsDrop = new DropdownMenu(this.CLASS_PARAMETER);
        componentsDrop.keepStatusOnAction();
        componentsDrop.setToSubmit();

        Iterator iter = componentNames.iterator();
        while (iter.hasNext()) {
          String component = (String)iter.next();
          componentsDrop.addMenuElement(component);
        }

        table.add("Component:",1,yindex);
        table.add(componentsDrop,2,yindex);
        SubmitButton button2 = new SubmitButton("Select");
        table.add(button2,2,yindex);

        String selectedComponentKey = modinfo.getParameter(CLASS_PARAMETER);
        if(selectedComponentKey!=null){

          yindex++;
          Class selectedClass = Class.forName(selectedComponentKey);

          //Method[] methods = selectedClass.getMethods();

          BeanInfo info = Introspector.getBeanInfo(selectedClass,ModuleObject.class);
          MethodDescriptor[] descriptors = info.getMethodDescriptors();
          DropdownMenu methodsDrop = new DropdownMenu(METHOD_PARAMETER);
          methodsDrop.keepStatusOnAction();
          methodsDrop.setToSubmit();
          String openingParentheses = "(";
          String closingParentheses = ")";
          String comma = ",";
          for (int i = 0; i < descriptors.length; i++) {
            Method method = descriptors[i].getMethod();
            //Method method = methods[i];
            if(method.getDeclaringClass().equals(selectedClass)){
              //String methodToString = methods[i].toString();
              String methodToString = method.getName()+openingParentheses;
              Class[] arguments = method.getParameterTypes();
              for (int j = 0; j < arguments.length; j++) {
                  methodToString += arguments[j].getName();
                  methodToString += comma;
              }
              methodToString += closingParentheses;
              String methodIdentifier = MethodFinder.getInstance().getMethodIdentifier(method);
              methodsDrop.addMenuElement(methodIdentifier,methodToString);
            }
          }

<<<<<<< ComponentManager.java
          table.add("Method:",1,yindex);
          table.add(methodsDrop,2,yindex);
          SubmitButton button3 = new SubmitButton("Select");
          table.add(button3,2,yindex);


          String selectedMethodIdentifier = modinfo.getParameter(METHOD_PARAMETER);
          if(selectedMethodIdentifier!=null){
              yindex++;
              TextInput methodDesc = new TextInput(METHOD_DESCRIPTION_PARAMETER);
              table.add("MethodDescription:",1,yindex);
              table.add(methodDesc,2,yindex);
              SubmitButton button4 = new SubmitButton("Register Method");
              table.add(button4,2,yindex);

              String selectedMethodDesc = modinfo.getParameter(METHOD_DESCRIPTION_PARAMETER);
              if(selectedMethodDesc!=null){
                if(!selectedMethodDesc.equals("")){
                  doBusiness(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc);
                }
              }
          }
=======
        int index = 2;
>>>>>>> 1.2

<<<<<<< ComponentManager.java
          String[] methodsToDelete = modinfo.getParameterValues(DELETE_CHECKBOX_NAME);
          if(methodsToDelete!=null){
            deleteMethods(iwb,selectedComponentKey,methodsToDelete);
          }

          IWPropertyList methodsList = IBPropertyHandler.getInstance().getMethods(iwb,selectedComponentKey);
          if(methodsList!=null){
            CheckBox deleteBox = new CheckBox(DELETE_CHECKBOX_NAME);
            IWPropertyListIterator methodsIter = methodsList.getIWPropertyListIterator();
            yindex++;
            table.add("Remove?",1,yindex);
            while (methodsIter.hasNext()) {
              yindex++;
              IWProperty prop = methodsIter.nextProperty();

              String identifier = prop.getKey();
              String description = prop.getValue();
              Method method = null;
              try{
                method = MethodFinder.getInstance().getMethod(identifier);
              }
              catch(Exception e){
                e.printStackTrace();
              }

              table.add(description,2,yindex);
              table.add(identifier,3,yindex);
              if(method!=null){
                table.add(method.toString(),4,yindex);
              }
              CheckBox rowBox = (CheckBox)deleteBox.clone();
              rowBox.setContent(identifier);
              table.add(rowBox,1,yindex);

            }
            yindex++;
            table.add(new SubmitButton("Update"),1,yindex);
          }
=======
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
>>>>>>> 1.2
        }
<<<<<<< ComponentManager.java
=======

        table.add(classesInput,1,index);
        table.add(typesDrop,2,index);

        table.setAlignment(3,index+1,"right");
        table.add(new SubmitButton("Save","save"),3,index+1);
>>>>>>> 1.2
      }
<<<<<<< ComponentManager.java

  }

  private void doBusiness(IWBundle iwb,String selectedComponentKey,String selectedMethodIdentifier,String selectedMethodDesc){
      IBPropertyHandler handler = IBPropertyHandler.getInstance();
      handler.setMethod(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc);
=======
>>>>>>> 1.2
  }

<<<<<<< ComponentManager.java
  public void deleteMethods(IWBundle iwb,String selectedComponentKey,String[] methodIdentifiers){
    for (int i = 0; i < methodIdentifiers.length; i++) {
      IBPropertyHandler.getInstance().removeMethod(iwb,selectedComponentKey,methodIdentifiers[i]);
    }
=======
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
>>>>>>> 1.2
  }

<<<<<<< ComponentManager.java

=======
  private Text getSmallText(String text) {
    Text T = new Text(text);
      T.setFontFace(Text.FONT_FACE_VERDANA);
      T.setFontSize(Text.FONT_SIZE_7_HTML_1);
    return T;
  }
>>>>>>> 1.2
}