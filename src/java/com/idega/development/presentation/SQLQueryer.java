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
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.FramePane;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/
public class SQLQueryer extends Block {
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";
	private static String PARAM_QUERY = "sql_qry_str";
	private static String PARAM_NUM_RECORDS = "sql_num_rec";
	
	private FramePane queryPane;
	private FramePane resultsPane;
	private String query;
	private boolean displayForm = true;
	private String resultName = "Result";
	private int numberOfViewedResults = 100;
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


		resultsPane = new FramePane(resultName);
		/**
		 * @todo: Improve security check
		 */
		if (iwc.isLoggedOn()) {
			
			String queryString = iwc.getParameter(PARAM_QUERY);
			try{
				numberOfViewedResults=Integer.parseInt(iwc.getParameter(PARAM_NUM_RECORDS));
			}
			catch(NumberFormatException nfe){
			}
			if (displayForm) {
				queryPane = new FramePane("Query");
				super.add(queryPane);
				Form form = new Form();
				form.setTarget(IWDeveloper.frameName);
				queryPane.add(form);
				TextArea input = new TextArea(PARAM_QUERY);
				input.setColumns(60);
				input.setRows(5);
				if (queryString != null) {
					input.setContent(queryString);
				}
				Table innerTable = new Table(2, 2);
				form.add(innerTable);
				innerTable.add(input, 1, 1);
				innerTable.add(new SubmitButton("Execute"), 1, 2);
				innerTable.add("Max. number of results:",2,1);
				TextInput maxNumInput = new TextInput(PARAM_NUM_RECORDS);
				maxNumInput.setValue(numberOfViewedResults);
				innerTable.add(Text.getBreak(),2,1);
				innerTable.add(maxNumInput,2,1);
			}
			Connection conn = getConnection(iwc);
			if (queryString == null)
				queryString = query;
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
					Table table = new Table();
					table.setColor("white");
					resultsPane.add(table);
					Statement stmt = conn.createStatement();
					if (queryString.trim().toLowerCase().startsWith("select") ) {
						ResultSet rs = stmt.executeQuery(queryString);
						ResultSetMetaData rsMeta = rs.getMetaData();
						// Get the N of Cols in the ResultSet
						int noCols = rsMeta.getColumnCount();
						//out.println("<tr>");
						int y = 1;
						int x = 1;
						for (int c = 1; c <= noCols; c++) {
							String el = rsMeta.getColumnLabel(c);
							//out.println("<th> " + el + " </th>");
							table.add(el, x, y);
							x++;
						}
						//out.println("</tr>");
						y++;
						table.setRowColor(1, "#D0D0D0");
						int counter=0;
						while (rs.next()&&(counter<numberOfViewedResults)) {
							//out.println("<tr>");
							x = 1;
							for (int c = 1; c <= noCols; c++) {
								String el = rs.getString(c);
								table.add(el, x, y);
								x++;
								//out.println("<td> " + el + " </td>");
							}
							counter++;
							y++;
							//out.println("</tr>");
						}
					}
					else if (queryString.trim().toLowerCase().startsWith("commit") ) {
						resultsPane.add("AutoCommit is on");
					}
					else{
						int i = stmt.executeUpdate(queryString);
						//if (i>0){
						resultsPane.add(i + " rows altered");
						//}
						//else{
						//}
					}
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
	
	
	protected Connection getConnection(IWContext iwc){
		return getConnection();
	}
	
	protected void freeConnection(IWContext iwc,Connection conn){
		this.freeConnection(conn);
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
