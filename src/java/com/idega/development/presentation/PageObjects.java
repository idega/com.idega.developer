/*
 * Created on 22.6.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.development.presentation;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idega.builder.data.IBPageObjectView;
import com.idega.builder.data.IBPageObjectViewHome;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * @author aron
 *
 * PageObjects shows where objects are placed in the pagetree
 */
public class PageObjects extends Block {
	
	private static final String PRM_PAGEID = "iw_bpid";
	private static final String PRM_PAGENAME = "iw_bpni";
	private static final String PRM_CLASSNAME = "iw_clni";
	private static final String PRM_TYPE = "iw_otpe";
	private static final String BUNDLE_PARAMETER = "iw_b_p_s";
	
	private Collection pageObjects = null;

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");
		
		String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);
		IWMainApplication iwma = iwc.getIWMainApplication();
		DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma, BUNDLE_PARAMETER);
		bundles.addMenuElementFirst("none", "none");
		//bundles.keepStatusOnAction();
		DropdownMenu typesDrop = new DropdownMenu(PRM_TYPE);
		List componentTypes = ICObjectBMPBean.getAvailableComponentTypes();
		typesDrop.addMenuElement("","Object type");
		Collections.sort(componentTypes);
		for (Iterator iter = componentTypes.iterator(); iter.hasNext();) {
			typesDrop.addMenuElement((String)iter.next());
		}
		typesDrop.keepStatusOnAction();
		
		TextInput pageIdInput = new TextInput(PRM_PAGEID);
		TextInput pageNameInput = new TextInput(PRM_PAGENAME);
		TextInput classNameInput = new TextInput(PRM_CLASSNAME);
		classNameInput.setLength(40);
		SubmitButton button1 = new SubmitButton("Search");
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		Table table = new Table();
		table.add(getHeader("Page id"),1,1);
		table.add(getHeader("Page name"),2,1);
		table.add(getHeader("Bundle"),3,1);
		table.add(getHeader("Class"),4,1);
		table.add(pageIdInput,1,2);
		table.add(pageNameInput,2,2);
		table.add(bundles,3,2);
		table.add(classNameInput,4,2);
		table.add(button1,1,3);
		table.add(typesDrop,2,3);
		form.add(table);
		add(Text.getBreak());
		add(form);
		add(Text.getBreak());
		
		IBPageObjectViewHome instanceHome = (IBPageObjectViewHome)IDOLookup.getHome(IBPageObjectView.class);
		boolean showInstanceCount = false;
		boolean isObjectTypeSet = iwc.isParameterSet(PRM_TYPE);
		String objectType = null;
		if(isObjectTypeSet)
			objectType = iwc.getParameter(PRM_TYPE);
		pageObjects = null;
		if(iwc.isParameterSet(PRM_PAGEID)){
			String pageId = iwc.getParameter(PRM_PAGEID);
			if(isObjectTypeSet)
				pageObjects = instanceHome.findByPageAndObjectType(Integer.valueOf(pageId),objectType);
			else
				pageObjects = instanceHome.findByPage(Integer.valueOf(pageId));
			presentatePageIdObjects(iwc);
		}
		else if(iwc.isParameterSet(PRM_PAGENAME)){
			String pageName = iwc.getParameter(PRM_PAGENAME);
			if(isObjectTypeSet)
				pageObjects = instanceHome.findByPageNameAndObjectType(pageName,objectType);
			else
				pageObjects = instanceHome.findByPageName(pageName);
			presentatePageNameObjects(iwc);
		}
		else if(iwc.isParameterSet(PRM_CLASSNAME)){
			String className = iwc.getParameter(PRM_CLASSNAME);
			if(isObjectTypeSet)
				pageObjects = instanceHome.findByClassNameAndObjectType(className,objectType);
			else
				pageObjects = instanceHome.findByClassName(className);
			presentateClassNameObjects(iwc);
		}
		else if(iwc.isParameterSet(BUNDLE_PARAMETER)){
			String bundle = iwc.getParameter(BUNDLE_PARAMETER);
			if(isObjectTypeSet)
				pageObjects = instanceHome.findByBundleAndObjectType(bundle,objectType);
			else;
				pageObjects = instanceHome.findByBundle(bundle);
			presentateBundleObjects(iwc);
		}
		
	}
	
	private void presentatePageIdObjects(IWContext iwc){
		if(pageObjects!=null && !pageObjects.isEmpty()){
			Table T = new Table();
			T.setColumns(6);
			
			int i = 0;
			int row = 1;
			int lastPageId = -1;
			T.add(getHeader("Instance id"),1,row);
			T.add(getHeader("Object id"),2,row);
			T.add(getHeader("Object name"),3,row);
			T.add(getHeader("Object type"),4,row);
			T.add(getHeader("Bundle"),5,row);
			T.add(getHeader("Class"),6,row);
			row++;
			
			for (Iterator iter = pageObjects.iterator(); iter.hasNext();) {
				IBPageObjectView pageObject = (IBPageObjectView) iter.next();
				int currentPageId = pageObject.getPageId().intValue();
				if(lastPageId!=currentPageId){
					row++;
					Link pageIdLink = new Link(getHeader(pageObject.getPageId().toString()));
					pageIdLink.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME,iwc);
					pageIdLink.addParameter(PRM_PAGEID,pageObject.getPageId().toString());
					T.add(getHeader("Page ID: "),1,row);
					T.add(pageIdLink,1,row);
					T.add(getHeader("Page Name: "+pageObject.getPageName()),3,row);
					T.add(getHeader("Template Id: "+pageObject.getTemplateId()),5,row);
					T.mergeCells(1,row,2,row);
					T.mergeCells(3,row,4,row);
					T.mergeCells(5,row,6,row);
					
					row++;
				}
				T.add(getText(String.valueOf(pageObject.getObjectInstanceId())),1,row);
				T.add(getText(String.valueOf(pageObject.getObjectId())),2,row);
				T.add(getText(pageObject.getObjectName()),3,row);
				T.add(getText(pageObject.getObjectType()),4,row);
				T.add(getText(pageObject.getBundleName()),5,row);
				T.add(getText(pageObject.getClassName()),6,row);
				row++;
				lastPageId = currentPageId;
			}
			add(T);
		
		}
	}
	
	private Text getText(String text){
		Text t = new Text(text);
		return t;
	}
	
	private Text getHeader(String text){
		return IWDeveloper.getText(text);
	}
	
	private void presentatePageNameObjects(IWContext iwc){
		presentatePageIdObjects(iwc);
	}
	
	private void presentateClassNameObjects(IWContext iwc){
		presentatePageIdObjects(iwc);
	}
	
	private void presentateBundleObjects(IWContext iwc){
		presentatePageIdObjects(iwc);
	}
}
