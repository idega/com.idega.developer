/*
 * $Id: SQLSessionConnection.java,v 1.2 2006/04/09 11:53:57 laddi Exp $
 * Created on May 4, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.development.business;

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import com.idega.util.database.ConnectionBroker;


public class SQLSessionConnection implements HttpSessionBindingListener {

	private Connection connection;
	
	public SQLSessionConnection() {
		this.connection = ConnectionBroker.getConnection();
		try {
			this.connection.setAutoCommit(false);
		}
		catch (SQLException sql) {
			sql.printStackTrace(System.err);
		}
		System.out.println("[SQLSessionConnection] Creating new connection.");
	}
	
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		if (this.connection != null) {
			try {
				this.connection.rollback();
				this.connection.setAutoCommit(true);
			}
			catch (SQLException sql) {
				sql.printStackTrace(System.err);
			}
			ConnectionBroker.freeConnection(this.connection);
		}
		System.out.println("[SQLSessionConnection] Connection freed successfully.");
	}

	public Connection getConnection() {
		return this.connection;
	}
}