/**
 * 
 */
package com.idega.developer;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.component.UIComponent;

import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.localisation.presentation.LocaleSwitcher;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.KeyboardShortcut;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.development.business.DeveloperConstants;
import com.idega.development.presentation.ApplicationPropertySetter;
import com.idega.development.presentation.ApplicationStatus;
import com.idega.development.presentation.BundleComponentManager;
import com.idega.development.presentation.BundleCreator;
import com.idega.development.presentation.BundlePropertySetter;
import com.idega.development.presentation.BundleResourceManager;
import com.idega.development.presentation.Caches;
import com.idega.development.presentation.ComponentManager;
import com.idega.development.presentation.DBPoolStatusViewer;
import com.idega.development.presentation.FilesManager;
import com.idega.development.presentation.HomePageGenerator;
import com.idega.development.presentation.LocaleSetter;
import com.idega.development.presentation.Localizer;
import com.idega.development.presentation.LocalizerStorage;
import com.idega.development.presentation.Logs;
import com.idega.development.presentation.NotificationsManager;
import com.idega.development.presentation.ObjectTypeManager;
import com.idega.development.presentation.PageObjects;
import com.idega.development.presentation.SQLQueryer;
import com.idega.development.presentation.ScriptManager;
import com.idega.development.presentation.Versions;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.workspace.view.WorkspaceApplicationNode;
import com.idega.workspace.view.WorkspaceClassViewNode;


/**
 * <p>
 * </p>
 *  Last modified: $Date: 2009/03/11 08:17:16 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.15 $
 */
public class DeveloperViewManager {

	private ViewNode developerNode;
	private IWMainApplication iwma;
	/**
	 * <p>
	 * </p>
	 * @param iwma
	 * @return
	 */
	public static DeveloperViewManager getInstance(IWMainApplication iwma) {
		DeveloperViewManager instance = (DeveloperViewManager) iwma.getAttribute("developerviewmanager");
		if(instance==null){
			instance = new DeveloperViewManager();
			instance.iwma=iwma;
			iwma.setAttribute("developerviewmanager",instance);
		}
		return instance;
	}
	
	public ViewManager getViewManager(){
		return ViewManager.getInstance(this.iwma);
	}
	
	
	public ViewNode getDeveloperViewNode(){
		IWBundle iwb = this.iwma.getBundle(DeveloperConstants.BUNDLE_IDENTIFIER);
		if(this.developerNode==null){
			this.developerNode = initalizeDeveloperNode(iwb);
		}
		return this.developerNode;
	}

	/**
	 * <p>
	 * </p>
	 * @param iwb
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ViewNode initalizeDeveloperNode(IWBundle iwb) {
		ViewManager viewManager = ViewManager.getInstance(this.iwma);
		ViewNode workspace = viewManager.getWorkspaceRoot();
		
		Collection<String> roles = new ArrayList<String>();
		roles.add(StandardRoles.ROLE_KEY_DEVELOPER);
		
		DefaultViewNode devNode = new WorkspaceApplicationNode("developer",workspace,roles);
		devNode.setJspUri(iwb.getJSPURI("developer.jsp"));
		devNode.setKeyboardShortcut(new KeyboardShortcut("3"));
		
		WorkspaceClassViewNode localizerStorageNode = new WorkspaceClassViewNode("localizerstorage",devNode);
		localizerStorageNode.setName("Localizer Storage");
		localizerStorageNode.setComponentClass(LocalizerStorage.class);
		localizerStorageNode.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode localizerNode = new WorkspaceClassViewNode("localizer",devNode);
		localizerNode.setName("Localizer");
		localizerNode.setComponentClass(Localizer.class);
		localizerNode.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode localeSwitcher = new WorkspaceClassViewNode("localswitcher",devNode);
		localeSwitcher.setName("Switch Current Locale");
		localeSwitcher.setComponentClass(LocaleSwitcher.class);
		localeSwitcher.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode localeSetter = new WorkspaceClassViewNode("localesetter",devNode);
		localeSetter.setName("Set Locale");
		localeSetter.setComponentClass(LocaleSetter.class);
		localeSetter.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode objectTypes = new WorkspaceClassViewNode("objecttypes",devNode);
		objectTypes.setName("Object Types");
		objectTypes.setComponentClass(ObjectTypeManager.class);
		objectTypes.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode bundleCreator = new WorkspaceClassViewNode("bundlecreator",devNode);
		bundleCreator.setName("Create Bundle");
		bundleCreator.setComponentClass(BundleCreator.class);
		bundleCreator.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode bundlepropertysetter = new WorkspaceClassViewNode("bundleproperties",devNode);
		bundlepropertysetter.setComponentClass(BundlePropertySetter.class);
		bundlepropertysetter.setName("Bundle Properties");
		bundlepropertysetter.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode bundleresourcemanager = new WorkspaceClassViewNode("bundleresources",devNode);
		bundleresourcemanager.setName("Bundle Resources");
		bundleresourcemanager.setComponentClass(BundleResourceManager.class);
		bundleresourcemanager.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode componentmanager = new WorkspaceClassViewNode("componentmanager",devNode);
		componentmanager.setName("Component Manager");
		componentmanager.setComponentClass(ComponentManager.class);
		componentmanager.setMaximizeBlockVertically(true);

		WorkspaceClassViewNode registercomponent = new WorkspaceClassViewNode("registercomponent",devNode);
		registercomponent.setName("Register Component");
		registercomponent.setComponentClass(BundleComponentManager.class);
		registercomponent.setMaximizeBlockVertically(true);

		WorkspaceClassViewNode dbpool = new WorkspaceClassViewNode("dbpool",devNode);
		dbpool.setName("Database Pool");
		dbpool.setComponentClass(DBPoolStatusViewer.class);
		dbpool.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode sqlquery = new WorkspaceClassViewNode("sqlquery",devNode);
		sqlquery.setName("Query Database (SQL)");
		sqlquery.setComponentClass(SQLQueryer.class);
		sqlquery.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode appstatus = new WorkspaceClassViewNode("applicationstatus",devNode);
		appstatus.setName("Application Status");
		appstatus.setComponentClass(ApplicationStatus.class);
		appstatus.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode caches = new WorkspaceClassViewNode("caches",devNode);
		caches.setName("Caches");
		caches.setComponentClass(Caches.class);
		caches.setMaximizeBlockVertically(true);

		WorkspaceClassViewNode logs = new WorkspaceClassViewNode("logs",devNode);
		logs.setName("Logs");
		logs.setComponentClass(Logs.class);
		logs.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode versions = new WorkspaceClassViewNode("versions",devNode);
		versions.setName("Module Versions");
		versions.setComponentClass(Versions.class);
		versions.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode homepageGenerator = new WorkspaceClassViewNode("homepagegenerator",devNode);
		homepageGenerator.setName("Generate Homepages");
		homepageGenerator.setComponentClass(HomePageGenerator.class);
		homepageGenerator.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode pageobjects = new WorkspaceClassViewNode("pageobjects",devNode);
		pageobjects.setName("Page Objects");
		pageobjects.setComponentClass(PageObjects.class);
		pageobjects.setMaximizeBlockVertically(true);

		WorkspaceClassViewNode scriptmanager = new WorkspaceClassViewNode("scriptmanager",devNode);
		scriptmanager.setName("Script Manager");
		scriptmanager.setComponentClass(ScriptManager.class);
		scriptmanager.setMaximizeBlockVertically(true);
		
		try {
			Class<UIComponent> ldapmanagerClass = (Class<UIComponent>) Class.forName("com.idega.block.ldap.manager.LDAPManager");

			WorkspaceClassViewNode ldap = new WorkspaceClassViewNode("ldap",devNode);
			ldap.setName("LDAP Manager");
			ldap.setComponentClass(ldapmanagerClass);
			ldap.setMaximizeBlockVertically(true);
		} catch(ClassNotFoundException cnfe) {}
		
		try {
			Class<UIComponent> siteInfoClass = (Class<UIComponent>) Class.forName("com.idega.content.themes.presentation.SiteInfo");

			WorkspaceClassViewNode siteInfo = new WorkspaceClassViewNode("siteinfo",devNode);
			siteInfo.setName("Site Info");
			siteInfo.setComponentClass(siteInfoClass);
			siteInfo.setMaximizeBlockVertically(true);
		} catch(ClassNotFoundException cnfe) {}
		
		WorkspaceClassViewNode appPropertiesNode = new WorkspaceClassViewNode("applicationproperties",devNode);
		appPropertiesNode.setName("Application Properties");
		appPropertiesNode.setComponentClass(ApplicationPropertySetter.class);
		appPropertiesNode.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode filesManager = new WorkspaceClassViewNode("icfilesmanager", devNode);
		filesManager.setName("Files Manager");
		filesManager.setComponentClass(FilesManager.class);
		filesManager.setMaximizeBlockVertically(true);

		DefaultViewNode converters = new DefaultViewNode("converters", devNode);
		converters.setName("Converters");
		converters.setFaceletUri(iwb.getFaceletURI("converters.xhtml"));
		filesManager.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode notifications = new WorkspaceClassViewNode("notification", devNode);
		notifications.setName("Notifications");
		notifications.setComponentClass(NotificationsManager.class);
		notifications.setMaximizeBlockVertically(true);

		return devNode;
	}
}