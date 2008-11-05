/*
 * $Id: SQLSessionConnection.java,v 1.3 2008/11/05 16:39:11 laddi Exp $
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
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import com.idega.util.database.ConnectionBroker;


public class SQLSessionConnection implements HttpSessionBindingListener {

	private Connection connection;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public SQLSessionConnection() {
		this.connection = ConnectionBroker.getConnection();
		try {
			this.connection.setAutoCommit(false);
		}
		catch (SQLException sql) {
			sql.printStackTrace(System.err);
		}
		logger.info("[SQLSessionConnection] Creating new connection.");
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
		logger.info("[SQLSessionConnection] Connection freed successfully.");
	}

	public Connection getConnection() {
		return this.connection;
	}
}