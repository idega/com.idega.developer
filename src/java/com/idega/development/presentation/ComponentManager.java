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

  public void main(ModuleInfo modinfo){
      add(IWDeveloper.getTitleTable(this.getClass()));
      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma,BUNDLE_PARAMETER);
      bundles.keepStatusOnAction();
      bundles.setToSubmit();

      Form form = new Form();
      add(form);

      Table table = new Table();
        table.setCellpadding(5);
        //table.setBorder(1);
      add(Text.getBreak());
      add(Text.getBreak());
      form.add(table);

      int yindex = 1;

      table.add(IWDeveloper.getText("Bundle:"),1,yindex);
      table.add(bundles,2,yindex);
      SubmitButton button1 = new SubmitButton("Select");
      table.add(button1,3,yindex);


      String bundleIdentifier = modinfo.getParameter(BUNDLE_PARAMETER);

      if(bundleIdentifier!=null){

        IWBundle iwb = iwma.getBundle(bundleIdentifier);


        yindex++;


        List componentNames = iwb.getComponentKeys();
        DropdownMenu componentsDrop = new DropdownMenu(this.CLASS_PARAMETER);
        componentsDrop.keepStatusOnAction();
        componentsDrop.setToSubmit();

        Iterator iter = componentNames.iterator();
        while (iter.hasNext()) {
          String component = (String)iter.next();
          componentsDrop.addMenuElement(component);
        }

        table.add(IWDeveloper.getText("Component:"),1,yindex);
        table.add(componentsDrop,2,yindex);
        SubmitButton button2 = new SubmitButton("Select");
        table.add(button2,3,yindex);

        String selectedComponentKey = modinfo.getParameter(CLASS_PARAMETER);
        if(selectedComponentKey!=null){

          yindex++;
          Class selectedClass=null;
          BeanInfo info = null;
          try{
          selectedClass = Class.forName(selectedComponentKey);
            //Method[] methods = selectedClass.getMethods();
            info = Introspector.getBeanInfo(selectedClass,ModuleObject.class);
          }
          catch(Exception e){
            e.printStackTrace();
          }
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


          table.add(IWDeveloper.getText("Method:"),1,yindex);
          table.add(methodsDrop,2,yindex);
          SubmitButton button3 = new SubmitButton("Select");
          table.add(button3,3,yindex);


          String selectedMethodIdentifier = modinfo.getParameter(METHOD_PARAMETER);
          if(selectedMethodIdentifier!=null){
              yindex++;
              TextInput methodDesc = new TextInput(METHOD_DESCRIPTION_PARAMETER);
              table.add(IWDeveloper.getText("MethodDescription:"),1,yindex);
              table.add(methodDesc,2,yindex);
              SubmitButton button4 = new SubmitButton("Register Method");
              table.add(button4,3,yindex);

              String selectedMethodDesc = modinfo.getParameter(METHOD_DESCRIPTION_PARAMETER);
              if(selectedMethodDesc!=null){
                if(!selectedMethodDesc.equals("")){
                  doBusiness(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc);
                }
              }
          }



          String[] methodsToDelete = modinfo.getParameterValues(DELETE_CHECKBOX_NAME);
          if(methodsToDelete!=null){
            deleteMethods(iwb,selectedComponentKey,methodsToDelete);
          }

          IWPropertyList methodsList = IBPropertyHandler.getInstance().getMethods(iwb,selectedComponentKey);
          if(methodsList!=null){
            CheckBox deleteBox = new CheckBox(DELETE_CHECKBOX_NAME);
            IWPropertyListIterator methodsIter = methodsList.getIWPropertyListIterator();
            yindex++;
            yindex++;
            table.add(IWDeveloper.getText("Remove?"),1,yindex);
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

              table.add(getSmallText(description),2,yindex);
              table.add(getSmallText(identifier),3,yindex);
              if(method!=null){
                table.add(getSmallText(method.toString()),4,yindex);
              }
              CheckBox rowBox = (CheckBox)deleteBox.clone();
              rowBox.setContent(identifier);
              table.add(rowBox,1,yindex);

            }
            yindex++;
            table.add(new SubmitButton("Update"),1,yindex);
          }
        }

      }

      table.setWidth(1,"160");


  }

  private void doBusiness(IWBundle iwb,String selectedComponentKey,String selectedMethodIdentifier,String selectedMethodDesc){
      IBPropertyHandler handler = IBPropertyHandler.getInstance();
      handler.setMethod(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc);
  }


  public void deleteMethods(IWBundle iwb,String selectedComponentKey,String[] methodIdentifiers){
    for (int i = 0; i < methodIdentifiers.length; i++) {
      IBPropertyHandler.getInstance().removeMethod(iwb,selectedComponentKey,methodIdentifiers[i]);
    }

  }

  private Text getSmallText(String text) {
    Text T = new Text(text);
      T.setFontFace(Text.FONT_FACE_VERDANA);
      T.setFontSize(Text.FONT_SIZE_7_HTML_1);
    return T;
  }

}