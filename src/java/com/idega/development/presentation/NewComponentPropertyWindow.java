package com.idega.development.presentation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObject;
import com.idega.development.presentation.comp.BundleComponentFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWPropertyList;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.reflect.MethodFinder;

/**
 * Title: idegaWeb Developer Description: Copyright: Copyright (c) 2001 Company:
 * idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */

public class NewComponentPropertyWindow extends Window {

	public static final String PARAMETER_BUNDLE = "iw_n_p_bundle_id";

	public static final String PARAMETER_COMPONENT = "iw_n_p_comp_class";

	public static final String PARAMETER_METHOD = "iw_n_p_meth_id";

	public static final String PARAMETER_HANDLER_PREFIX = "iw_n_phandler";

	public static final String PARAMETER_DESCRIPTION = "iw_n_p_desc";

	public static final String PARAMETER_MULTIVALUED = "iw_n_p_all_mult";

	public static final String PARAMETER_DESC_PREFIX = "iw_param_desc_";

	public static final String PARAMETER_PRIMARY_KEY = "iw_n_p_prim_key";

	public static final String PARAMETER_SAVE = "iw_n_p_save";

	private IWBundle selectedBundle = null;
	private String component = null;

	public NewComponentPropertyWindow() {
		setWidth(700);
		setTitle("Add New Property");
		setResizable(true);
	}

	public void main(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_BUNDLE);
		form.maintainParameter(PARAMETER_COMPONENT);
		this.selectedBundle = getSelectedBundle(iwc);
		add(form);
		Table t = new Table();
		form.add(t);
		this.component = getSelectedComponent(iwc);
		String method = getSelectedMethod(iwc);
		boolean methodSelected = false;
		boolean propertySave = false;
		if (method == null) {
			methodSelected = false;
		}
		else {
			methodSelected = true;
		}
		propertySave = iwc.isParameterSet(PARAMETER_SAVE);

		t.add(getMethodsDropdown(this.component), 1, 1);
		t.add(new SubmitButton("Select"), 2, 1);

		if (propertySave) {
			try {
				propertySave(iwc);
			}
			catch (Exception e) {
				add("Error: " + e.getMessage());
			}
		}

		if (methodSelected) {
			t.add(getInputForm(method), 1, 2);
		}

	}

	public boolean isMultiValued(IWContext iwc) {
		return iwc.isParameterSet(PARAMETER_MULTIVALUED);
	}

	public String getDescription(IWContext iwc) {
		return iwc.getParameter(PARAMETER_DESCRIPTION);
	}

	public String getStringSelectedBundle(IWContext iwc) {
		return iwc.getParameter(PARAMETER_BUNDLE);
	}

	public IWBundle getSelectedBundle(IWContext iwc) {
		String sIwb = getStringSelectedBundle(iwc);
		IWBundle iwb = iwc.getIWMainApplication().getBundle(sIwb);
		return iwb;
	}

	public String getSelectedComponent(IWContext iwc) {
		return iwc.getParameter(PARAMETER_COMPONENT);
	}

	public String getSelectedMethod(IWContext iwc) {
		return iwc.getParameter(PARAMETER_METHOD);
	}

	public PresentationObject getInputForm(String methodIdentifier) {
		//Form form = new Form();
		Table t = new Table();
		boolean hasOnlyOneParameter = false;
		Class[] parameterClasses = null;
		try {
			parameterClasses = MethodFinder.getInstance().getArgumentClasses(methodIdentifier);
			if (parameterClasses.length == 1) {
				hasOnlyOneParameter = true;
			}
		}
		catch (Exception e) {
			t.add("Error " + e.getMessage());
		}
		
		boolean update = false;
		IWPropertyList parameterOptions = null;
		IWPropertyList parameter = null;
		IWPropertyList componentList = this.selectedBundle.getComponentPropertyList(this.component);
		IWPropertyList methodList = componentList.getIWPropertyList(IBPropertyHandler.METHODS_KEY);
		IWPropertyList method = methodList.getIWPropertyList(methodIdentifier);
		if (method != null) {
			update = true;
			parameterOptions = method.getPropertyList(IBPropertyHandler.METHOD_PARAMETERS_KEY);
		}


		//form.add(t);
		int ycounter = 1;
		TextInput methodDesc = new TextInput(PARAMETER_DESCRIPTION);
		if (update) {
			methodDesc.setContent(method.getProperty(IBPropertyHandler.METHOD_PROPERTY_DESCRIPTION));
		}
		t.add("Property Name: ", 1, ycounter);
		t.add(methodDesc, 2, ycounter++);

		CheckBox allowMultiv = new CheckBox(PARAMETER_MULTIVALUED);
		if (update) {
			allowMultiv.setChecked(new Boolean(method.getProperty(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED)).booleanValue());
		}
		t.add("Allow Multivalued: ", 1, ycounter);
		t.add(allowMultiv, 2, ycounter++);

		t.add("Parameters: ", 1, ycounter++);

		if (!hasOnlyOneParameter) {
			t.add("Description: ", 2, ycounter);
		}
		t.add("Handler: ", 3, ycounter);
		if (!hasOnlyOneParameter) {
			t.add("Primary key: ", 4, ycounter);
		}
		ycounter++;
		try {
			for (int i = 0; i < parameterClasses.length; i++) {
				if (update) {
					parameter = parameterOptions.getIWPropertyList(Integer.toString(i));
				}
				//Starts at index 0
				Text description = new Text("Parameter<" + i + "> ; " + parameterClasses[i].getName() + ": ");
				t.add(description, 1, ycounter);
				if (!hasOnlyOneParameter) {
					String content = null;
					if (update) {
						content = parameter.getProperty(IBPropertyHandler.METHOD_PARAMETER_PROPERTY_DESCRIPTION);
					}
					t.add(getDescriptionInput(i, content), 2, ycounter);
				}
				String selected = null;
				if (update) {
					selected = parameter.getProperty(IBPropertyHandler.METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);
				}
				t.add(getHandlersDropdown(i, selected), 3, ycounter);
				if (!hasOnlyOneParameter) {
					boolean isPrimaryKey = false;
					if (update) {
						isPrimaryKey = new Boolean(parameter.getProperty(IBPropertyHandler.METHOD_PARAMETER_PROPERTY_PRIMARY_KEY)).booleanValue();
					}
					t.add(getPrimaryKeyBox(i, isPrimaryKey), 4, ycounter);
				}
				ycounter++;
			}

			SubmitButton submit = new SubmitButton(PARAMETER_SAVE, "Save");
			t.add(submit, 1, ycounter++);

		}
		catch (Exception e) {
			t.add("Error " + e.getMessage());
		}

		return t;
		//return form;
	}

	public DropdownMenu getHandlersDropdown(int parameterNumber, String selectedValue) {
		//return getHandlersDropdown(PARAMETER_HANDLER_PREFIX+parameterNumber);
		return getHandlersDropdown(PARAMETER_HANDLER_PREFIX, selectedValue);
	}

	public DropdownMenu getHandlersDropdown(String name, String selectedValue) {
		DropdownMenu menu = new DropdownMenu(name);
		menu.addMenuElement("", "Default");
		List list = IBPropertyHandler.getInstance().getAvailablePropertyHandlers();
		Collections.sort(list);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			ICObject item = (ICObject) iter.next();
			try {
				menu.addMenuElement(item.getObjectClass().getName(), item.getName());
			}
			catch (ClassNotFoundException e) {

			}
		}
		if (selectedValue != null) {
			menu.setSelectedElement(selectedValue);
		}
		return menu;
	}

	public void propertySave(IWContext iwc) throws Exception {
		IWBundle iwb = this.getSelectedBundle(iwc);
		String component = this.getSelectedComponent(iwc);
		String method = this.getSelectedMethod(iwc);

		String description = this.getDescription(iwc);
		boolean multivalued = this.isMultiValued(iwc);

		System.out.println("description: " + description);
		System.out.println("Multivalued: " + multivalued);

		String[] handlers = getHandlers(iwc);
		String[] descriptions = getDescriptions(iwc);
		boolean[] primaryKeys = getPrimaryKeys(iwc);
		System.out.println("  newproperty = Handlers,Descriptions,PrimaryKeys:");
		for (int i = 0; i < handlers.length; i++) {
			System.out.println("handler[" + i + "]: " + handlers[i]);
			System.out.println("descriptions[" + i + "]: " + descriptions[i]);
			System.out.println("primaryKeys[" + i + "]: " + primaryKeys[i]);
		}

		propertySave(iwb, component, method, description, multivalued, handlers, descriptions, primaryKeys);

	}

	public boolean[] getPrimaryKeys(IWContext iwc) {
		int numberOfParameters = getHandlers(iwc).length;
		boolean[] theReturn = new boolean[numberOfParameters];
		String[] realPrimKeyValues = iwc.getParameterValues(PARAMETER_PRIMARY_KEY);
		if (realPrimKeyValues == null) {
			for (int i = 0; i < theReturn.length; i++) {
				theReturn[i] = false;
			}
		}
		else {
			int otherIndex = 0;
			for (int i = 0; i < theReturn.length; i++) {
				try {
					String sRealParam = realPrimKeyValues[otherIndex];
					int iRealParam = Integer.parseInt(sRealParam);
					if (iRealParam == i) {
						theReturn[i] = true;
						otherIndex++;
					}
					else {
						theReturn[i] = false;
					}
				}
				catch (Exception e) {
					theReturn[i] = false;
				}
			}
		}
		return theReturn;
	}

	public String[] getHandlers(IWContext iwc) {
		return iwc.getParameterValues(PARAMETER_HANDLER_PREFIX);
	}

	public String[] getDescriptions(IWContext iwc) {
		String[] theReturn = iwc.getParameterValues(PARAMETER_DESC_PREFIX);
		if (theReturn == null) {
			String[] newString = { getDescription(iwc) };
			return newString;
		}
		else {
			return theReturn;
		}
	}

	public void propertySave(IWBundle iwb, String componentIdentifier, String methodIdentifier, String description,
			boolean isMultivalued, String[] handlers, String[] descriptions, boolean[] primaryKeys) throws Exception {
		boolean save = false;
		try {
			save = IBPropertyHandler.getInstance().saveNewProperty(iwb, componentIdentifier, methodIdentifier,
					description, isMultivalued, handlers, descriptions, primaryKeys);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (save) {
			add("Property saved successfully");
		}
		else {
			add("Property already exists, please delete before creating again");
		}
	}

	public TextInput getDescriptionInput(int index, String content) {
		//TextInput input = new TextInput(PARAMETER_DESC_PREFIX+index);
		TextInput input = new TextInput(PARAMETER_DESC_PREFIX);
		if (content != null) {
			input.setContent(content);
		}
		return input;
	}

	public CheckBox getPrimaryKeyBox(int index, boolean preChecked) {
		CheckBox cBox = new CheckBox(PARAMETER_PRIMARY_KEY);
		cBox.setValue(index);
		cBox.setChecked(preChecked);

		return cBox;
	}

	public DropdownMenu getMethodsDropdown(String selectedComponentKey) {
		return getMethodsDropdown(selectedComponentKey, PARAMETER_METHOD);
	}

	private void putMethodsInMap(Map m, Method[] methods) {
		putMethodsInMap(m, methods, "get");
	}

	private void putMethodsInMap(Map m, Method[] methods, String methodStartFilter) {
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = MethodFinder.getInstance().getMethodNameWidthParameters(method);
			//if(name.startsWith("set")){
			if (name.startsWith(methodStartFilter)) {
				m.put(name, method);
				System.out.println("Putting method for " + name);
			}
		}
	}

	public DropdownMenu getMethodsDropdown(String selectedComponentKey, String name) {
		Class selectedClass = null;
		Method[] methods = null;
		Map methodsMap = new HashMap();
		try {
			selectedClass = RefactorClassRegistry.forName(selectedComponentKey);
			Class introspectionClass = selectedClass;

			//		added by aron 21.june 2003
			if (this.selectedBundle != null) {

				String selectedCompType = this.selectedBundle.getComponentType(selectedComponentKey);
				BundleComponent comp = BundleComponentFactory.getInstance().getBundleComponent(selectedCompType);
				Class stopClass = comp.getFinalReflectionClass();

				Map map = MethodFinder.getInstance().getMapOfClassMethodsRecursive(selectedClass, stopClass,
						comp.getMethodStartFilters());
				methodsMap.putAll(map);
				/*
				 * boolean noStopClass = stopClass ==null;
				 * while(!introspectionClass.equals(stopClass)){ Method[]
				 * newMethods = introspectionClass.getMethods(); String[]
				 * filters = comp.getMethodStartFilters(); for (int i = 0; i <
				 * filters.length; i++) {
				 * putMethodsInMap(methodsMap,newMethods,filters[i]); }
				 * if(noStopClass) break; introspectionClass =
				 * introspectionClass.getSuperclass();
				 *  }
				 */
			}
			else {
				Class stopClass = PresentationObject.class;

				//info = Introspector.getBeanInfo(selectedClass,stopClass);

				while (!introspectionClass.equals(stopClass)) {
					Method[] newMethods = introspectionClass.getMethods();
					//System.out.println("newMethods.length="+newMethods.length);
					/*
					 * if(methods==null){ methods = newMethods;
					 * //System.out.println("NewComponentPropertyWindow 1 for:
					 * "+introspectionClass.getName()); } else{
					 * //System.out.println("NewComponentPropertyWindow 2 for:
					 * "+introspectionClass.getName()); int oldLength =
					 * methods.length; Method[] newArray = new
					 * Method[oldLength+newMethods.length];
					 * System.arraycopy(methods,0,newArray,0,oldLength);
					 * System.arraycopy(newMethods,0,newArray,oldLength,newMethods.length);
					 * methods = newArray; }
					 */

					putMethodsInMap(methodsMap, newMethods);
					introspectionClass = introspectionClass.getSuperclass();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		methods = (Method[]) methodsMap.values().toArray(new Method[0]);
		java.util.Arrays.sort(methods, com.idega.util.Comparators.getMethodComparator());
		//MethodDescriptor[] descriptors = info.getMethodDescriptors();
		//java.util.Arrays.sort(descriptors,com.idega.util.Comparators.getMethodDescriptorComparator());
		DropdownMenu methodsDrop = new DropdownMenu(name);
		methodsDrop.keepStatusOnAction();
		methodsDrop.setToSubmit();

		for (int i = 0; i < methods.length; i++) {
			//for (int i = 0; i < descriptors.length; i++) {
			Method method = methods[i];
			//Method method = descriptors[i].getMethod();
			String methodToString = MethodFinder.getInstance().getMethodNameWidthParameters(method);

			String methodIdentifier = MethodFinder.getInstance().getMethodIdentifierWithoutDeclaringClass(method);
			methodsDrop.addMenuElement(methodIdentifier, methodToString);
		}

		return methodsDrop;
	}

}