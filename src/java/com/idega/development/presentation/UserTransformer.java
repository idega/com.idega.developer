/*
 * Created on 25.6.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.development.presentation;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.user.business.UserBusiness;
import com.idega.data.DatastoreInterface;
import com.idega.data.IDOLookup;
import com.idega.data.SapDBDatastoreInterface;
import com.idega.idegaweb.presentation.BusyBar;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.IWTimestamp;

/**
 * @author aron
 *
 * UserTransformer transforms old users to new users
 */
public class UserTransformer extends Block{
	private static final String PARAM_NAME_TRANSFORM = "transform_old_users";
	private static final String PARAM_NAME_SHOW_OLD = "show_old_users";
	private static final String PARAM_NAME_CREATE_OLD = "create_old";
	
	DateFormat df;
	String currentDate = null;
	
	public void main(IWContext iwc)throws Exception{
		df = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,iwc.getCurrentLocale());
		String sql = iwc.getParameter("user_select_sql");
		
		
		Form form = new Form();
		TextArea ta = new TextArea("users_select_sql");
		ta.keepStatusOnAction(true);
		ta.setColumns(70);
		ta.setRows(10);
		int row = 1;
		Table table = new Table();
		table.add("Tranforms selected users from old user to new user",1,row++);
		table.add(ta,1,row++);
		SubmitButton fix = new SubmitButton("Transform users", PARAM_NAME_TRANSFORM, "true");
		SubmitButton show = new SubmitButton("Show users", PARAM_NAME_SHOW_OLD, "true");
		SubmitButton createTestUser = new SubmitButton("Create Test User", PARAM_NAME_CREATE_OLD, "true");
		table.add(show,1,row);
		table.add(fix,1,row);
		table.add(createTestUser,1,row);
		row++;
		CheckBox requireGroupCreation = new CheckBox("req_grp_crtn","true");
		table.add(requireGroupCreation,1,row);
		table.add("User group creation required (else user id without group created shown in log) ",1,row++);
		CheckBox moveGroups = new CheckBox("reg_mv_grp", "true");
		table.add(moveGroups, 1, row);
		table.add("Move group that are in the way of ic_user_representative groups (relations will not be lost)", 1, row++);
		
		BusyBar busyBar = new BusyBar("fix_busy");
		busyBar.addBusyObject(fix);
		busyBar.addBusyObject(show);
		busyBar.addBusyObject(createTestUser);
		table.add(busyBar,1,row++);
		table.add("DEFAULT SQL: "+getDefaultUserSelectSQL(),1,row++);
		table.add("WARNING: Only select fields  \"ic_user_id, first_name,middle_name,last_name,user_representative\" in same order",1,row++);
		form.add(table);
		add(form);
		if(iwc.isParameterSet(PARAM_NAME_TRANSFORM)){
			runFix(iwc.getParameter("user_select_sql"),iwc.isParameterSet("req_grp_crtn"), iwc.isParameterSet("reg_mv_grp"));
			showUsers(sql);
		}
		if(iwc.isParameterSet(PARAM_NAME_SHOW_OLD)){
			showUsers(sql);
		}
		if(iwc.isParameterSet(PARAM_NAME_CREATE_OLD)) {
			createOldUser(iwc);
			showUsers(sql);
		}
	}
	
	public void createOldUser(IWContext iwc) {
		Object objUserSysAtt = iwc.getApplicationSettings().getProperty("IW_USER_SYSTEM");
		boolean isOldSystemUsed = objUserSysAtt != null && objUserSysAtt.toString().equals("OLD");
		if(!isOldSystemUsed) {
			addText("Creating an old user can only be done if the application property \"IW_USER_SYSTEM\" is set to \"OLD\"");
			return;
		}
		UserBusiness userBiz = UserBusiness.getInstance();
		com.idega.core.user.data.User user = null;
		try {
			user = userBiz.insertUser("testuserfn", "testusermn", "testuserln", "testuserdn", "testuser for userTransformer", new Integer(0), null, new Integer(-1));
		} catch(Exception e) {
			// try using hardcoded id for group that exists
			try {
				user = userBiz.insertUser("testuserfn", "testusermn", "testuserln", "testuserdn", "testuser for userTransformer", new Integer(0), null, new Integer(24));
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		if(user!=null) {
			addText("Added user \"" + user.getDisplayName() + "\"");
		}
	}
	
	public String getDefaultUserSelectSQL(){
		return "select ic_user_id, first_name,middle_name,last_name,user_representative from ic_user u where user_representative is not null and user_representative in (select ic_group_id from ic_group where group_type not  like 'permission'  ) and ic_user_id != user_representative";
	}
	
	public void showUsers(String userSelectSQL)throws Exception{
		Table table = new Table();
		String userSQL = userSelectSQL!=null?userSelectSQL:getDefaultUserSelectSQL();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		try{
			conn = com.idega.util.database.ConnectionBroker.getConnection();
			stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(userSQL);
			table.add("UserID",1,1);
			table.add("FirstName",2,1);
			table.add("MiddleName",3,1);
			table.add("LastName",4,1);
			table.add("UserRepresentative",5,1);
			int row = 2;
			while(rs.next()){
				table.add(rs.getString(1),1,row);
				table.add(rs.getString(2),2,row);
				table.add(rs.getString(3),3,row);
				table.add(rs.getString(4),4,row);
				table.add( rs.getString(5),5,row++);
			}
			rs.close();
		}
		finally{
			if (stmt != null) {
                try {
					stmt.close();
				} catch (java.sql.SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }	
			if (conn != null) {
				com.idega.util.database.ConnectionBroker.freeConnection(conn);
            }
		
		}
		table.setHorizontalZebraColored("#F2e1a9","FFFFFF");
		table.setRowColor(1,"#C38536");
		add(table);
	}

	
	public void runFix(String userSelectSql,boolean requireGroups, boolean moveGroups){
		// get users connected to given group id
		// delete from ic_group_tree ids related to given group id
		// remove relation in ic_user 
		// delete from ic_group
		// creeate new ic_groups for users
		DatastoreInterface datastoreInterface = DatastoreInterface.getInstance();
		boolean sap = (datastoreInterface instanceof SapDBDatastoreInterface);

		String userSQL = userSelectSql!=null?userSelectSql:getDefaultUserSelectSQL();
		String updateUserGroupSQL = "update ic_user set user_representative=null where ic_user_id = ?";
		String selectRelatedGroupsSQL = "select ic_group_id from ic_group_tree where child_ic_group_id = ?";
		String deleteUserGroupRelationSQL = " delete from ic_group_tree where child_ic_group_id = ?";
		String deleteUserGroupRelationSQL2 = " delete from ic_group_relation where related_ic_group_id = ?";
		String deleteUserGroupSQL = "delete from ic_group where ic_group_id = ?";
		String insertGroupSQL 	= "insert into ic_group (ic_group_id,group_type,name,extra_info) values (?,'ic_user_representative',?,'Fixed "+df.format(new Date())+"')";
		String insertGroupRelationSQL = "insert into ic_group_relation(IC_GROUP_ID,RELATIONSHIP_TYPE,INITIATION_DATE ,RELATED_IC_GROUP_ID ,GROUP_RELATION_STATUS ,INIT_MODIFICATION_DATE) values (?,'GROUP_PARENT', '"+com.idega.util.IWTimestamp.RightNow().toSQLString()+"',?,'ST_ACTIVE','"+com.idega.util.IWTimestamp.RightNow().toSQLString()+"')";
		if (sap) {
			insertGroupRelationSQL = "insert into ic_group_relation(IC_GROUP_ID,RELATIONSHIP_TYPE,INITIATION_DATE ,RELATED_IC_GROUP_ID ,GROUP_RELATION_STATUS ,INIT_MODIFICATION_DATE,IC_GROUP_RELATION_ID) values (?,'GROUP_PARENT', '"+com.idega.util.IWTimestamp.RightNow().toSQLString()+"',?,'ST_ACTIVE','"+com.idega.util.IWTimestamp.RightNow().toSQLString()+"', ?)";
		}
		String updateGroupMetadataSQL = "update ic_group_ic_metadata set ic_group_id = ? where ic_group_id = ?";
		String updateGroupProtocolSQL = "update ic_group_protocol set ic_group_id = ? where ic_group_id = ?";
		String updateGroupNewworkSQL = "update ic_group_network set ic_group_id = ? where ic_group_id = ?";
		String updateGroupAddressSQL = "update ic_group_address set ic_group_id = ? where ic_group_id = ?";
		String updateGroupEmailSQL = "update ic_group_email set ic_group_id = ? where ic_group_id = ?";
		String updateGroupPhoneSQL = "update ic_group_phone set ic_group_id = ? where ic_group_id = ?";
		String updateGroupPermissionSQL = "update ic_permission set group_id = ? where group_id = ?";
		String updateGroupPermissionSQL2 = "update ic_permission set PERMISSION_CONTEXT_VALUE = ? where PERMISSION_CONTEXT_VALUE = ? and PERMISSION_CONTEXT_TYPE = '"+AccessController.CATEGORY_STRING_GROUP_ID+"'";

		String groupRelationNextValSQL = "select ic_group_relation_seq.NEXTVAL AS NEXTID FROM DUAL";

		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			t.begin();
			conn = com.idega.util.database.ConnectionBroker.getConnection();
			stmt = conn.createStatement();
			java.sql.PreparedStatement updateUserGroupStatement = conn.prepareStatement(updateUserGroupSQL);
			java.sql.PreparedStatement selectRelatedGroupsStatement = conn.prepareStatement(selectRelatedGroupsSQL);
			java.sql.PreparedStatement deleteUserGroupRelation = conn.prepareStatement(deleteUserGroupRelationSQL);
			java.sql.PreparedStatement deleteUserGroupRelation2 = conn.prepareStatement(deleteUserGroupRelationSQL2);
			java.sql.PreparedStatement deleteUserGroup = conn.prepareStatement(deleteUserGroupSQL);
			java.sql.PreparedStatement insertGroupStatement = conn.prepareStatement(insertGroupSQL);
			java.sql.PreparedStatement insertGroupRelationStatement = conn.prepareStatement(insertGroupRelationSQL);
			java.sql.PreparedStatement checkGroupExistance = conn.prepareStatement("select ic_group_id from ic_group where ic_group_id = ? and group_type <> 'ic_user_representative'");
			java.sql.PreparedStatement checkRepresentativeGroupExistance = conn.prepareStatement("select ic_group_id from ic_group where ic_group_id = ? and group_type = 'ic_user_representative'");
			java.sql.PreparedStatement groupRelationNextVal = null;
			if (sap) {
				groupRelationNextVal = conn.prepareStatement(groupRelationNextValSQL);
			}
			
			java.sql.PreparedStatement updateGroupMetadata = conn.prepareStatement(updateGroupMetadataSQL);
			java.sql.PreparedStatement updateGroupProtocol = conn.prepareStatement(updateGroupProtocolSQL);
			java.sql.PreparedStatement updateGroupNetwork = conn.prepareStatement(updateGroupNewworkSQL);
			java.sql.PreparedStatement updateGroupAddress = conn.prepareStatement(updateGroupAddressSQL);
			java.sql.PreparedStatement updateGroupEmail = conn.prepareStatement(updateGroupEmailSQL);
			java.sql.PreparedStatement updateGroupPhone = conn.prepareStatement(updateGroupPhoneSQL);
			java.sql.PreparedStatement updateGroupPermission = conn.prepareStatement(updateGroupPermissionSQL);
			java.sql.PreparedStatement updateGroupPermission2 = conn.prepareStatement(updateGroupPermissionSQL2);
			
			java.sql.ResultSet rs = stmt.executeQuery(userSQL);
			java.sql.ResultSet groupRS;
			java.util.ArrayList groupIds;
			java.util.ArrayList users = new java.util.ArrayList();
			GroupHome groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			Group group;
			Group newGroup;

			java.util.HashMap groupMap = new java.util.HashMap();
			//int i = 0;
			System.out.println("Starting olduser->newuser transformation");
			while(rs.next()){
				String[] userinfo = new String[5];
				userinfo[0] = rs.getString(1);
				userinfo[1] = rs.getString(2);
				userinfo[2] = rs.getString(3);
				userinfo[3] = rs.getString(4);
				userinfo[4] = rs.getString(5);
			
				System.out.println("handling user "+userinfo[0]);
				System.out.print("getting related groups for group"+userinfo[4]);
				selectRelatedGroupsStatement.setString(1,userinfo[4]);
				groupRS = selectRelatedGroupsStatement.executeQuery();
				groupIds = new java.util.ArrayList(2);
				while(groupRS.next()){
					String groupid = groupRS.getString(1);
					if(groupid!=null)
						groupIds.add(groupid);
					System.out.print(" "+groupid);
					
				}
				System.out.println();
				groupMap.put(userinfo[0],groupIds);
				groupRS.close();
				System.out.println("delete user relations (ic_group_tree) whith group_id "+userinfo[4]);
				deleteUserGroupRelation.setString(1,userinfo[4]);
				deleteUserGroupRelation.execute();
				
				
				System.out.println("delete user relations (ic_group_relation) whith group_id "+userinfo[4]);
				deleteUserGroupRelation2.setString(1,userinfo[4]);
				deleteUserGroupRelation2.execute();
				
				System.out.println("Update ic_user set user_representative as null where user id is "+userinfo[0]);
				updateUserGroupStatement.setString(1,userinfo[0]);
				updateUserGroupStatement.executeUpdate();
				users.add(userinfo);
				
				System.out.println("delete user group (ic_group) whith group_id "+userinfo[4]);
				deleteUserGroup.setString(1,userinfo[4]);
				deleteUserGroup.execute();
				
				
			}
			
			// Create user_representative groups and recreate relations
			
			for (java.util.Iterator iter = users.iterator(); iter.hasNext();) {
				String[] userinfo = (String[]) iter.next();
				
				String userID = userinfo[0];
				String firstName = userinfo[1];
				//String middleName = userinfo[2];
				String lastName = userinfo[3];
				//String userGroupID = userinfo[4];
				
				System.out.println("Checking ic_group with id "+userID);
				
				checkGroupExistance.setString(1,userID);
				checkRepresentativeGroupExistance.setString(1, userID);
				
				rs = checkGroupExistance.executeQuery();
				groupRS = checkRepresentativeGroupExistance.executeQuery();
				
				if (groupRS.next()) {
					System.out.println("User "+userID+" has a ic_user_representative group");
				}
				// print user id to log if user groups not required
				else if(!requireGroups && rs.next()){
					// Group found that is not a ic_user_representative
					
					if (moveGroups) {
						group = groupHome.findByPrimaryKey(new Integer(userID));
						System.out.println("User "+userID+" has a wrong kind of group : "+group.getGroupType()+", MOVING");
						newGroup = groupHome.create();
						newGroup.setAbbrevation(group.getAbbrevation());
						newGroup.setAliasID(group.getAliasID());
						newGroup.setCreated(IWTimestamp.RightNow().getTimestamp());
						newGroup.setDescription(group.getDescription());
						newGroup.setExtraInfo(group.getExtraInfo());
						newGroup.setGroupType(group.getGroupType());
						newGroup.setHomeFolderID(group.getHomeFolderID());
						newGroup.setName(group.getName());
						newGroup.setPermissionControllingGroupID(group.getPermissionControllingGroupID());
						newGroup.setShortName(group.getShortName());
						newGroup.setUniqueId(group.getUniqueId());
						newGroup.store();
						
						String newGroupPK = newGroup.getPrimaryKey().toString();
						String oldGroupPK = group.getPrimaryKey().toString();
						
						updateGroupMetadata.setString(1, newGroupPK);
						updateGroupMetadata.setString(2, oldGroupPK);
						updateGroupMetadata.execute();
						
						updateGroupProtocol.setString(1, newGroupPK);
						updateGroupProtocol.setString(2, oldGroupPK);
						updateGroupProtocol.execute();
						
						updateGroupNetwork.setString(1, newGroupPK);
						updateGroupNetwork.setString(2, oldGroupPK);
						updateGroupNetwork.execute();
						
						updateGroupAddress.setString(1, newGroupPK);
						updateGroupAddress.setString(2, oldGroupPK);
						updateGroupAddress.execute();
						
						updateGroupEmail.setString(1, newGroupPK);
						updateGroupEmail.setString(2, oldGroupPK);
						updateGroupEmail.execute();
						
						updateGroupPhone.setString(1, newGroupPK);
						updateGroupPhone.setString(2, oldGroupPK);
						updateGroupPhone.execute();
	
						updateGroupPermission.setString(1, newGroupPK);
						updateGroupPermission.setString(2, oldGroupPK);
						updateGroupPermission.execute();

						updateGroupPermission2.setString(1, newGroupPK);
						updateGroupPermission2.setString(2, oldGroupPK);
						updateGroupPermission2.execute();
						
						group.setGroupType("ic_user_representative");
						group.setName(firstName+" "+lastName);
						group.setExtraInfo("UserTransformer, old group is now id = "+newGroupPK+", "+df.format(new Date()));
						group.store();
						
						ArrayList list = (ArrayList) groupMap.get(userID);
						ArrayList newList = new ArrayList();
						if (list != null) {
							Iterator itera = list.iterator();
							String gID;
							while (itera.hasNext()) {
								gID = (String) itera.next();
								if (gID.equals(oldGroupPK)) {
									newList.add(newGroupPK);
								} else {
									newList.add(gID);
								}
							}
						}
						
						groupMap.put(userID, newList);
						
						userRelations(sap, insertGroupRelationStatement, groupRelationNextVal, groupMap, userID);
					} else {
						System.out.println("User "+userID+" has a wrong kind of group");
					}
				}
				else{
					System.out.println("Insert into ic_group with id "+ userID );
					insertGroupStatement.setString(1,userID);
					insertGroupStatement.setString(2,"'"+firstName + " "+ lastName+"'");
					insertGroupStatement.execute();
				
					userRelations(sap, insertGroupRelationStatement, groupRelationNextVal, groupMap, userID);
				}
			
			}
			rs.close();
		t.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				t.rollback();
				Text rollBack = new Text("ROLLBACK: No data was changed (see output log)");
				rollBack.setFontColor("#FF0000");
				add(rollBack);
			}
			catch (javax.transaction.SystemException ex) {
				ex.printStackTrace();
			}
		}
		finally{
			if (stmt != null) {
                try {
					stmt.close();
				} catch (java.sql.SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }	
			if (conn != null) {
				com.idega.util.database.ConnectionBroker.freeConnection(conn);
            }
		
		}
		
	}

	/**
	 * @param sap
	 * @param insertGroupRelationStatement
	 * @param groupRelationNextVal
	 * @param rs
	 * @param groupMap
	 * @param userID
	 * @throws SQLException
	 */
	private void userRelations(boolean sap, java.sql.PreparedStatement insertGroupRelationStatement, java.sql.PreparedStatement groupRelationNextVal, java.util.HashMap groupMap, String userID) throws SQLException {
		int nextGroupRelationID;
		if(groupMap.containsKey(userID)){
			System.out.println("Insert into ic_group_relation for child group "+userID);
			java.util.ArrayList groupIDs = (java.util.ArrayList) groupMap.get(userID);
			for (int j = 0; j < groupIDs.size(); j++) {
				if (sap) {
					java.sql.ResultSet groupIDrs = groupRelationNextVal.executeQuery();
					if (groupIDrs != null && groupIDrs.next()) {
						nextGroupRelationID = groupIDrs.getInt("NEXTID");
						insertGroupRelationStatement.setString(3, Integer.toString( nextGroupRelationID));
					}
					groupIDrs.close();
				}
				insertGroupRelationStatement.setString(1,(String)groupIDs.get(j));
				insertGroupRelationStatement.setString(2,userID);
				insertGroupRelationStatement.execute();
			}
			
			
		}
	}



}