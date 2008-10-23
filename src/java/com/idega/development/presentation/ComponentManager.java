package com.idega.development.presentation;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.PresentationUtil;
import com.idega.util.reflect.MethodFinder;

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
	private static final String USER_FRIENDLY_PARAMETER = "iw_method_user_friendly";
	private static final String METHOD_NEEDS_RELOAD_PARAMETER = "iw_method_needs_reload";

	public ComponentManager() {
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		IWMainApplication iwma = iwc.getIWMainApplication();
		DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma, BUNDLE_PARAMETER);
		bundles.keepStatusOnAction();
		bundles.setToSubmit();

		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);

		Table table = new Table();
		Table propertyTable = new Table();
		table.setCellpadding(5);
		//table.setBorder(1);
		add(Text.getBreak());
		add(Text.getBreak());
		form.add(table);
		form.add(Text.getBreak());

		int yindex = 1;

		table.add(IWDeveloper.getText("Bundle:"), 1, yindex);
		table.add(bundles, 2, yindex);
		SubmitButton button1 = new SubmitButton("Select");
		table.add(button1, 3, yindex);

		String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);

		if (bundleIdentifier != null) {
			iwb = iwma.getBundle(bundleIdentifier);

			yindex++;

			List componentNames = iwb.getComponentKeys();
			Map<String, String> names = new TreeMap<String, String>();
			Iterator iter = componentNames.iterator();
			while (iter.hasNext()) {
				String element = (String) iter.next();
				names.put(element.substring(element.lastIndexOf(".") + 1), element);
			}

			DropdownMenu componentsDrop = new DropdownMenu(ComponentManager.CLASS_PARAMETER);
			componentsDrop.keepStatusOnAction();
			componentsDrop.setToSubmit();

			iter = names.keySet().iterator();
			while (iter.hasNext()) {
				String display = (String) iter.next();
				String component = names.get(display);

				componentsDrop.addMenuElement(component, display);
			}

			table.add(IWDeveloper.getText("Component:"), 1, yindex);
			table.add(componentsDrop, 2, yindex);
			SubmitButton button2 = new SubmitButton("Select");
			table.add(button2, 3, yindex);

			String selectedComponentKey = iwc.getParameter(CLASS_PARAMETER);
			if (selectedComponentKey != null) {

				yindex++;
				//DropdownMenu methodsDrop = this.getMethodsDropdown(selectedComponentKey);

				PresentationObject newPropertyOpener = getNewPropertyOpener(bundleIdentifier, selectedComponentKey);

				//table.add(IWDeveloper.getText("Method:"),1,yindex);
				//table.add(methodsDrop,2,yindex);
				table.add(newPropertyOpener, 2, yindex);
				//SubmitButton button3 = new SubmitButton("Select");
				//table.add(button3,3,yindex);

				String selectedMethodIdentifier = iwc.getParameter(METHOD_PARAMETER);
				if (selectedMethodIdentifier != null) {
					yindex++;
					TextInput methodDesc = new TextInput(METHOD_DESCRIPTION_PARAMETER);
					table.add(IWDeveloper.getText("MethodDescription:"), 1, yindex);
					table.add(methodDesc, 2, yindex);
					SubmitButton button4 = new SubmitButton("Register Method");
					table.add(button4, 3, yindex);
					yindex++;
					CheckBox allowManyValues = new CheckBox(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
					table.add(IWDeveloper.getText("Allow multivalued:"), 1, yindex);
					table.add(allowManyValues, 2, yindex);
					table.add(new Parameter(OPTIONS_PARAMETER, IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED));
					table.add(new Parameter(getTypeParameter(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED), "java.lang.Boolean"));

					String selectedMethodDesc = iwc.getParameter(METHOD_DESCRIPTION_PARAMETER);
					if (selectedMethodDesc != null) {
						if (!selectedMethodDesc.equals("")) {
							/*boolean multivalued = false;
							if (iwc.isParameterSet(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED)) {
								multivalued = true;
							}*/
							String parameterString = OPTIONS_PARAMETER;
							Map m = parseOptions(iwc, parameterString);
							doBusiness(iwb, selectedComponentKey, selectedMethodIdentifier, selectedMethodDesc, m);
						}
					}
				}

				String[] methodsToDelete = iwc.getParameterValues(DELETE_CHECKBOX_NAME);
				if (methodsToDelete != null) {
					deleteMethods(iwb, selectedComponentKey, methodsToDelete);
				}
				
				IWPropertyList methodsList = IBPropertyHandler.getInstance().getMethods(iwb, selectedComponentKey);
				
				manageMethodProperties(iwb, selectedComponentKey, iwc.getParameterValues(USER_FRIENDLY_PARAMETER), methodsList, iwc, true);
				manageMethodProperties(iwb, selectedComponentKey, iwc.getParameterValues(METHOD_NEEDS_RELOAD_PARAMETER), methodsList, iwc, false);
				
				if (methodsList != null) {

					IWPropertyListIterator methodsIter = methodsList.getIWPropertyListIterator();
					if (methodsIter.hasNext()) {
						form.add(propertyTable);

						String methodName;
						//yindex++;
						//yindex++;
						yindex = 1;

						propertyTable.add(IWDeveloper.getText("Remove?"), 1, yindex);
						propertyTable.add(IWDeveloper.getText("\"User Friendly\"?"), 2, yindex);
						propertyTable.add(IWDeveloper.getText("Needs reload"), 3, yindex);
						propertyTable.add(IWDeveloper.getText("Property"), 4, yindex);
						propertyTable.add(IWDeveloper.getText("Method used"), 5, yindex);
						
						String simpleAction = null;
						String reloadAction = null;
						while (methodsIter.hasNext()) {
							yindex++;
							IWProperty prop = methodsIter.nextProperty();

							String identifier = IBPropertyHandler.getInstance().getMethodIdentifier(prop);
							String description = IBPropertyHandler.getInstance().getMethodDescription(prop, iwc.getCurrentLocale());
							Method method = null;
							Class selectedClass = RefactorClassRegistry.forName(selectedComponentKey);
							try {
								//System.out.println("ComponentManager: "+identifier);
								method = MethodFinder.getInstance().getMethod(identifier, selectedClass);
							}
							catch (Exception e) {
								e.printStackTrace();
							}

							propertyTable.add(getSmallText(description), 4, yindex);
							//table.add(getSmallText(identifier),4,yindex);
							if (method != null) {
								methodName = method.getName() + "( ";
								for (int i = 0; i < method.getParameterTypes().length; i++) {
									if (i != 0) {
										methodName += " , ";
									}
									methodName += method.getParameterTypes()[i].getName();
								}
								methodName += " )";
								propertyTable.add(getSmallText(methodName), 5, yindex);
							}
							CheckBox rowBox = new CheckBox(DELETE_CHECKBOX_NAME);
							rowBox.setContent(identifier);
							propertyTable.add(rowBox, 1, yindex);
							
							CheckBox manageSimpleProperty = new CheckBox();
							manageSimpleProperty.setContent(identifier);
							simpleAction = new StringBuffer("addComponentPropertyToList(null, '").append(USER_FRIENDLY_PARAMETER).append("', this)").toString();
							manageSimpleProperty.setOnClick(simpleAction);
							manageSimpleProperty.setChecked(prop.isPropertySimple());
							propertyTable.add(manageSimpleProperty, 2, yindex);
							
							CheckBox manageNeedsReloadProperty = new CheckBox();
							manageNeedsReloadProperty.setContent(identifier);
							reloadAction = new StringBuffer("addComponentPropertyToList(null, '").append(METHOD_NEEDS_RELOAD_PARAMETER).append("', this)").toString();
							manageNeedsReloadProperty.setOnClick(reloadAction);
							manageNeedsReloadProperty.setChecked(prop.isNeedsReload());
							propertyTable.add(manageNeedsReloadProperty, 3, yindex);
						}
						yindex++;
						propertyTable.add(new SubmitButton("Update"), 1, yindex);
						propertyTable.mergeCells(1, yindex, 4, yindex);
					}
				}
			}

		}

		table.setWidth(1, "160");
	}

	private void doBusiness(IWBundle iwb, String selectedComponentKey, String selectedMethodIdentifier, String selectedMethodDesc, Map options) {
		IBPropertyHandler handler = IBPropertyHandler.getInstance();
		handler.setMethod(iwb, selectedComponentKey, selectedMethodIdentifier, selectedMethodDesc, options);
		iwb.storeState();
	}

	public String getTypeParameter(String inputParameter) {
		return inputParameter + "_type";
	}

	public Map parseOptions(IWContext iwc, String parameterName) {
		String[] parameters = iwc.getParameterValues(parameterName);

		Map theReturn = new Hashtable();
		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			String sValue = iwc.getParameter(parameter);
			Object oValue = null;
			String parameterType = iwc.getParameter(getTypeParameter(parameter));
			if (parameterType == null) {
				parameterType = "java.lang.String";
			}
			if (parameterType.equals("java.lang.Boolean")) {
				if (sValue != null) {
					if (sValue.equals("Y")) {
						oValue = Boolean.TRUE;
					}
					else if (sValue.equals("N")) {
						oValue = Boolean.FALSE;
					}
					else {
						oValue = Boolean.valueOf(sValue);
					}
				}
				else {
					oValue = Boolean.FALSE;
				}
			}
			if (parameterType.equals("java.lang.Integer")) {
				if (sValue != null) {
					oValue = Integer.valueOf(sValue);
				}
				else {
					//oValue = new Integer(0);
				}
			}
			if (parameterType.equals("java.lang.Float")) {
				if (sValue != null) {
					oValue = Float.valueOf(sValue);
				}
				else {
					//oValue = new Float(0);
				}
			}
			if (parameterType.equals("java.lang.Double")) {
				if (sValue != null) {
					oValue = Double.valueOf(sValue);
				}
				else {
					//oValue = new Double(0);
				}
			}
			if (parameterType.equals("java.lang.Character")) {
				if (sValue != null) {
					oValue = new Character(sValue.charAt(0));
				}
			}
			else {
				oValue = sValue;
			}
			if (oValue != null) {
				theReturn.put(parameter, oValue);
			}

		}
		
		return theReturn;
	}

	public void deleteMethods(IWBundle iwb, String selectedComponentKey, String[] methodIdentifiers) {
		for (int i = 0; i < methodIdentifiers.length; i++) {
			IBPropertyHandler.getInstance().removeMethod(iwb, selectedComponentKey, methodIdentifiers[i]);
		}
		if(methodIdentifiers.length>0){
			iwb.storeState();
		}

	}
	
	private void manageMethodProperties(IWBundle iwb, String selectedComponentKey, String[] markedMethods, IWPropertyList methods, IWContext iwc, boolean manageSimple) {
		if (iwb == null || selectedComponentKey == null || markedMethods == null || methods == null) {
			return;
		}
		String enable = "enable";
		String[] values = null;
		String eta = "@";
		boolean madeChanges = false;
		for (int i = 0; i < markedMethods.length; i++) {
			values = markedMethods[i].split(eta);
			if (values.length == 2) {
				IWProperty property = methods.getIWProperty(values[0]);
				if (property != null) {
					if (values[1].indexOf(enable) == -1) {
						if (manageSimple) {
							property.setSimple(false);
						}
						else {
							property.setNeedsReload(false);
						}
					}
					else {
						if (manageSimple) {
							property.setSimple(true);
						}
						else {
							property.setNeedsReload(true);
						}
					}
					madeChanges = true;
				}
			}
		}
		if (madeChanges) {
			//	Removing from cache
			BuilderLogic.getInstance().removeBlockObjectFromCache(iwc, BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY);
			BuilderLogic.getInstance().removeBlockObjectFromCache(iwc, BuilderConstants.ADD_NEW_MODULE_WINDOW_CACHE_KEY);
			methods.store();
		}
	}

	private Text getSmallText(String text) {
		Text T = new Text(text);
		T.setFontFace(Text.FONT_FACE_VERDANA);
		T.setFontSize(Text.FONT_SIZE_7_HTML_1);
		return T;
	}

	public PresentationObject getNewPropertyOpener(String bundleIdentifier, String componentKey) {

		Link newProperty = new Link("Add New Property");
		newProperty.setWindowToOpen(NewComponentPropertyWindow.class);
		newProperty.addParameter(NewComponentPropertyWindow.PARAMETER_BUNDLE, bundleIdentifier);
		newProperty.addParameter(NewComponentPropertyWindow.PARAMETER_COMPONENT, componentKey);
		return newProperty;
	}

}
