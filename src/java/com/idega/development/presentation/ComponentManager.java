package com.idega.development.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;

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

import java.util.Map;
import java.util.Hashtable;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */



public class ComponentManager extends Block {

  private static final String BUNDLE_PARAMETER = "iw_b_p_s";
  private static final String CLASS_PARAMETER = "iw_bundle_comp_class";
  private static final String DELETE_CHECKBOX_NAME = "iw_bundle_comp_meth_delete";
  private static final String METHOD_PARAMETER = "iw_method_par";
  private static final String METHOD_DESCRIPTION_PARAMETER = "iw_method_desc_par";
  private static final String OPTIONS_PARAMETER = "iw_method_options";


  public ComponentManager() {
  }

  public void main(IWContext iwc)throws Exception{
      add(IWDeveloper.getTitleTable(this.getClass()));
      IWMainApplication iwma = iwc.getApplication();
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


      String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);

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

        String selectedComponentKey = iwc.getParameter(CLASS_PARAMETER);
        if(selectedComponentKey!=null){

          yindex++;
          //DropdownMenu methodsDrop = this.getMethodsDropdown(selectedComponentKey);

          PresentationObject newPropertyOpener = getNewPropertyOpener(bundleIdentifier,selectedComponentKey);

          //table.add(IWDeveloper.getText("Method:"),1,yindex);
          //table.add(methodsDrop,2,yindex);
          table.add(newPropertyOpener,2,yindex);
          SubmitButton button3 = new SubmitButton("Select");
          table.add(button3,3,yindex);


          String selectedMethodIdentifier = iwc.getParameter(METHOD_PARAMETER);
          if(selectedMethodIdentifier!=null){
              yindex++;
              TextInput methodDesc = new TextInput(METHOD_DESCRIPTION_PARAMETER);
              table.add(IWDeveloper.getText("MethodDescription:"),1,yindex);
              table.add(methodDesc,2,yindex);
              SubmitButton button4 = new SubmitButton("Register Method");
              table.add(button4,3,yindex);
              yindex++;
              CheckBox allowManyValues = new CheckBox(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
              table.add(IWDeveloper.getText("Allow multivalued:"),1,yindex);
              table.add(allowManyValues,2,yindex);
              table.add(new Parameter(OPTIONS_PARAMETER,IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED));
              table.add(new Parameter(getTypeParameter(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED),"java.lang.Boolean"));

              String selectedMethodDesc = iwc.getParameter(METHOD_DESCRIPTION_PARAMETER);
              if(selectedMethodDesc!=null){
                if(!selectedMethodDesc.equals("")){
                  boolean multivalued = false;
                  if(iwc.isParameterSet(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED)){
                    multivalued = true;
                  }
                  String parameterString = OPTIONS_PARAMETER;
                  Map m = parseOptions(iwc,parameterString);
                  doBusiness(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc,m);
                }
              }
          }



          String[] methodsToDelete = iwc.getParameterValues(DELETE_CHECKBOX_NAME);
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

              String identifier = IBPropertyHandler.getInstance().getMethodIdentifier(prop);
              String description = IBPropertyHandler.getInstance().getMethodDescription(prop,iwc);
              Method method = null;
              Class selectedClass=Class.forName(selectedComponentKey);
              try{
                //System.out.println("ComponentManager: "+identifier);
                method = MethodFinder.getInstance().getMethod(identifier,selectedClass);
              }
              catch(Exception e){
                e.printStackTrace();
              }

              table.add(getSmallText(description),2,yindex);
              //table.add(getSmallText(identifier),3,yindex);
              if(method!=null){
                table.add(getSmallText(method.toString()),3,yindex);
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

  private void doBusiness(IWBundle iwb,String selectedComponentKey,String selectedMethodIdentifier,String selectedMethodDesc,Map options){
      IBPropertyHandler handler = IBPropertyHandler.getInstance();
      handler.setMethod(iwb,selectedComponentKey,selectedMethodIdentifier,selectedMethodDesc,options);
  }

  public String getTypeParameter(String inputParameter){
    return inputParameter+"_type";
  }

  public Map parseOptions(IWContext iwc,String parameterName){
    String[] parameters = iwc.getParameterValues(parameterName);

    Map theReturn = new Hashtable();
    for (int i = 0; i < parameters.length; i++) {
      String parameter = parameters[i];
      String sValue = iwc.getParameter(parameter);
      Object oValue = null;
      String parameterType = iwc.getParameter(getTypeParameter(parameter));
      if(parameterType == null){
        parameterType="java.lang.String";
      }
      if(parameterType.equals("java.lang.Boolean")){
        if(sValue!=null){
          if(sValue.equals("Y")){
            oValue=Boolean.TRUE;
          }
          else if(sValue.equals("N")){
             oValue=Boolean.FALSE;
          }
          else{
            oValue = Boolean.valueOf(sValue);
          }
        }
        else{
          oValue = Boolean.FALSE;
        }
      }
      if(parameterType.equals("java.lang.Integer")){
        if(sValue!=null){
          oValue = Integer.valueOf(sValue);
        }
        else{
          //oValue = new Integer(0);
        }
      }
      if(parameterType.equals("java.lang.Float")){
        if(sValue!=null){
          oValue = Float.valueOf(sValue);
        }
        else{
          //oValue = new Float(0);
        }
      }
      if(parameterType.equals("java.lang.Double")){
        if(sValue!=null){
          oValue = Double.valueOf(sValue);
        }
        else{
          //oValue = new Double(0);
        }
      }
      if(parameterType.equals("java.lang.Character")){
        if(sValue!=null){
          oValue = new Character(sValue.charAt(0));
        }
      }
      else{
        oValue = sValue;
      }
      if(oValue!=null){
        theReturn.put(parameter,oValue);
      }

    }
    return theReturn;
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



  public PresentationObject getNewPropertyOpener(String bundleIdentifier,String componentKey){

          Link newProperty = new Link("Add New Property");
          newProperty.setWindowToOpen(NewComponentPropertyWindow.class);
          newProperty.addParameter(NewComponentPropertyWindow.PARAMETER_BUNDLE,bundleIdentifier);
          newProperty.addParameter(NewComponentPropertyWindow.PARAMETER_COMPONENT,componentKey);
          return newProperty;
  }

}