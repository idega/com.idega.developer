/*
 * $Id: SQLSessionConnection.java,v 1.1 2005/05/04 11:35:32 laddi Exp $
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
		connection = ConnectionBroker.getConnection();
		try {
			connection.setAutoCommit(false);
		}
		catch (SQLException sql) {
			sql.printStackTrace(System.err);
		}
		System.out.println("[SQLSessionConnection] Creating new connection.");
	}
	
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		if (connection != null) {
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			}
			catch (SQLException sql) {
				sql.printStackTrace(System.err);
			}
			ConnectionBroker.freeConnection(connection);
		}
		System.out.println("[SQLSessionConnection] Connection freed successfully.");
	}

	public Connection getConnection() {
		return connection;
	}
}