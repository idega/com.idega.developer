package com.idega.development.presentation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.core.component.data.BundleComponent;
import com.idega.development.presentation.comp.BundleComponentFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
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

	public BundleComponentManager() {
	}

	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		getParentPage().addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/developer.css"));
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
	
	public static Form getPropertiesTable(IWBundle iwb, Iterator iter) {
		Form form = new Form();
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
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Remove"));

		group = table.createBodyRowGroup();
		
		int i = 0;
		while (iter.hasNext()) {
			row = group.createRow();

			String className = (String) iter.next();
			String type = iwb.getComponentType(className);

			CheckBox rowBox = new CheckBox(DELETE_CHECKBOX_NAME);
			rowBox.setContent(className);

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.add(new Text(className));

			cell = row.createCell();
			cell.add(new Text(type));

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

		SubmitButton delete = new SubmitButton("Delete", "delete");
		delete.setStyleClass("button");
		delete.setID("delete");

		buttonLayer.add(delete);

		return form;
	}
}