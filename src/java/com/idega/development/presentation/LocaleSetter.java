package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSetter extends PresentationObjectContainer {

	public static String localesParameter = "iw_localeswitcher_locale";
	private int count = 0;
	private Locale _coreLocale = null;

	public LocaleSetter() {
	}

	public void main(IWContext iwc) {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}

		this._coreLocale = iwc.getIWMainApplication().getCoreLocale();

		if (iwc.getParameter("save") != null) {
			save(iwc);
		}

		Locale defLocale = iwc.getApplicationSettings().getDefaultLocale();
		ICLocale icDefLocale = ICLocaleBusiness.getICLocale(defLocale);

		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		Table T = new Table();
		T.add(IWDeveloper.getText("Use"), 1, 1);
		T.add(IWDeveloper.getText("Country"), 2, 1);
		T.add(IWDeveloper.getText("Language"), 3, 1);
		T.add(IWDeveloper.getText("Region"), 4, 1);
		T.add(IWDeveloper.getText("Default"), 5, 1);

		this.count = 1;
		addToTable(T, ICLocaleBusiness.listOfLocales(true), icDefLocale);
		SubmitButton save = new SubmitButton("save", "Save");
		this.count++;
		T.add(save, 1, this.count);
		this.count++;
		addToTable(T, ICLocaleBusiness.listOfLocales(false), null);
		T.add(new HiddenInput("loc_count", String.valueOf(this.count)));
		T.setCellpadding(2);
		//T.setBorder(1);
		form.add(T);
		add(form);
	}

	private void addToTable(Table T, List listOfLocales, ICLocale defLocale) {
		if (listOfLocales != null) {
			CheckBox chk;
			RadioButton rb;
			ICLocale icLocale;
			Locale javaLocale;
			Iterator I = listOfLocales.iterator();
			while (I.hasNext()) {
				this.count++;
				icLocale = (ICLocale) I.next();
				javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());
				chk = new CheckBox("loc_chk" + this.count, String.valueOf(icLocale.getLocaleID()));
				chk.setChecked(icLocale.getInUse());
				if (javaLocale.equals(this._coreLocale)) {
					chk.setDisabled(true);
					T.add(new HiddenInput("loc_chk" + this.count, String.valueOf(icLocale.getLocaleID())), 1, this.count);
				}
				T.add(chk, 1, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayCountry()), 2, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayLanguage()), 3, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayVariant()), 4, this.count);
				if (defLocale != null && icLocale.getInUse()) {
					rb = new RadioButton("default_locale", icLocale.getName());
					T.add(rb, 5, this.count);
					if (defLocale.getLocaleID() == icLocale.getLocaleID()) {
						rb.setSelected();
					}
				}
			}
		}
	}

	private void save(IWContext iwc) {
		String sCount = iwc.getParameter("loc_count");

		if (sCount != null) {
			java.util.Vector V = new java.util.Vector();
			int count = Integer.parseInt(sCount);
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
		}
	}

}
