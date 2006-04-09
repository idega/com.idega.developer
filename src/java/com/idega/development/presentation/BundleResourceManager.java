/*
 * Created on Jan 5, 2004
 *  
 */
package com.idega.development.presentation;
import java.util.Locale;

import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.presentation.FileManager;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.util.LocaleUtil;
/**
 * BundleResourceManager
 * 
 * @author aron
 * @version 1.0
 */
public class BundleResourceManager extends Block {
	
	private static final String BUNDLE_PARAMETER = "iw_b_p_s";
	private static final String LOCALE_PARAMETER = "iw_l_p_s";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return "com.idega.developer";
	}
	
	public void main(IWContext iwc) {
		add(IWDeveloper.getTitleTable(this.getClass()));
		Locale locale = null;
		String folder = null;
		if (iwc.isParameterSet(LOCALE_PARAMETER)) {
			locale = LocaleUtil.getLocale(iwc.getParameter(LOCALE_PARAMETER));
		}
		String bundleIdentifier = iwc.getParameter(BUNDLE_PARAMETER);
		IWMainApplication iwma = iwc.getIWMainApplication();
		DropdownMenu bundles = BundlePropertySetter.getRegisteredBundlesDropdown(iwma, BUNDLE_PARAMETER);
		bundles.addMenuElementFirst("none", "none");
		bundles.keepStatusOnAction();
		bundles.setToSubmit();
		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, LOCALE_PARAMETER);
		localesDrop.addMenuElementFirst("none", "none");
		localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();
		String toplevel ="/resources";
		if (bundleIdentifier != null && !"none".equals(bundleIdentifier)) {
			IWBundle bundle = iwma.getBundle(bundleIdentifier);
			if (folder == null) {
				if (locale != null) {
					folder = bundle.getResourcesRealPath(locale);
					toplevel +="/"+ locale.toString() + ".locale";
				}
				else {
					folder = bundle.getResourcesRealPath();
				}
			}
		}
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(4,1);
		table.add(IWDeveloper.getText("Bundle:"), 1, 1);
		table.add(bundles, 2, 1);
		table.add(IWDeveloper.getText("Locale:"), 3, 1);
		table.add(localesDrop, 4, 1);
		table.setCellpadding(5);
		form.add(table);
		form.add(Text.getBreak());
		if (folder != null) {
			FileManager fm = new FileManager();
			fm.setStartingFolderRealPath(folder);
			fm.setTopLevelBrowseFolder(toplevel);
			fm.setBundleIdentifier(getBundleIdentifier());
			fm.setSkipFolders(new String[]{"CVS",".locale"});
			//fm.setSkipFiles(new String[]{"Localizable.strings","Localized.strings"});
			fm.addMaintainedParameter(IWDeveloper.actionParameter);
			fm.addMaintainedParameter(IWDeveloper.PARAMETER_CLASS_NAME);
			fm.addMaintainedParameter(LOCALE_PARAMETER);
			fm.addMaintainedParameter(BUNDLE_PARAMETER);
			add(fm);
		}
	}
	
}
