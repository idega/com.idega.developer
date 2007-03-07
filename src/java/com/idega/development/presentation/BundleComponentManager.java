package com.idega.development.presentation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.development.presentation.comp.BundleComponentFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.StringHandler;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class BundleComponentManager extends Block {

	private static final String BUNDLE_PARAMETER = "iw_b_p_s";
	//private static final String PROPERTY_KEY_NAME_PARAMETER="iw_b_p_s_k";
	//private static final String PROPERTY_VALUE_PARAMETER="iw_b_p_s_v";

	private static final String CLASS_INPUT_NAME = "iw_bundle_comp_class";
	private static final String TYPE_INPUT_NAME = "iw_bundle_comp_type";
	private static final String DELETE_CHECKBOX_NAME = "iw_bundle_comp_delete";
	private static final String COMPONENT_IS_WIDGET = "iw_bundle_comp_widget";
	private static final String COMPONENT_IS_BLOCK = "iw_bundle_comp_block";
	private static final String FORM_ID = "componentCustomizeForm";
	private static final String UPDATE_PARAMETER = "update";

	public BundleComponentManager() {
	}

	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		Page parent = getParentPage();
		if (parent != null) {
			parent.addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/developer.css"));
			parent.addJavascriptURL(iwb.getVirtualPathWithFileNameString("javascript/developer.js"));
		}
		String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("applicationPropertySetter");
		add(topLayer);

		IWMainApplication iwma = iwc.getIWMainApplication();
		DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma, BUNDLE_PARAMETER);
		bundles.keepStatusOnAction();
		bundles.setToSubmit();

		DropdownMenu typesDrop = new DropdownMenu(BundleComponentManager.TYPE_INPUT_NAME);
		TextInput classesInput = new TextInput(CLASS_INPUT_NAME);

		FieldSet fieldSet = new FieldSet("Bundle Component Manager");
		fieldSet.setStyleClass("componentManager");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		fieldSet.add(form);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Bundle", bundles);
		formItem.add(label);
		formItem.add(bundles);
		form.add(formItem);

		if (bundleIdentifier != null) {
			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label("Class name", classesInput);
			formItem.add(label);
			formItem.add(classesInput);
			form.add(formItem);

			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label("Class type", typesDrop);
			formItem.add(label);
			formItem.add(typesDrop);
			form.add(formItem);
		}
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton go = new SubmitButton("Select");
		go.setStyleClass("button");
		go.setID("select");

		SubmitButton save = new SubmitButton("save", "Save");
		save.setStyleClass("button");
		save.setID("save");

		buttonLayer.add(go);
		if (bundleIdentifier != null) {
			buttonLayer.add(save);
		}

		if (bundleIdentifier != null) {

			IWBundle bundle = iwc.getIWMainApplication().getBundle(bundleIdentifier);
			
			try {
				doBusiness(iwc, bundle);
			}
			catch (Exception e) {
				add("Error: " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
			}

			List componentTypes = com.idega.core.component.data.ICObjectBMPBean.getAvailableComponentTypes();
			Collections.sort(componentTypes);
			Iterator iter = componentTypes.iterator();

			while (iter.hasNext()) {
				String type = (String) iter.next();
				typesDrop.addMenuElement(type);
			}

			List compList = bundle.getComponentKeys();
			Collections.sort(compList);
			Iterator compIter = compList.iterator();

			FieldSet keySet = new FieldSet(new Legend("Available classes"));
			keySet.setStyleClass("availableClasses");
			topLayer.add(keySet);

			keySet.add(getPropertiesTable(bundle, compIter));
			
		}
		doRecognizeComponents(iwc);
	}

	private void doBusiness(IWContext iwc, IWBundle iwb) throws Exception {
		String save = iwc.getParameter("save");

		if ((iwb != null) && (save != null)) {
			String newComponentClass = iwc.getParameter(BundleComponentManager.CLASS_INPUT_NAME);
			if (newComponentClass == null) {
				newComponentClass = StringHandler.EMPTY_STRING;
			}

			String newComponentType = iwc.getParameter(BundleComponentManager.TYPE_INPUT_NAME);
			if (newComponentType == null) {
				newComponentType = StringHandler.EMPTY_STRING;
			}

			String[] deletes = iwc.getParameterValues(BundleComponentManager.DELETE_CHECKBOX_NAME);
			if (deletes != null) {
				for (int i = 0; i < deletes.length; i++) {
					iwb.removeComponent(deletes[i]);
				}
				iwb.storeState();
			}

			String emptyString = StringHandler.EMPTY_STRING;

			if (!(emptyString.equals(newComponentClass) || emptyString.equals(newComponentType))) {
				Class cls = RefactorClassRegistry.forName(newComponentClass);
				// Added by aron 21.june 2003 
					BundleComponent comp = BundleComponentFactory.getInstance().getBundleComponent(newComponentType);
					boolean valid = comp.validateInterfaces(cls);
					valid &= comp.validateSuperClasses(cls);
					if(!valid) {
						throw new Exception("Component needs to implement required interfaces ");
					}
				// 
				iwb.addComponent(newComponentClass, newComponentType);
				//iwb.storeState();
			}
		}
		else if ((iwb != null) && (save == null)) {

		}

		IBAddModuleWindow.removeAttributes(iwc);
	}
	
	protected Form getPropertiesTable(IWBundle iwb, Iterator iter) {
		Form form = new Form();
		form.setId(FORM_ID);
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);

		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		form.add(table);
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.add(new Text("ClassName"));

		cell = row.createHeaderCell();
		cell.add(new Text("Type"));
		
		cell = row.createHeaderCell();
		cell.add(new Text("Widget"));
		
		cell = row.createHeaderCell();
		cell.add(new Text("Block"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Remove"));

		group = table.createBodyRowGroup();
		
		CheckBox isWidget = null;
		CheckBox isBlock = null;
		int i = 0;
		ICObjectHome objectHome = getICObjectHome();
		ICObject object = null;
		for (Iterator it = iter; it.hasNext(); ) {
			row = group.createRow();

			String className = (String) iter.next();
			String type = iwb.getComponentType(className);
			if (objectHome != null) {
				try {
					object = objectHome.findByClassName(className);
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}

			CheckBox rowBox = new CheckBox(DELETE_CHECKBOX_NAME);
			rowBox.setContent(className);
			
			isWidget = new CheckBox();
			isWidget.setContent(className);
			isWidget.setOnClick("addComponentPropertyToList('"+FORM_ID+"', '"+COMPONENT_IS_WIDGET+"', this)");
			if (object != null) {
				isWidget.setChecked(object.isWidget());
			}
			
			isBlock = new CheckBox();
			isBlock.setContent(className);
			isBlock.setOnClick("addComponentPropertyToList('"+FORM_ID+"', '"+COMPONENT_IS_BLOCK+"', this)");
			if (object != null) {
				isBlock.setChecked(object.isBlock());
			}

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(new Text(className));

			cell = row.createCell();
			cell.add(new Text(type));
			
			cell = row.createCell();
			cell.add(isWidget);
			
			cell = row.createCell();
			cell.add(isBlock);

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.add(rowBox);

			i++;

			if (i % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton update = new SubmitButton(UPDATE_PARAMETER, "Update");
		update.setStyleClass("button");
		update.setID(UPDATE_PARAMETER);

		buttonLayer.add(update);

		return form;
	}
	
	private void doRecognizeComponents(IWContext iwc) {
		String update = iwc.getParameter(UPDATE_PARAMETER);
		if (update == null) {
			return;
		}
		String[] widgets = iwc.getParameterValues(COMPONENT_IS_WIDGET);
		String[] blocks = iwc.getParameterValues(COMPONENT_IS_BLOCK);
		if (widgets == null && blocks == null) {
			return;
		}
		
		ICObjectHome objectHome = getICObjectHome();
		if (objectHome == null) {
			return;
		}
		
		setComponentValues(widgets, objectHome, true);
		setComponentValues(blocks, objectHome, false);
	}
	
	private void setComponentValues(String[] classNames, ICObjectHome objectHome, boolean manageWidgets) {
		if (classNames == null || objectHome == null) {
			return;
		}
		ICObject object = null;
		String[] values = null;
		String eta = "@";
		String enable = "enable";
		boolean needToStore = false;
		for (int i = 0; i < classNames.length; i++) {
			needToStore = false;
			values = classNames[i].split(eta);
			if (values.length == 2) {
				try {
					object = objectHome.findByClassName(values[0]);
				} catch (FinderException e) {
					e.printStackTrace();
				}
				if (object != null) {
					if (values[1].indexOf(enable) == -1) { // Setting FALSE
						if (manageWidgets) {
							if (object.isWidget()) {
								object.setIsWidget(Boolean.FALSE);
								needToStore = true;
							}
						}
						else {
							if (object.isBlock()) {
								object.setIsBlock(Boolean.FALSE);
								needToStore = true;
							}
						}
					}
					else { // Setting TRUE
						if (manageWidgets) {
							if (!object.isWidget()) {
								object.setIsWidget(Boolean.TRUE);
								needToStore = true;
							}
						}
						else {
							if (!object.isBlock()) {
								object.setIsBlock(Boolean.TRUE);
								needToStore = true;
							}
						}
					}
					if (needToStore) {
						object.store();
					}
				}
			}
		}
	}
	
	private ICObjectHome getICObjectHome() {
		ICObjectHome home = null;
		try {
			home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		} catch (IDOLookupException e) {
			e.printStackTrace();
			return null;
		}
		return home;
	}
}