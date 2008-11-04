package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.PresentationUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSetter extends Block {

	public static String localesParameter = "iw_localeswitcher_locale";
	private int count = 0;

	public LocaleSetter() {
	}

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		if (iwc.getParameter("save") != null) {
			save(iwc);
		}

		Locale defLocale = iwc.getApplicationSettings().getDefaultLocale();
		ICLocale icDefLocale = ICLocaleBusiness.getICLocale(defLocale);

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("localeSetter");
		add(topLayer);

		Form form = new Form();
		topLayer.add(form);

		FieldSet fieldSet = new FieldSet("Active locales");
		fieldSet.setStyleClass("activeLocales");
		form.add(fieldSet);
		
		Table2 table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth("100%");
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		fieldSet.add(table);
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("inputColumn");
		cell.add(new Text("Use"));

		cell = row.createHeaderCell();
		cell.add(new Text("Language"));

		cell = row.createHeaderCell();
		cell.add(new Text("Country"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.setStyleClass("inputColumn");
		cell.add(new Text("Default"));

		group = table.createBodyRowGroup();
		
		this.count = 1;
		addToTable(group, ICLocaleBusiness.listOfLocales(true), icDefLocale);
		
		String setLocaleVariant = iwc.getApplicationSettings().getProperty("com.idega.core.localevariant");
		if(setLocaleVariant==null){
			setLocaleVariant="";
		}
		TextInput localeVariantInput = new TextInput("com.idega.core.localevariant", setLocaleVariant);
		
		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Locale Variant", localeVariantInput);
		formItem.add(label);
		formItem.add(localeVariantInput);
		fieldSet.add(formItem);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		fieldSet.add(buttonLayer);

		SubmitButton save = new SubmitButton("save", "Save");
		buttonLayer.add(save);
		
		fieldSet = new FieldSet("Available locales");
		fieldSet.setStyleClass("availableLocales");
		form.add(fieldSet);
		
		table = new Table2();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth("100%");
		table.setStyleClass("developerTable");
		table.setStyleClass("ruler");
		fieldSet.add(table);
		
		group = table.createHeaderRowGroup();
		row = group.createRow();
		
		cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("inputColumn");
		cell.add(new Text("Use"));

		cell = row.createHeaderCell();
		cell.add(new Text("Language"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text("Country"));

		group = table.createBodyRowGroup();
		
		addToTable(group, ICLocaleBusiness.listOfLocales(false), null);
		form.add(new HiddenInput("loc_count", String.valueOf(this.count)));
	}

	private void addToTable(TableRowGroup group, List listOfLocales, ICLocale defLocale) {
		if (listOfLocales != null) {
			Iterator I = listOfLocales.iterator();
			int rowCount = 1;
			while (I.hasNext()) {
				ICLocale icLocale = (ICLocale) I.next();
				Locale javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());
				if (javaLocale.getDisplayCountry().length() == 0 && !javaLocale.equals(Locale.ENGLISH)) {
					continue;
				}
				
				TableRow row = group.createRow();
				
				CheckBox checkBox = new CheckBox("loc_chk" + this.count++, String.valueOf(icLocale.getLocaleID()));
				checkBox.setChecked(icLocale.getInUse());
				
				TableCell2 cell = row.createCell();
				cell.setStyleClass("inputColumn");
				cell.setStyleClass("firstColumn");
				cell.add(checkBox);

				cell = row.createCell();
				cell.add(new Text(javaLocale.getDisplayLanguage() + " (" + javaLocale.getDisplayLanguage(javaLocale) + ")"));
				
				cell = row.createCell();
				cell.add(new Text(javaLocale.getDisplayCountry() + " (" + javaLocale.getDisplayCountry(javaLocale) + ")"));
				
				if (defLocale != null && icLocale.getInUse()) {
					RadioButton radio = new RadioButton("default_locale", icLocale.getName());
					if (defLocale.getLocaleID() == icLocale.getLocaleID()) {
						radio.setSelected();
					}
					cell = row.createCell();
					cell.setStyleClass("lastColumn");
					cell.setStyleClass("inputColumn");
					cell.add(radio);
				}
				
				if (rowCount % 2 == 0) {
					row.setStyleClass("evenRow");
				}
				else {
					row.setStyleClass("oddRow");
				}
				rowCount++;
			}
		}
	}

	private void save(IWContext iwc) {
		String sCount = iwc.getParameter("loc_count");

		if (sCount != null) {
			java.util.Vector<String> V = new java.util.Vector<String>();
			int count = Integer.parseInt(sCount) + 1;
			String chk;
			for (int i = 0; i < count; i++) {
				chk = iwc.getParameter("loc_chk" + i);
				if (chk != null) {
					V.add(chk);
				}
			}
			ICLocaleBusiness.makeLocalesInUse(V);

			String sDefLocale = iwc.getParameter("default_locale");
			if (sDefLocale != null) {
				iwc.getApplicationSettings().setDefaultLocale(ICLocaleBusiness.getLocaleFromLocaleString(sDefLocale));
			}
			
			String localeVariant = iwc.getParameter("com.idega.core.localevariant");
			String setLocaleVariant = iwc.getApplicationSettings().getProperty("com.idega.core.localevariant");
			if(setLocaleVariant==null){
				setLocaleVariant="";
			}
			if(localeVariant!=null){
				if(localeVariant.equals("")){
					iwc.getApplicationSettings().removeProperty("com.idega.core.localevariant");
				}
				else{
					iwc.getApplicationSettings().setProperty("com.idega.core.localevariant", localeVariant);
				}
				boolean update = !localeVariant.equals(setLocaleVariant);
				if(update){
					List bundleList = iwc.getIWMainApplication().getRegisteredBundles();
					for (Iterator iter = bundleList.iterator(); iter.hasNext();) {
						IWBundle bundle = (IWBundle) iter.next();
						if (bundle != null) {
							bundle.reloadBundle();
						}
					}
				}
			}
		}
	}
}