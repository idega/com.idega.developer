package com.idega.development.presentation;

import java.util.List;
import java.util.logging.Level;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWApplicationContext;
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
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;

/**
*
* 
* @author <a href="anton@idega.com">Anton Makarov</a>
* @version $Revision: 1.5 $
*
* Last modified: $Date: 2009/01/05 10:27:23 $ by $Author: anton $
*
*/

public class LocalizerStorage extends Block {

	private static final String PRIORITY_LEVELS = "priority_levels";
	private static final String STORAGE_IDENTIFIER = "storage_identifier";
	private static final String AUTO_INSERT = "auto_insert";
	
	public LocalizerStorage() {
	}

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		//iwb.getRe
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesStyleSheet());
		
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/Localizer.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getWeb2Business(iwc).getBundleUriToHumanizedMessagesScript());

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("localeStorageSetter");
		add(topLayer);

		Form form = new Form();
		topLayer.add(form);
		
		FieldSet fieldSet = new FieldSet("Active Storages");
		fieldSet.setStyleClass("activeStorages");
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
		cell.add(new Text("Storage Id"));

		cell = row.createHeaderCell();
		cell.setStyleClass("inputColumn");
		cell.add(new Text("Priority level"));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.setStyleClass("inputColumn");
		cell.add(new Text("Auto insert"));
		
		group = table.createBodyRowGroup();
		addAvailableMessageResources(iwc, group);
	}
	
	private void addAvailableMessageResources(IWContext iwc, TableRowGroup group) {
		List<MessageResource> resources = iwc.getIWMainApplication().getMessageFactory().getAvailableUninitializedMessageResources();
		int count = 0;
		for(MessageResource resource : resources) {
			count++;
			TableRow row = group.createRow();
		
			TableCell2 cell = row.createCell();
			cell.setStyleClass("firstColumn");
			Text identifier = new Text(resource.getIdentifier());
			identifier.setID(STORAGE_IDENTIFIER + CoreConstants.UNDER + count);
			cell.add(identifier);
			
			cell = row.createCell();
			cell.setStyleClass("inputColumn");
			DropdownMenu priorityDrop = getAvailablePriorityLevels();
			priorityDrop.setId(PRIORITY_LEVELS + CoreConstants.UNDER + count);
			priorityDrop.setSelectedElement(getPriorityLevel(iwc, resource.getIdentifier()).intValue() /*resource.getLevel().intValue()*/);
			priorityDrop.setOnChange(getPriorityChangeScript(count));
			cell.add(priorityDrop);
			
			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.setStyleClass("inputColumn");

			CheckBox checkBox = new CheckBox(resource.getIdentifier());
			checkBox.setID(AUTO_INSERT + CoreConstants.UNDER + count);
			checkBox.setChecked(getAutoInsert(iwc, resource.getIdentifier()));
			
			if(!DefaultIWBundle.isProductionEnvironment()) {
				checkBox.setOnChange(getAutoInsertChangeScript(count));
			} else {
				checkBox.setDisabled(true);
			}
			
			cell.add(checkBox);
			
			if (count % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
		}			
	}
	
	private boolean getAutoInsert(IWContext iwc, String storageIdentifier) {
		boolean autoInsert = true;
		List<MessageResource> resources = iwc.getIWMainApplication().getMessageFactory().getResourceListByStorageIdentifier(storageIdentifier);
		for(MessageResource resource : resources) {
			autoInsert = autoInsert && resource.isAutoInsert();
		}
		return autoInsert;
	}
	
	private Level getPriorityLevel(IWContext iwc, String storageIdentifier) {
		List<MessageResource> resources = iwc.getIWMainApplication().getMessageFactory().getResourceListByStorageIdentifier(storageIdentifier);
		//All resources should have the same level so we take first resource
		if(!resources.isEmpty())
			return resources.get(0).getLevel();
		return null;
	}

	public static DropdownMenu getAvailablePriorityLevels() {
		List<Level> levels = MessageResourceImportanceLevel.levelList();
		DropdownMenu down = new DropdownMenu(PRIORITY_LEVELS);
		for(Level level : levels) {
			down.addMenuElement(level.intValue(), level.getName());
		}
		return down;
	}
	
	private String getPriorityChangeScript(int resourceCounter) {
		StringBuffer script = new StringBuffer();
		script.append("Localizer.setPriorityLevel(")
			  .append("dwr.util.getValue('").append(STORAGE_IDENTIFIER + CoreConstants.UNDER + resourceCounter).append("'), ")
			  .append("dwr.util.getValue('").append(PRIORITY_LEVELS + CoreConstants.UNDER + resourceCounter)
			  .append("'), {callback: function(value) { if(value==1) { humanMsg.displayMsg('Priority level changed...'); } } });");
		return script.toString();
	}
	
	private String getAutoInsertChangeScript(int resourceCounter) {
		StringBuffer script = new StringBuffer();
		script.append("Localizer.setAutoInsert(")
			  .append("dwr.util.getValue('").append(STORAGE_IDENTIFIER + CoreConstants.UNDER + resourceCounter).append("'), ")
			  .append("dwr.util.getValue('").append(AUTO_INSERT + CoreConstants.UNDER + resourceCounter)
			  .append("'), {callback: function(value) { if(value==1) { humanMsg.displayMsg('AutoInsert property changed...'); } } });");
		return script.toString();
	}
	
	private Web2Business getWeb2Business(IWApplicationContext iwac) {
		try {
			return (Web2Business) IBOLookup.getServiceInstance(iwac, Web2Business.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
}