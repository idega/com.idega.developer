package com.idega.development.presentation;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import com.idega.development.business.DeveloperConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.GenericButton;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;

public class FilesManager extends Block {
	
	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		IWResourceBundle iwrb = getResourceBundle(iwc);
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		
		addJavaScript(iwc);
		
		Layer main = new Layer();
		
		Layer successTextContainer = new Layer();
		successTextContainer.setStyleAttribute("display: none");
		successTextContainer.add(new Text(iwrb.getLocalizedString("succeeded_to_copy_files", "Files were successfully copied")));
		main.add(successTextContainer);
		
		Layer failureTextContainer = new Layer();
		failureTextContainer.setStyleAttribute("display: none");
		failureTextContainer.add(new Text(iwrb.getLocalizedString("failed_to_copy_files", "Failed to copy files")));
		main.add(failureTextContainer);
		
		Layer exportButtonContainer = new Layer();
		GenericButton exportButton = new GenericButton(iwrb.getLocalizedString("export_ic_files_from_db", "Copy files from DB to repository"));
		exportButton.setStyleClass("button");
		StringBuffer action = new StringBuffer("copyFilesToSlide(['").append(iwrb.getLocalizedString("copying", "Copying..."));
		action.append("', '").append(successTextContainer.getId()).append("', '").append(failureTextContainer.getId()).append("']);");
		exportButton.setOnClick(action.toString());
		exportButtonContainer.add(exportButton);
		main.add(exportButtonContainer);
		
		add(main);
	}
	
	private void addJavaScript(IWContext iwc) {
		AddResource adder = AddResourceFactory.getInstance(iwc);
		
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, CoreConstants.DWR_ENGINE_SCRIPT);
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, "/dwr/interface/FilesManagerBusiness.js");
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, getBundle(iwc).getVirtualPathWithFileNameString("javascript/FilesManagerHelper.js"));
	}
	
	@Override
	public String getBundleIdentifier() {
		return DeveloperConstants.BUNDLE_IDENTIFIER;
	}

}