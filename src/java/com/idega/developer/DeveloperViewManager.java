/**
 * 
 */
package com.idega.developer;

import java.util.ArrayList;
import java.util.Collection;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.FramedWindowClassViewNode;
import com.idega.core.view.KeyboardShortcut;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.development.presentation.ApplicationPropertySetter;
import com.idega.development.presentation.ApplicationStatus;
import com.idega.development.presentation.BundleComponentManager;
import com.idega.development.presentation.BundleCreator;
import com.idega.development.presentation.BundlePropertySetter;
import com.idega.development.presentation.BundleResourceManager;
import com.idega.development.presentation.Caches;
import com.idega.development.presentation.ComponentManager;
import com.idega.development.presentation.DBPoolStatusViewer;
import com.idega.development.presentation.HomePageGenerator;
import com.idega.development.presentation.IWDeveloper;
import com.idega.development.presentation.LDAPManager;
import com.idega.development.presentation.LocaleSetter;
import com.idega.development.presentation.Localizer;
import com.idega.development.presentation.Logs;
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
 * TODO tryggvil Describe Type SchoolViewManager
 * </p>
 *  Last modified: $Date: 2006/02/23 16:11:03 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class DeveloperViewManager {

	private ViewNode developerNode;
	private IWMainApplication iwma;
	/**
	 * <p>
	 * TODO tryggvil describe method getInstance
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
		return ViewManager.getInstance(iwma);
	}
	
	
	public ViewNode getDeveloperViewNode(){
		IWBundle iwb = iwma.getBundle("com.idega.developer");
		if(developerNode==null){
			developerNode = initalizeDeveloperNode(iwb);
		}
		return developerNode;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initalizeSchoolNode
	 * </p>
	 * @param iwb
	 * @return
	 */
	private ViewNode initalizeDeveloperNode(IWBundle iwb) {
		ViewManager viewManager = ViewManager.getInstance(iwma);
		ViewNode workspace = viewManager.getWorkspaceRoot();
		
		Collection roles = new ArrayList();
		roles.add(StandardRoles.ROLE_KEY_DEVELOPER);
		
		DefaultViewNode devNode = new WorkspaceApplicationNode("developer",workspace,roles);
		//devNode.setName("#{localizedStrings['com.idega.developer']['developer']}");
		devNode.setKeyboardShortcut(new KeyboardShortcut("3"));
		
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
		
		/*WorkspaceClassViewNode updateManager = new WorkspaceClassViewNode("updatemanager",devNode);
		updateManager.setName("Update manager");
		updateManager.setComponentClass(UpdateManager.class);
		updateManager.setMaximizeBlockVertically(true);*/
		
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
		
		WorkspaceClassViewNode ldap = new WorkspaceClassViewNode("ldap",devNode);
		ldap.setName("LDAP Manager");
		ldap.setComponentClass(LDAPManager.class);
		ldap.setMaximizeBlockVertically(true);
		
		WorkspaceClassViewNode appPropertiesNode = new WorkspaceClassViewNode("applicationproperties",devNode);
		appPropertiesNode.setName("Application Properties");
		appPropertiesNode.setComponentClass(ApplicationPropertySetter.class);
		appPropertiesNode.setMaximizeBlockVertically(true);
		
		Class applicationClass = IWDeveloper.class;
		FramedWindowClassViewNode oldDeveloperNode = new FramedWindowClassViewNode("olddeveloper",devNode);
		oldDeveloperNode.setName("Old Developer");
		oldDeveloperNode.setWindowClass(applicationClass);
		
		String jspPath = iwma.getBundle("com.idega.workspace").getJSPURI("workspace.jsp");
		oldDeveloperNode.setJspUri(jspPath);
		
		//oldDeveloperNode.setJspUri(workspace.getResourceURI());

		return devNode;
	}
	
}
