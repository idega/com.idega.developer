package com.idega.development.presentation;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.FinderException;
import com.idega.core.idgenerator.business.UUIDBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationConstants;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.HorizontalRule;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.GroupTreeNode;
import com.idega.user.data.Group;
import com.idega.user.presentation.GroupChooser;
import com.idega.util.text.TextSoap;
/**
 * A manager for IdegaWeb's integrated LDAP server and replication services.
 *@author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *@version 1.0
 
 */
public class LDAPManager extends Block implements LDAPReplicationConstants,EmbeddedLDAPServerConstants {
	
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";
	
	private boolean isServerStarted = false;
	private IWResourceBundle iwrb;
	private IWBundle coreBundle;
	
	private static final String PARAM_SAVE_LDAP_PROPS_PATH = "save_lpad_props_path";
	private static final String PARAM_SAVE_LDAP_SETTINGS = "save_lpad_settings";
	private static final String PARAM_SAVE_BACKEND_SETTINGS = "save_backend";
	private static final String PARAM_SAVE_REPLICATION_SETTINGS = "save_reps";	
	private static final String PARAM_STOP_ALL_REPLICATORS = "stop_all_rep";
	private static final String PARAM_START_ALL_REPLICATORS = "start_all_rep";
	private static final String PARAM_DELETE_REPLICATION_SETTINGS = "del_rep";
	private static final String PARAM_NEW_REPLICATION_SETTINGS = "new_rep";
	private static final String PARAM_TOGGLE_START_STOP = "ldap_start_stop";
	private static final String PARAM_RUN_UUID_PROCESS = "run_uuid_proc";
	private static final String PARAM_UUID_PROCESS = "uuid_proc";
	private static final String PARAM_VALUE_CREATE_ALL_UNIQUE_IDs = "uuid_proc_cr_all";
	private static final String PARAM_VALUE_REMOVE_ALL_UNIQUE_IDs = "uuid_proc_re_all";
	private static final String PARAM_VALUE_CREATE_ALL_GROUP_UNIQUE_IDs = "uuid_proc_cr_gr";
	private static final String PARAM_VALUE_CREATE_ALL_USER_UNIQUE_IDs = "uuid_proc_cr_usr";
	private static final String PARAM_VALUE_REMOVE_ALL_GROUP_UNIQUE_IDs = "uuid_proc_re_gr";
	private static final String PARAM_VALUE_REMOVE_ALL_USER_UNIQUE_IDs = "uuid_proc_re_usr";
	
	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	private LDAPReplicationBusiness ldapReplicationBiz;
	private UUIDBusiness uuidBiz;
	
	private String pathToConfigFiles;
	
	private static String darkColor = "#BCBCBC";
	private static String lightColor = "#DEDEDE";
	
	private boolean justCreatedUUIDs = false;
	private boolean justRemovedUUIDs = false;
	
	private GroupBusiness groupBiz = null;
	
	public LDAPManager() {
	}
	
	public void main(IWContext iwc) throws Exception {
		iwrb = getResourceBundle(iwc);
		isServerStarted = getEmbeddedLDAPServerBusiness(iwc).isServerStarted();
		
		handleActions(iwc);
		
		//add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) getParentPage().setBackgroundColor("#FFFFFF");
		
		if (iwc.isLoggedOn()) {
			// maintain this parameter IWDeveloper.PARAMETER_CLASS_NAME
			//add server start stop button
			addBreak();
			addServerStatus(iwc);
			
			//add ldap settings
			addBreak();
			addLDAPSettings(iwc);
			
			//add backend settings
			addBreak();
			addBackendSettings(iwc);
			
			//add replication settings
			addBreak();
			addReplicationSettings(iwc);
			
			//add the universal unique id util, to create or remove all unique id for users and groups
			addBreak();
			addUniqueIdUtil(iwc);
		}
		else {
			add(iwrb.getLocalizedString("not.logged.on","Not logged on"));
		}
	}
	
	/**
	 * @param iwc
	 */
	private void handleActions(IWContext iwc) {
		try{
			if(iwc.isParameterSet(PARAM_TOGGLE_START_STOP)){
				startOrStopEmbeddedLDAPServer(iwc);
			}
			else if(iwc.isParameterSet(PARAM_SAVE_LDAP_SETTINGS)){
				applyLDAPChanges(iwc);
			}
			else if(iwc.isParameterSet(PARAM_SAVE_BACKEND_SETTINGS)){
				applyBackendChanges(iwc);	
			}
			else if(iwc.isParameterSet(PARAM_SAVE_REPLICATION_SETTINGS)){
				applyReplicationChanges(iwc);	
			}
			else if(iwc.isParameterSet(PARAM_SAVE_LDAP_PROPS_PATH)){
				getEmbeddedLDAPServerBusiness(iwc).setPathToLDAPConfigFiles(iwc.getParameter(PROPS_JAVALDAP_PROPS_REAL_PATH));
			}
			else if(iwc.isParameterSet(PARAM_NEW_REPLICATION_SETTINGS)){
				getLDAPReplicationBusiness(iwc).createNewReplicationSettings();
			}
			else if(iwc.isParameterSet(PARAM_DELETE_REPLICATION_SETTINGS)){
				String repNum = iwc.getParameter(PARAM_DELETE_REPLICATION_SETTINGS);
				getLDAPReplicationBusiness(iwc).deleteReplicator(Integer.parseInt(repNum));
			}
			else if(iwc.isParameterSet(PARAM_START_ALL_REPLICATORS)){
				getLDAPReplicationBusiness(iwc).startAllReplicators();
			}
			else if(iwc.isParameterSet(PARAM_STOP_ALL_REPLICATORS)){
				getLDAPReplicationBusiness(iwc).stopAllReplicators();
			}
			else if(iwc.isParameterSet(PARAM_RUN_UUID_PROCESS)){
				String param = iwc.getParameter(PARAM_UUID_PROCESS);
				
				if(param!=null){
					if(param.equals(PARAM_VALUE_CREATE_ALL_UNIQUE_IDs)){
						getUUIDBusiness(iwc).generateUUIDsForAllUsersAndGroups();
						justCreatedUUIDs = true;
					}
					else if(param.equals(PARAM_VALUE_REMOVE_ALL_UNIQUE_IDs)){
						getUUIDBusiness(iwc).removeUniqueIDsForUsersAndGroups();
						justRemovedUUIDs = true;
					}
					else if(param.equals(PARAM_VALUE_CREATE_ALL_GROUP_UNIQUE_IDs)){
						getUUIDBusiness(iwc).generateUUIDsForAllGroups();
						justRemovedUUIDs = true;
					}
					else if(param.equals(PARAM_VALUE_CREATE_ALL_USER_UNIQUE_IDs)){
						getUUIDBusiness(iwc).generateUUIDsForAllUsers();
						justRemovedUUIDs = true;
					}
					else if(param.equals(PARAM_VALUE_REMOVE_ALL_GROUP_UNIQUE_IDs)){
						getUUIDBusiness(iwc).removeUUIDsFromAllGroups();
						justRemovedUUIDs = true;
					}
					else if(param.equals(PARAM_VALUE_REMOVE_ALL_USER_UNIQUE_IDs)){
						getUUIDBusiness(iwc).removeUUIDsFromAllUsers();
						justRemovedUUIDs = true;
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @param iwc
	 * @throws RemoteException
	 */
	private void startOrStopEmbeddedLDAPServer(IWContext iwc) throws RemoteException {
		if(isServerStarted){
			getEmbeddedLDAPServerBusiness(iwc).stopEmbeddedLDAPServer();
		}
		else{
			getEmbeddedLDAPServerBusiness(iwc).startEmbeddedLDAPServer();
		}
		isServerStarted = getEmbeddedLDAPServerBusiness(iwc).isServerStarted();
	}
	
	/**
	 * @param iwc
	 * @throws IOException
	 */
	private void applyBackendChanges(IWContext iwc) throws IOException {
		Properties backendSettings = getEmbeddedLDAPServerBusiness(iwc).getBackendSettings();
		backendSettings.setProperty(PROPS_BACKEND_ZERO_ROOT,iwc.getParameter(PROPS_BACKEND_ZERO_ROOT));
		
		getEmbeddedLDAPServerBusiness(iwc).storeBackendProperties();
	}
	
	/**
	 * @param iwc
	 * @throws IOException
	 */
	private void applyLDAPChanges(IWContext iwc) throws IOException {
		Properties ldapSettings = getEmbeddedLDAPServerBusiness(iwc).getLDAPSettings();
		ldapSettings.setProperty(PROPS_JAVALDAP_SERVER_NAME,iwc.getParameter(PROPS_JAVALDAP_SERVER_NAME));
		ldapSettings.setProperty(PROPS_JAVALDAP_SERVER_PORT,iwc.getParameter(PROPS_JAVALDAP_SERVER_PORT));
		ldapSettings.setProperty(PROPS_JAVALDAP_DEBUG,iwc.getParameter(PROPS_JAVALDAP_DEBUG));
		ldapSettings.setProperty(PROPS_JAVALDAP_ROOTUSER,iwc.getParameter(PROPS_JAVALDAP_ROOTUSER));
		ldapSettings.setProperty(PROPS_JAVALDAP_ROOTPW,iwc.getParameter(PROPS_JAVALDAP_ROOTPW));
		toggleBooleanProperty(ldapSettings,PROPS_JAVALDAP_AUTO_START,iwc);
		getEmbeddedLDAPServerBusiness(iwc).storeLDAPProperties();
	}
	
	/**
	 * @param iwc
	 * @throws IOException
	 */
	private void applyReplicationChanges(IWContext iwc) throws IOException {
		Properties replicationSettings = getLDAPReplicationBusiness(iwc).getReplicationSettings();
		
		String num = replicationSettings.getProperty(PROPS_REPLICATION_NUM);
		int numberOfReplicators = Integer.parseInt(num);
		
		for(int i = 1; i<=numberOfReplicators; i++){
			
			//special cases because we cannot have "." in javascript variables
			String underScoreKey = TextSoap.findAndReplace(PROPS_REPLICATOR_PREFIX +i+PROPS_REPLICATOR_BASE_GROUP_ID, ".", "_");
			String baseGroupId = iwc.getParameter(underScoreKey);
			if(baseGroupId!=null){
				baseGroupId = baseGroupId.substring(baseGroupId.indexOf("_")+1);
			}
			replicationSettings.setProperty(PROPS_REPLICATOR_PREFIX +i+PROPS_REPLICATOR_BASE_GROUP_ID, baseGroupId);
			
			String underScoreKey2 = TextSoap.findAndReplace(PROPS_REPLICATOR_PREFIX +i+PROPS_REPLICATOR_PARENT_GROUP_ID, ".", "_");
			String parentGroupId = iwc.getParameter(underScoreKey2);
			if(parentGroupId!=null){
				parentGroupId = parentGroupId.substring(parentGroupId.indexOf("_")+1);
			}
			replicationSettings.setProperty(PROPS_REPLICATOR_PREFIX +i+PROPS_REPLICATOR_PARENT_GROUP_ID, parentGroupId);
			//
			
			//text cases
			setReplicationProperty(iwc, PROPS_REPLICATOR_BASE_RDN, i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_BASE_UNIQUE_ID,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_HOST,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_PORT,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_INTERVAL_MINUTES,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_SCHEDULER_STRING,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_SEARCH_TIMEOUT_MS,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_ROOT_USER,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_ROOT_PASSWORD,i);
			setReplicationProperty(iwc, PROPS_REPLICATOR_IWLDAPWS_URI,i);
			
			//booleans
			toggleBooleanProperty(replicationSettings, PROPS_REPLICATOR_PREFIX +i+ PROPS_REPLICATOR_REPLICATE_BASE_RDN, iwc);
			toggleBooleanProperty(replicationSettings, PROPS_REPLICATOR_PREFIX +i+ PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID, iwc);
			toggleBooleanProperty(replicationSettings, PROPS_REPLICATOR_PREFIX +i+ PROPS_REPLICATOR_ACTIVE, iwc);
			toggleBooleanProperty(replicationSettings, PROPS_REPLICATOR_PREFIX +i+ PROPS_REPLICATOR_ACTIVE_LISTENER, iwc);
			toggleBooleanProperty(replicationSettings, PROPS_REPLICATOR_PREFIX +i+ PROPS_REPLICATOR_REPEAT, iwc);
			
		}
		
		
		getLDAPReplicationBusiness(iwc).storeReplicationProperties();
	}
	
	
	
	/**
	 * @param iwc
	 * @param replicationSettings
	 * @param i
	 */
	private void setReplicationProperty(IWContext iwc, String propertyKey, int replicatorId) {
		Properties replicationSettings;
		try {
			replicationSettings = getLDAPReplicationBusiness(iwc).getReplicationSettings();
			replicationSettings.setProperty(PROPS_REPLICATOR_PREFIX +replicatorId+ propertyKey, iwc.getParameter(PROPS_REPLICATOR_PREFIX +replicatorId+ propertyKey));
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a table of properties and values from the javaldap.prop properties file
	 * @throws IOException
	 */
	private void addLDAPSettings(IWContext iwc) throws IOException {
		List editable = new ArrayList();
		editable.add(PROPS_JAVALDAP_DEBUG);
		editable.add(PROPS_JAVALDAP_ROOTPW);
		editable.add(PROPS_JAVALDAP_SERVER_NAME);
		editable.add(PROPS_JAVALDAP_SERVER_PORT);
		editable.add(PROPS_JAVALDAP_WEBSERVICE_PORT);
		editable.add(PROPS_JAVALDAP_AUTO_START);
		editable.add(PROPS_JAVALDAP_ROOTUSER);
		
		List checkBoxes = new ArrayList();
		checkBoxes.add(PROPS_JAVALDAP_AUTO_START);
		
		addSettings(iwc,PARAM_SAVE_LDAP_SETTINGS,getEmbeddedLDAPServerBusiness(iwc).getLDAPSettings(),iwrb.getLocalizedString("LDAPMANAGER.ldap.settings","LDAP Settings"),editable,null,checkBoxes,null,null,true);
	}
	
	/**
	 * Adds a table of properties and values from the backends.prop properties file
	 * @throws IOException
	 */
	private void addBackendSettings(IWContext iwc) throws IOException {
		List editable = new ArrayList();
		editable.add(PROPS_BACKEND_ZERO_ROOT);
		
		addSettings(iwc,PARAM_SAVE_BACKEND_SETTINGS, getEmbeddedLDAPServerBusiness(iwc).getBackendSettings(),iwrb.getLocalizedString("LDAPMANAGER.backend.settings","Backend Settings"),editable,null,null,null,null,false);
	}
	
	/**
	 * Adds a table of properties and values from the backends.prop properties file
	 * @throws IOException
	 */
	private void addReplicationSettings(IWContext iwc) throws IOException {
		Properties repProps = getLDAPReplicationBusiness(iwc).getReplicationSettings();
		List editable = new ArrayList();
		editable.add(PROPS_REPLICATOR_BASE_RDN);
		editable.add(PROPS_REPLICATOR_BASE_UNIQUE_ID);
		editable.add(PROPS_REPLICATOR_BASE_GROUP_ID);
		editable.add(PROPS_REPLICATOR_PARENT_GROUP_ID);
		editable.add(PROPS_REPLICATOR_HOST);
		editable.add(PROPS_REPLICATOR_PORT);
		editable.add(PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		editable.add(PROPS_REPLICATOR_INTERVAL_MINUTES);
		editable.add(PROPS_REPLICATOR_SCHEDULER_STRING);
		editable.add(PROPS_REPLICATOR_REPEAT);
		editable.add(PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		editable.add(PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT);
		editable.add(PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		editable.add(PROPS_REPLICATOR_ACTIVE);
		editable.add(PROPS_REPLICATOR_ROOT_USER);
		editable.add(PROPS_REPLICATOR_ROOT_PASSWORD);
		
		editable.add(PROPS_REPLICATOR_ACTIVE_LISTENER);
		editable.add(PROPS_REPLICATOR_IWLDAPWS_URI);
		
		List invisible = new ArrayList();
		invisible.add(PROPS_REPLICATION_NUM);
		
		List checkBoxes = new ArrayList();
		checkBoxes.add(PROPS_REPLICATOR_ACTIVE);
		checkBoxes.add(PROPS_REPLICATOR_ACTIVE_LISTENER);
		checkBoxes.add(PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		checkBoxes.add(PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		checkBoxes.add(PROPS_REPLICATOR_REPEAT);
		
		List splitter = new ArrayList();
		splitter.add(PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		
		Map specialIOMap = new HashMap();
		specialIOMap.put(PROPS_REPLICATOR_BASE_GROUP_ID, new GroupChooser(PROPS_REPLICATOR_BASE_GROUP_ID));
		specialIOMap.put(PROPS_REPLICATOR_PARENT_GROUP_ID, new GroupChooser(PROPS_REPLICATOR_PARENT_GROUP_ID));
		
		Text headerText = new Text(iwrb.getLocalizedString("LDAPMANAGER.replication.settings","Replication Settings"));
		headerText.setBold();
		headerText.setFontSize(Text.FONT_SIZE_10_HTML_2);
		add(headerText);
		add(new HorizontalRule());
		
		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		
		Table buttons = new Table(4,1);
		SubmitButton newRep = new SubmitButton(PARAM_NEW_REPLICATION_SETTINGS,iwrb.getLocalizedString("LDAPMANAGER.new.replication.settings","new replicator"));
		SubmitButton startAll = new SubmitButton(PARAM_START_ALL_REPLICATORS,iwrb.getLocalizedString("LDAPMANAGER.start.all.replicators","start all active replicators"));
		SubmitButton stopAll = new SubmitButton(PARAM_STOP_ALL_REPLICATORS,iwrb.getLocalizedString("LDAPMANAGER.stop.all.replicators","stop all replicators"));
		buttons.add(newRep,1,1);
		buttons.add(startAll,3,1);
		buttons.add(stopAll,4,1);
		
		Text text = new Text(iwrb.getLocalizedString("LDAPMANAGER.replication.scheduler.help.text","<b>Scheduler timer string format (-1 means every day/week/month/year...) :</b><br><i>\"minute (-1 or 0-59), hour (-1 or 0-23), day of month (-1 or 1-31), month (-1 or 0-11), day Of Week (-1 or 1-7) ,year (-1 or xxxx)\"</i>"));
		
		form.add(buttons);
		add(form);
		addBreak();
		add(text);
		addBreak();
		
		addSettings(iwc,PARAM_SAVE_REPLICATION_SETTINGS, repProps,null,editable,invisible,checkBoxes,splitter,specialIOMap,false);
	}
	
	
	
	/**
	 * Adds a table of properties and values from a properties file
	 * @throws IOException
	 */
	private void addSettings(IWContext iwc, String saveParameterName, Properties serverProps, String header, List editableKeys, List invisibleKeys, List checkBoxKeys, List keySplitters, Map customInterfaceObjectMap, boolean onlyShowEditable) throws IOException {
		Form settingsForm = new Form();
		settingsForm.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		Table settingsTable = new Table();
		settingsTable.setCellspacing(0);
		
		String rowColor = darkColor;
		
		SubmitButton save = new SubmitButton(saveParameterName,iwrb.getLocalizedString("LDAPMANAGER.save.changes","save changes"));
		
		
		if(header!=null){
			Text headerText = new Text(header);
			headerText.setBold();
			headerText.setFontSize(Text.FONT_SIZE_10_HTML_2);
			add(headerText);
			add(new HorizontalRule());
		}
		Iterator keys = serverProps.keySet().iterator();
		
		int row = 1;
		
		while (keys.hasNext()) {
			String key = (String)keys.next();
			//this is only for replication settings
			String temp = key.substring(key.indexOf(".")+1);
			int index = temp.indexOf(".");
			String cleanPropertyKey = temp;
			if(index>0){
				cleanPropertyKey = temp.substring(index);
			}
			
			//ends
			
			if(!isInvisibleKey(invisibleKeys,key)){
				boolean isEditable = isEditableKey(editableKeys,key);
				if(onlyShowEditable && !isEditable){
					continue;
				}
				
				
				String value = (String)serverProps.get(key);
				Text keyUpper = new Text(key.toUpperCase());
				keyUpper.setBold();
				settingsTable.add(keyUpper,1,row);
				if(isEditable){
					if(shouldBeCheckBox(key,checkBoxKeys)){
						CheckBox box = new CheckBox(key,value);
						if(value!=null && value.toLowerCase().equals("true")){
							box.setChecked(true);
						}
						settingsTable.add(box,2,row);
					}else if(isCustomIOKey(customInterfaceObjectMap,key)){
						Object obj = customInterfaceObjectMap.get(cleanPropertyKey);
						if(obj instanceof InterfaceObject){
							InterfaceObject iObj = (InterfaceObject)((InterfaceObject) obj).clone();
							iObj.setContent(value);
							iObj.setName(key);
							settingsTable.add(iObj,2,row);
						}
						else if(obj instanceof GroupChooser){
							//GroupChooser chooser = (GroupChooser)((GroupChooser)obj).clone();
							String underScoreKey = TextSoap.findAndReplace(key, ".", "_");
							GroupChooser chooser = new GroupChooser(underScoreKey);
							//special hack because group chooser is not an interfaceobject
							if(value!=null && !"".equals(value)){
								Group group;
								try {
									group = getGroupBusiness(iwc).getGroupByGroupID(Integer.parseInt(value));
									GroupTreeNode node = new GroupTreeNode(group,iwc.getApplicationContext());
									chooser.setSelectedNode(node);
									chooser.setChooserParameter(underScoreKey);
									settingsTable.add(chooser,2,row);
								}
								catch (NumberFormatException e) {
									e.printStackTrace();
								}
								catch (FinderException e) {
									e.printStackTrace();
								}
							}else{
								chooser.setChooserParameter(underScoreKey);
								settingsTable.add(chooser,2,row);
							}
						}
					}
					else{
						TextInput valueInput = new TextInput(key,value);
						settingsTable.add(valueInput,2,row);
					}
				}
				else{
					settingsTable.add(value,2,row);
				}
				
				if(keySplitters!=null && !keySplitters.isEmpty()){
					setColorForRow(settingsTable, rowColor, row);
				}
				row++;
				//change the color in the next row
				if(shouldSplit(key,keySplitters)){	
					setColorForRow(settingsTable, rowColor, row);
					addDeleteButton(settingsTable, row, key);
					if(rowColor.equals(darkColor)){
						rowColor = lightColor;
					}else{
						rowColor = darkColor;
					}
					row++;
				}
			}
		}
		settingsTable.add(save,3,row);
		settingsForm.add(settingsTable);
		add(settingsForm);
	}
	
	
	/**
	 * Adds a toolset for creating and removing UUIDs
	 * @throws IOException
	 */
	private void addUniqueIdUtil(IWContext iwc) throws IOException {
		Form uuidForm = new Form();
		uuidForm.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		
		Table settingsTable = new Table(2,7);
		settingsTable.setCellspacing(0);
		
		Text headerText = new Text(iwrb.getLocalizedString("LDAPMANAGER.UUID.util.header","Universally Unique Identifier Utility"));
		headerText.setBold();
		headerText.setFontSize(Text.FONT_SIZE_10_HTML_2);
		add(headerText);
		add(new HorizontalRule());
		
		RadioButton create = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_CREATE_ALL_UNIQUE_IDs);
		RadioButton remove = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_REMOVE_ALL_UNIQUE_IDs);
		
		RadioButton createGroup = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_CREATE_ALL_GROUP_UNIQUE_IDs);
		RadioButton removeGroup = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_REMOVE_ALL_GROUP_UNIQUE_IDs);
		
		RadioButton createUser = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_CREATE_ALL_USER_UNIQUE_IDs);
		RadioButton removeUser = new RadioButton(PARAM_UUID_PROCESS,PARAM_VALUE_REMOVE_ALL_USER_UNIQUE_IDs);
		
		SubmitButton save = new SubmitButton(PARAM_RUN_UUID_PROCESS,iwrb.getLocalizedString("LDAPMANAGER.run.process","run process"));
		save.setSubmitConfirm(iwrb.getLocalizedString("LDAPMANAGER.run.process.confirm","Are you sure you want to run the process? It could affect all users and groups in the database."));
		
		
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.create","Create UUID for all users and groups"),1,1);
		settingsTable.add(create,2,1);
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.remove","Remove all UUID from all users and groups"),1,2);
		settingsTable.add(remove,2,2);
		
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.createGroup","Create UUID for all groups"),1,3);
		settingsTable.add(createGroup,2,3);
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.removeGroup","Remove all UUID from all groups"),1,4);
		settingsTable.add(removeGroup,2,4);
		
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.createUser","Create UUID for all users"),1,5);
		settingsTable.add(createUser,2,5);
		settingsTable.add(iwrb.getLocalizedString("LDAPMANAGER.run.process.removeUser","Remove all UUID from all users"),1,6);
		settingsTable.add(removeUser,2,6);
		settingsTable.add(save,2,7);
		uuidForm.add(settingsTable);
		add(uuidForm);
		
		if(justCreatedUUIDs){
			addBreak();
			Text message = new Text(iwrb.getLocalizedString("LDAPMANAGER.run.process.create.done","Done creating UUID for all users and groups!"));
			message.setBold();
			add(message);
		}
		else if(justRemovedUUIDs){
			Text message = new Text(iwrb.getLocalizedString("LDAPMANAGER.run.process.remove.done","Done removing UUIDs from all users and groups!"));
			message.setBold();
			add(message);
		}
	}
	
	private void setColorForRow(Table settingsTable, String rowColor, int row) {
		settingsTable.setColor(1,row,rowColor);
		settingsTable.setColor(2,row,rowColor);
		settingsTable.setColor(3,row,rowColor);
	}
	
	/**
	 * @param settingsTable
	 * @param row
	 * @param key
	 */
	private void addDeleteButton(Table settingsTable, int row, String key) {
		//a little hack, only used for the replicator
		try{
			String num = getReplicatorNumber(key);
			SubmitButton deleteRep = new SubmitButton(iwrb.getLocalizedString("LDAPMANAGER.delete.replicator","delete replicator and reorder"),PARAM_DELETE_REPLICATION_SETTINGS,num);
			//settingsTable.mergeCells(3,1,3,row);
			settingsTable.add(deleteRep,3,row);
			
		}
		catch(Exception e){
			return;
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	private String getReplicatorNumber(String key) {
		int index = PROPS_REPLICATOR_PREFIX.length();
		int index2 = key.indexOf(".",index);
		return key.substring(index,index2);
	}
	
	/**
	 * Checks if the key is editable
	 * @param serverProps
	 * @param key
	 * @return
	 */
	private boolean isEditableKey(List editableKeys, String key) {
		if(editableKeys!=null && !editableKeys.isEmpty()){
			Iterator iter = editableKeys.iterator();
			while (iter.hasNext()) {
				String checkKey = (String) iter.next();
				if( key.indexOf(checkKey)>=0 ){
					return true;
				}
			}
			return false;
		}
		else return true;
	}
	
	
	/**
	 * Checks if the key has a custom interface object
	 * @param serverProps
	 * @param key
	 * @return
	 */
	private boolean isCustomIOKey(Map customIOObjects, String key) {
		if(customIOObjects!=null && !customIOObjects.isEmpty()){
			Iterator iter = customIOObjects.keySet().iterator();
			while (iter.hasNext()) {
				String checkKey = (String) iter.next();
				if( key.indexOf(checkKey)>=0 ){
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
	/**
	 * Checks if the key should be invisible
	 * @param serverProps
	 * @param key
	 * @return
	 */
	private boolean isInvisibleKey(List invisibleKeys, String key) {
		if(invisibleKeys!=null && !invisibleKeys.isEmpty()){
			Iterator iter = invisibleKeys.iterator();
			while (iter.hasNext()) {
				String checkKey = (String) iter.next();
				if( key.indexOf(checkKey)>=0 ){
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
	/**
	 * Needed because the replicator settings contain unknown numbers in them
	 * @param key
	 * @param checkBoxKeys
	 * @return
	 */
	private boolean shouldBeCheckBox(String key, List checkBoxKeys){
		if(checkBoxKeys!=null && !checkBoxKeys.isEmpty()){
			Iterator iter = checkBoxKeys.iterator();
			while (iter.hasNext()) {
				String checkKey = (String) iter.next();
				if( key.indexOf(checkKey)>=0 ){
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
	/**
	 * Needed because the replicator settings contain unknown numbers in them
	 * @param key
	 * @param splitKeys
	 * @return
	 */
	private boolean shouldSplit(String key, List splitKeys){
		if(splitKeys!=null && !splitKeys.isEmpty()){
			Iterator iter = splitKeys.iterator();
			while (iter.hasNext()) {
				String splitKey = (String) iter.next();
				if( key.indexOf(splitKey)>=0 ){
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
	private void toggleBooleanProperty(Properties props, String key, IWContext iwc){
		String oldValue = props.getProperty(key);
		String newValue = iwc.getParameter(key);
		
		if(newValue==null && oldValue != null){
			if(oldValue.toLowerCase().equals("true")){
				props.setProperty(key,"false");
			}
		}
		else {
			props.setProperty(key,"true");
		}
	}
	
	
	/**
	 * @param iwrb
	 */
	private void addServerStatus(IWContext iwc) {
		Form serverStatusForm = new Form();
		Table serverStarter = new Table(3,2);
		Text text = new Text(iwrb.getLocalizedString("LDAPMANAGER.embedded.server.status","Embedded LDAP server status : "));
		text.setBold();
		Text running = new Text(iwrb.getLocalizedString("LDAPMANAGER.embedded.server.status.running","Running"));
		running.setFontColor("green");
		running.setBold();
		Text stopped = new Text(iwrb.getLocalizedString("LDAPMANAGER.embedded.server.status.stopped","Stopped"));
		stopped.setFontColor("red");
		stopped.setBold();
		String start = iwrb.getLocalizedString("LDAPMANAGER.embedded.server.action.start","start");
		String stop = iwrb.getLocalizedString("LDAPMANAGER.embedded.server.action.stop","stop");
		SubmitButton startStop = new SubmitButton();
		startStop.setName(PARAM_TOGGLE_START_STOP);
		
		serverStarter.add(text,1,1);
		if(isServerStarted){
			serverStarter.add(running,2,1);
			startStop.setValue(stop);
		}
		else{
			serverStarter.add(stopped,2,1);
			startStop.setValue(start);
		}
		serverStarter.add(startStop,3,1);
		
		//add the realpath to the property files
		try {
			SubmitButton savePath = new SubmitButton(PARAM_SAVE_LDAP_PROPS_PATH,iwrb.getLocalizedString("LDAPMANAGER.save.changes","save changes"));
			Text realPath = new Text(iwrb.getLocalizedString(PROPS_JAVALDAP_PROPS_REAL_PATH,"Path to property files : "));
			realPath.setBold();
			TextInput path = new TextInput(PROPS_JAVALDAP_PROPS_REAL_PATH,getEmbeddedLDAPServerBusiness(iwc).getPathToLDAPConfigFiles());
			serverStarter.add(realPath,1,2);
			serverStarter.add(path,2,2);
			serverStarter.add(savePath,3,2);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		serverStatusForm.add(serverStarter);
		serverStatusForm.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		add(serverStatusForm);
	}
	
	
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(IWApplicationContext iwc) {
		if (embeddedLDAPServerBiz == null) {
			try {
				embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, EmbeddedLDAPServerBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwc) {
		if (ldapReplicationBiz == null) {
			try {
				ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, LDAPReplicationBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return ldapReplicationBiz;
	}
	
	public UUIDBusiness getUUIDBusiness(IWApplicationContext iwc) {
		if (uuidBiz == null) {
			try {
				uuidBiz = (UUIDBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UUIDBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return uuidBiz;
	}
	
	private GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc,GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}
}
