package com.idega.development.presentation;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.HorizontalRule;
import com.idega.presentation.text.Text;
import com.idega.repository.data.RefactorClassRegistry;
/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWDeveloper extends com.idega.presentation.app.IWApplication {

	public static final String actionParameter = "iw_developer_action";
	public static final String dbPoolStatusViewerParameter = "iw_poolstatus_viewer";
	public static final String updateManagerParameter = "iw_update_manager";
	public static final String frameName = "iwdv_rightFrame";
	
	public static final String PARAMETER_CLASS_NAME = "iwdv_class_name";

	public IWDeveloper() {
		super("idegaWeb Developer");
		add(IWDeveloper.IWDevPage.class);
		super.setResizable(true);
		super.setScrollbar(true);
		super.setScrolling(1, true);
		super.setWidth(800);
		super.setHeight(600);
		//super.setOnLoad("moveTo(0,0);");
	}

	public static class IWDevPage extends com.idega.presentation.ui.Window {

		public IWDevPage() {
			this.setStatus(true);
		}

		public void main(IWContext iwc) throws Exception {
			IWBundle iwbCore = getBundle(iwc);
			
			if (iwc.isIE())
				getParentPage().setBackgroundColor("#B0B29D");
				
			Layer topLayer = new Layer(Layer.DIV);
			topLayer.setZIndex(3);
			topLayer.setPositionType(Layer.FIXED);
			topLayer.setTopPosition(0);
			topLayer.setLeftPosition(0);
			topLayer.setBackgroundColor("#0E2456");
			topLayer.setWidth(Table.HUNDRED_PERCENT);
			topLayer.setHeight(25);
			add(topLayer);
			
			Table headerTable = new Table();
			headerTable.setCellpadding(0);
			headerTable.setCellspacing(0);
			headerTable.setWidth(Table.HUNDRED_PERCENT);
			headerTable.setAlignment(2,1,Table.HORIZONTAL_ALIGN_RIGHT);
			topLayer.add(headerTable);

			Image idegaweb = iwbCore.getImage("/editorwindow/idegaweb.gif","idegaWeb");
			headerTable.add(idegaweb,1,1);

			Text adminTitle = new Text("idegaWeb Developer");
			adminTitle.setStyleAttribute("color:#FFFFFF;font-family:Arial,Helvetica,sans-serif;font-size:12px;font-weight:bold;margin-right:5px;");
			headerTable.add(adminTitle,2,1);

			
			Layer leftLayer = new Layer(Layer.DIV);
			leftLayer.setZIndex(2);
			leftLayer.setPositionType(Layer.FIXED);
			leftLayer.setTopPosition(25);
			leftLayer.setLeftPosition(0);
			leftLayer.setPadding(5);
			leftLayer.setBackgroundColor("#B0B29D");
			leftLayer.setWidth(180);
			leftLayer.setHeight(Table.HUNDRED_PERCENT);
			add(leftLayer);
			
			DeveloperList list = new DeveloperList();
			leftLayer.add(list);

			Layer rightLayer = new Layer(Layer.DIV);
			rightLayer.setZIndex(1);
			rightLayer.setPositionType(Layer.ABSOLUTE);
			rightLayer.setTopPosition(25);
			rightLayer.setPadding(5);
			if (iwc.isIE()) {
				rightLayer.setBackgroundColor("#FFFFFF");
				rightLayer.setWidth(Table.HUNDRED_PERCENT);
				rightLayer.setHeight(Table.HUNDRED_PERCENT);
				rightLayer.setLeftPosition(180);
			}
			else
				rightLayer.setLeftPosition(190);
			add(rightLayer);
			
			if (iwc.isParameterSet(PARAMETER_CLASS_NAME)) {
				String className = IWMainApplication.decryptClassName(iwc.getParameter(PARAMETER_CLASS_NAME));
				PresentationObject obj = (PresentationObject) RefactorClassRegistry.getInstance().newInstance(className, this.getClass());
				rightLayer.add(obj);
			}
			else {
				rightLayer.add(new Localizer());
			}
		}
	}

	public static Table getTitleTable(String displayString, Image image) {
		Table titleTable = new Table(1, 2);
		titleTable.setCellpadding(0);
		titleTable.setCellspacing(0);
		titleTable.setWidth("100%");
		Text headline = getText(displayString);
		headline.setFontSize(Text.FONT_SIZE_14_HTML_4);
		headline.setFontColor("#0E2456");
		if (image != null) {
			image.setHorizontalSpacing(5);
			titleTable.add(image, 1, 1);
		}
		titleTable.add(headline, 1, 1);
		titleTable.add(new HorizontalRule("100%", 2, "color: #FF9310", true), 1, 2);
		return titleTable;
	}

	public static Table getTitleTable(String displayString) {
		return getTitleTable(displayString, null);
	}

	public static Table getTitleTable(Class classToUse, Image image) {
		return getTitleTable(classToUse.getName().substring(classToUse.getName().lastIndexOf(".") + 1), image);
	}

	public static Table getTitleTable(Class classToUse) {
		return getTitleTable(classToUse, null);
	}

	public static Text getText(String text) {
		Text T = new Text(text);
		T.setBold();
		T.setFontFace(Text.FONT_FACE_VERDANA);
		T.setFontSize(Text.FONT_SIZE_10_HTML_2);
		return T;
	}
}
