//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.development.presentation;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.presentation.text.*;
import	com.idega.presentation.*;
import	com.idega.presentation.ui.*;
import	com.idega.data.*;
import com.idega.util.text.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class SQLQueryer extends Block{

  public final static String IW_BUNDLE_IDENTIFIER="com.idega.developer";
    private static String queryParameter="SQLQUERYSTRING";
    private FramePane queryPane;
    private FramePane resultsPane;
    private String query;
    private boolean displayForm = true;
    private String resultName = "Result";


    public SQLQueryer(){
    }

    public void add(PresentationObject obj){
      resultsPane.add(obj);
    }


    public void setWidth(int width){
      if(queryPane!=null) queryPane.setWidth(width);
      if(resultsPane!=null) resultsPane.setWidth(width);
    }

    public void setSQLQuery(String query){
      this.query = query;
      this.displayForm = false;
    }

    public void setResultName(String resultName){
      this.resultName = resultName;
    }

    public void main(IWContext iwc)throws Exception{
      resultsPane = new FramePane(resultName);
      String  queryString = iwc.getParameter (queryParameter);


      if(displayForm){
	queryPane = new FramePane("Query");
	super.add(queryPane);
	Form form = new Form();
	queryPane.add(form);
	TextArea input = new TextArea(queryParameter);
	input.setWidth(50);
	input.setHeight(4);
	if(queryString!=null){
	  input.setContent(queryString);
	}
	Table innertTable =new Table(1,2);
	form.add(innertTable);
	innertTable.add(input,1,1);
	innertTable.add(new SubmitButton("Execute"),1,2);
      }


      Connection conn=getConnection();
      if(queryString == null) queryString = query;

      try {
	if (queryString != null){
	  super.add(resultsPane);
	  if( displayForm ){
	    add("Your query was:");
	    add(Text.getBreak());
	    Text text = new Text(queryString);
	    text.setBold();
	    add(text);
	    addBreak();
	  }
	  Table table = new Table();
	  table.setColor("white");
	  add(table);
	  Statement stmt = conn.createStatement();
	    if(queryString.toLowerCase().indexOf("select")==-1){
		int i = stmt.executeUpdate(queryString);
		//if (i>0){
			add(i+" rows altered");
		//}
		//else{

		//}
	    }
	    else{
		ResultSet rs = stmt.executeQuery(queryString);
		ResultSetMetaData rsMeta = rs.getMetaData();
		// Get the N of Cols in the ResultSet
		int noCols = rsMeta.getColumnCount();
		//out.println("<tr>");
		int y=1;
		int x=1;
		for (int c=1; c<=noCols; c++) {
		    String el = rsMeta.getColumnLabel(c);
		    //out.println("<th> " + el + " </th>");
		    table.add(el,x,y);
		    x++;
		}
		//out.println("</tr>");
		y++;

		table.setRowColor(1,"#D0D0D0");
		while (rs.next()) {
		    //out.println("<tr>");
		    x=1;
		    for (int c=1; c<=noCols; c++) {
			String el = rs.getString(c);
			table.add(el,x,y);
			x++;
			//out.println("<td> " + el + " </td>");

		    }
		    y++;
		    //out.println("</tr>");
		}
	    }
		//out.println("</table>");
	  }//end if querystring

	}//end of try
	catch (SQLException ex ) {
	    //out.println ( "<P><PRE>" );
	      while (ex != null) {
		  add("Message:   " + ex.getMessage ());

					      this.addBreak();
		  add("SQLState:  " + ex.getSQLState ());
					      this.addBreak();
		  add("ErrorCode: " + ex.getErrorCode ());
					      this.addBreak();
		  ex = ex.getNextException();
		  //out.println("");
	      }
	      //out.println ( "</PRE><P>" );
	}
	finally{
	  this.freeConnection(conn);
	}
	//out.println ("<hr>You can now try to retrieve something.");
	//out.println("<FORM METHOD=POST ACTION=\"/servlet/CoffeeBreakServlet\">");
	//out.println("<FORM METHOD=POST ACTION=\""+req.getRequestURI()+"\">");
		      //out.println("Query: <INPUT TYPE=TEXT SIZE=50 NAME=\"QUERYSTRING\"> ");
	//out.println("<INPUT TYPE=SUBMIT VALUE=\"GO!\">");
	//out.println("</FORM>");
	//out.println("<hr><pre>e.g.:");
	//out.println("SELECT * FROM COFFEES");
	//out.println("SELECT * FROM COFFEES WHERE PRICE > 9");
	//out.println("SELECT PRICE, COF_NAME FROM COFFEES");
	//out.println("<pre>");

	//out.println ("<hr><a href=\""+req.getRequestURI()+"\">Query again ?</a>");// | Source: <A HREF=\"/develop/servlets-ex/coffee-break/CoffeeBreakServlet.java\">CoffeeBreakServlet.java</A>");
	//out.println ( "</body></html>" );


    }

  public synchronized Object clone() {
    SQLQueryer obj = null;
    try {
      obj = (SQLQueryer)super.clone();
      obj.queryParameter = this.queryParameter;
      obj.queryPane = this.queryPane;
      obj.resultsPane = this.resultsPane;
      obj.query = this.query;
      obj.displayForm = this.displayForm;
      obj.resultName = this.resultName;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

}
