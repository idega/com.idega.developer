//idega 2001 - Tryggvi Larusson
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.development.presentation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.FramePane;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.SQLDataDumper;
import com.idega.util.database.ConnectionBroker;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/
public class SQLQueryer extends Block {
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";
	private static String PARAM_QUERY = "sql_qry_str";
	private static String PARAM_NUM_RECORDS = "sql_num_rec";
	private static String PARAM_QUERY_NAME = "sql_hist_qry_name";
	private static String HISTORY_QUERIES = "sql_hist_queries";
	private static String AREA_COLS = "area_cols";
	private static String AREA_ROWS = "area_rows";
	private static String DUMP_FILE = "dump_file";
	private static String DUMP_TYPE = "dump_type";
	
	private FramePane queryPane;
	private FramePane resultsPane;
	private String query;
	private boolean displayForm = true;
	private String resultName = "Result";
	private int numberOfViewedResults = 100;
	private Map historyQueries = null;
	private String historyQueryName = null;
	private String dumpFileName = null;
	private Integer dumpFileType = null;
	
	private int aCols = 70,aRows = 6;
	
	public SQLQueryer() {
	}
	//public void add(PresentationObject obj) {
	//	resultsPane.add(obj);
	//}
	public void setWidth(int width) {
		if (queryPane != null)
			queryPane.setWidth(width);
		if (resultsPane != null)
			resultsPane.setWidth(width);
	}
	public void setSQLQuery(String query) {
		this.query = query;
		this.displayForm = false;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	public void main(IWContext iwc) throws Exception {
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");

		resultsPane = new FramePane(resultName);
		/**
		 * @todo: Improve security check
		 */
		if (iwc.isLoggedOn()) {
			
			String queryString = iwc.getParameter(PARAM_QUERY);
			if(iwc.isParameterSet("clearhist")){
				iwc.removeApplicationAttribute(HISTORY_QUERIES);
				queryString = null;
			}
			historyQueryName = iwc.getParameter(PARAM_QUERY_NAME);
			historyQueries = (Map) iwc.getApplicationAttribute(HISTORY_QUERIES);
			if(historyQueries==null)
				historyQueries = new HashMap();
			
			if(historyQueryName!=null && !"".equals(historyQueryName) ){
				historyQueries.put(historyQueryName,queryString);
				iwc.setApplicationAttribute(HISTORY_QUERIES,historyQueries);
			}
			// just adding  query to history list
			if(iwc.isParameterSet("to_history")){
				queryString = null;
			}
			
			if(iwc.isParameterSet(DUMP_FILE))
				dumpFileName = iwc.getParameter(DUMP_FILE);
			if(iwc.isParameterSet(DUMP_TYPE)){
				try {
					dumpFileType = Integer.valueOf(iwc.getParameter(DUMP_TYPE));
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
			try{
				numberOfViewedResults=Integer.parseInt(iwc.getParameter(PARAM_NUM_RECORDS));
			}
			catch(NumberFormatException nfe){
			}
			if (queryString == null && query!=null)
					queryString = query;
					
			if (displayForm) {
				queryPane = new FramePane("Query");
				super.add(queryPane);
				Form form = new Form();
				form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
				//form.setTarget(IWDeveloper.frameName);
				queryPane.add(form);
				TextArea input = new TextArea(PARAM_QUERY);
				input.setColumns(aCols);
				input.setRows(aRows);
				if (queryString != null) {
					input.setContent(queryString);
				}
				TextInput areaRows = new TextInput("area_rows");
				TextInput areaCols = new TextInput("area_cols");
				areaRows.setContent(String.valueOf(aRows));
				areaCols.setContent(String.valueOf(aCols));
 				areaRows.setLength(3);
 				areaCols.setLength(3);
				areaRows.keepStatusOnAction();
				areaCols.keepStatusOnAction();
				areaRows.setOnChange("this.form."+PARAM_QUERY+".rows = this.value");
				areaCols.setOnChange("this.form."+PARAM_QUERY+".cols = this.value");
				Table innerTable = new Table(3, 4);
				form.add(innerTable);
				
				innerTable.add(new Text("Size:"),3,1);
				innerTable.add(areaRows,3,1);
				innerTable.add(new Text("x"),3,1);
				innerTable.add(areaCols,3,1);
				
				DropdownMenu drp = getSessionQueryDrop();
				if(drp!=null){
					innerTable.mergeCells(1,1,2,1);
					innerTable.add(drp,1,1);
					innerTable.setAlignment(3,1,innerTable.HORIZONTAL_ALIGN_RIGHT);
					innerTable.add(new SubmitButton("clearhist","Clear history"),3,1);
				}
				
					
				innerTable.mergeCells(1,2,3,2);
				innerTable.add(input, 1, 2);
				innerTable.add("Max. number of results:",1,3);
				TextInput maxNumInput = new TextInput(PARAM_NUM_RECORDS);
				maxNumInput.setLength(6);
				maxNumInput.setValue(numberOfViewedResults);
				//innerTable.add(Text.getBreak(),2,1);
				
				innerTable.add(maxNumInput,1,3);
				innerTable.add("Query history name",1,3);
				TextInput sessQueryNameInput = new TextInput(PARAM_QUERY_NAME);
				innerTable.add(sessQueryNameInput,1,3);
				//innerTable.setAlignment(1,3,innerTable.HORIZONTAL_ALIGN_RIGHT);
				innerTable.add("Only to history:",1,3);
				innerTable.add(new CheckBox("to_history","true"),1,3);
				innerTable.add(new SubmitButton("Execute"), 3, 3);
				innerTable.mergeCells(1,3,2,3);
				
				innerTable.add("Dump file",1,4);
				TextInput dumpFileNameInput = new TextInput(DUMP_FILE);
				DropdownMenu dumpTypes = new DropdownMenu(DUMP_TYPE);
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_CSV,"CSV");
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_SQL_INSERT,"SQL inserts");
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_SQL_UPDATE,"SQL updates");
				dumpTypes.keepStatusOnAction(true);
				innerTable.add(dumpFileNameInput,1,4);
				innerTable.add(dumpTypes,1,4);
				
				if(dumpFileName!=null && dumpFileType!=null && queryString!=null){
					SQLDataDumper dumper = new SQLDataDumper();
					dumper.setQuery(queryString);
					dumper.setDumpFile(dumpFileName);
					dumper.setType(dumpFileType.intValue());
					String virtualFolderPath = iwc.getIWMainApplication().getCacheDirectoryURI();
					dumper.setDumpFolder(iwc.getIWMainApplication().getRealPath(virtualFolderPath));
					java.io.File file = dumper.dump();
					//innerTable.add(file.getAbsolutePath(),1,4);
					//String fileURI = virtualFolderPath+"/"+file.getName();
					//Link fileLink =new Link(file.getName(),fileURI);
					//fileLink.setTarget(Link.TARGET_NEW_WINDOW);
					DownloadLink fileLink = new DownloadLink(file.getName(),file.getAbsolutePath());
					innerTable.add(Text.getNonBrakingSpace(),1,4);
					innerTable.add(fileLink,1,4);
					
					
				}
			}
			Connection conn = getConnection(iwc);
			
			try {
				if (queryString != null) {
					super.add(resultsPane);
					if (displayForm) {
						resultsPane.add("Your query was:");
						resultsPane.add(Text.getBreak());
						Text text = new Text(queryString);
						text.setBold();
						resultsPane.add(text);
						resultsPane.addBreak();
					}
					
					Statement stmt = conn.createStatement();
					StringTokenizer tokener = new StringTokenizer(queryString,";");
					int alterCount = 0;
					while(tokener.hasMoreTokens()){
					    queryString = tokener.nextToken();
					    if(!"".equals(queryString)){
							if (queryString.trim().toLowerCase().startsWith("select") ) {
							    Table table = new Table();
								table.setColor("white");
								resultsPane.add(table);
								ResultSet rs = stmt.executeQuery(queryString);
								ResultSetMetaData rsMeta = rs.getMetaData();
								// Get the N of Cols in the ResultSet
								int noCols = rsMeta.getColumnCount();
								//out.println("<tr>");
								int row = 1;
								int col = 1;
								for (int c = 1; c <= noCols; c++) {
									String el = rsMeta.getColumnLabel(c);
									//out.println("<th> " + el + " </th>");
									table.add(el, col, row);
									col++;
								}
								//out.println("</tr>");
								row++;
								table.setRowColor(1, "#D0D0D0");
								int counter=0;
								while (rs.next()&&(counter<numberOfViewedResults)) {
									//out.println("<tr>");
									col = 1;
									for (int c = 1; c <= noCols; c++) {
										String el = rs.getString(c);
										table.add(el, col, row);
										col++;
										//out.println("<td> " + el + " </td>");
									}
									counter++;
									row++;
									//out.println("</tr>");
								}
							}
							else if (queryString.trim().toLowerCase().startsWith("commit") ) {
								resultsPane.add("AutoCommit is on");
							}
							else{
								int i = stmt.executeUpdate(queryString);
								//if (i>0){
								//resultsPane.add(i + " rows altered");
								alterCount +=i;
								//}
								//else{
								//}
							}
					    }
					}
					if(alterCount>0)
					    resultsPane.add(alterCount + " rows altered");
					//out.println("</table>");
					
					
				} //end if querystring

			} //end of try
			catch (SQLException ex) {
				while (ex != null) {
					resultsPane.add("Message:   " + ex.getMessage());
					this.addBreak();
					resultsPane.add("SQLState:  " + ex.getSQLState());
					this.addBreak();
					resultsPane.add("ErrorCode: " + ex.getErrorCode());
					this.addBreak();
					ex = ex.getNextException();
					//out.println("");
				}
			}
			finally {
				this.freeConnection(iwc,conn);
			}
			
		}
		else {
			add("Not logged on");
		}
	}
	
	private void initSQLAreaSize(IWContext iwc){
		if(iwc.isParameterSet(AREA_COLS)){
			try {
				aCols = Integer.parseInt(iwc.getParameter(AREA_COLS));
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		if(iwc.isParameterSet(AREA_ROWS)){
			try {
				aRows = Integer.parseInt(iwc.getParameter(AREA_ROWS));
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	private DropdownMenu getSessionQueryDrop(){
		
		DropdownMenu drop = new DropdownMenu("sql_sess_qry_drp");
		drop.addMenuElement(" ","History");
		drop.addMenuElement("select * from","Select * from");
		if(historyQueries!=null && !historyQueries.isEmpty() ){
			Iterator iter = historyQueries.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				drop.addMenuElement((String)entry.getValue(),(String)entry.getKey());
			}
			drop.setOnChange("this.form."+PARAM_QUERY+".value = this.options[this.selectedIndex].value;");
		}
		return drop;
	}
	
	protected Connection getConnection(IWContext iwc){
		return getConnection();
	}
	
	protected void freeConnection(IWContext iwc,Connection conn){
		this.freeConnection(conn);
	}	
	
	public Connection getConnection()
	{
		return ConnectionBroker.getConnection();
	}
	public void freeConnection(Connection conn)
	{
		ConnectionBroker.freeConnection(conn);
	}
	
	public Object clone() {
		SQLQueryer obj = null;
		try {
			obj = (SQLQueryer) super.clone();
			if(queryPane!=null)
				obj.queryPane = (FramePane)this.queryPane.clone();
			if(queryPane!=null)
				obj.resultsPane = (FramePane)this.resultsPane.clone();
			obj.query = this.query;
			obj.displayForm = this.displayForm;
			obj.resultName = this.resultName;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}
