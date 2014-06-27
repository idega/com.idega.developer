// idega 2001 - Tryggvi Larusson
/*
 *
 * Copyright 2001 idega.is All Rights Reserved.
 *
 */
package com.idega.development.presentation;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.StringTokenizer;

import com.idega.development.business.SQLSessionConnection;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Legend;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.SQLDataDumper;
import com.idega.util.StringHandler;

/**
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *
 * @version 1.0
 *
 */
public class SQLQueryer extends Block {

	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";

	private static String PARAM_QUERY = "sqlQuery";
	private static String PARAM_NUM_RECORDS = "sql_num_rec";
	private static String DUMP_FILE = "dump_file";
	private static String DUMP_TYPE = "dump_type";

	private static final String SESSION_ATTRIBUTE_CONNECTION = "session_connection";

	private String query;
	private boolean displayForm = true;
	private String resultName = "Result";
	private int numberOfViewedResults = 100;
	private String dumpFileName = null;
	private Integer dumpFileType = null;

	public SQLQueryer() {
	}

	public void setWidth(int width) {
	}

	public void setSQLQuery(String query) {
		this.query = query;
		this.displayForm = false;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = this.getBundle(iwc);
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		add(topLayer);

		FieldSet querySet = new FieldSet(new Legend("Query"));
		querySet.setStyleClass("querySet");
		topLayer.add(querySet);

		/**
		 * @todo: Improve security check
		 */
		if (iwc.isLoggedOn()) {

			String queryString = iwc.getParameter(PARAM_QUERY);

			if (iwc.isParameterSet(DUMP_FILE)) {
				this.dumpFileName = iwc.getParameter(DUMP_FILE);
			}
			if (iwc.isParameterSet(DUMP_TYPE)) {
				try {
					this.dumpFileType = Integer.valueOf(iwc.getParameter(DUMP_TYPE));
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			try {
				this.numberOfViewedResults = Integer.parseInt(iwc.getParameter(PARAM_NUM_RECORDS));
			}
			catch (NumberFormatException nfe) {
			}

			if (queryString == null && this.query != null) {
				queryString = this.query;
			}

			if (this.displayForm) {
				Form form = new Form();
				form.setID("sqlQuerier");
				form.setStyleClass("developerForm");
				form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);

				querySet.add(form);

//				Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.class);
//				this.getParentPage().addJavascriptURL(web2.getCodePressScriptFilePath());

				TextArea input = new TextArea(PARAM_QUERY);
				input.setId(PARAM_QUERY);
				input.keepStatusOnAction(true);
//				enable syntax coloring!
				input.setStyleClass("codepress sql linenumbers-on");

				Layer formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				formItem.setStyleClass("bigFormItem");
				Label label = new Label("Query", input);
				formItem.add(label);
				formItem.add(input);
				form.add(formItem);

				TextInput maxNumInput = new TextInput(PARAM_NUM_RECORDS);
				maxNumInput.setLength(6);
				maxNumInput.setValue(this.numberOfViewedResults);

				formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				label = new Label("Max. number of results:", maxNumInput);
				formItem.add(label);
				formItem.add(maxNumInput);
				form.add(formItem);

				TextInput dumpFileNameInput = new TextInput(DUMP_FILE);

				formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				label = new Label("Dump file name", dumpFileNameInput);
				formItem.add(label);
				formItem.add(dumpFileNameInput);
				form.add(formItem);

				DropdownMenu dumpTypes = new DropdownMenu(DUMP_TYPE);
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_CSV, "CSV");
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_SQL_INSERT, "SQL inserts");
				dumpTypes.addMenuElement(SQLDataDumper.TYPE_SQL_UPDATE, "SQL updates");
				dumpTypes.keepStatusOnAction(true);

				formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				label = new Label("Dump file type", dumpTypes);
				formItem.add(label);
				formItem.add(dumpTypes);
				form.add(formItem);

				if (this.dumpFileName != null && this.dumpFileType != null && queryString != null) {
					SQLDataDumper dumper = new SQLDataDumper();
					dumper.setQuery(queryString);
					dumper.setDumpFile(this.dumpFileName);
					dumper.setType(this.dumpFileType.intValue());
					String virtualFolderPath = iwc.getIWMainApplication().getCacheDirectoryURI();
					dumper.setDumpFolder(iwc.getIWMainApplication().getRealPath(virtualFolderPath));
					java.io.File file = dumper.dump();

					DownloadLink fileLink = new DownloadLink(file.getName());
					fileLink.setAbsoluteFilePath(file.getAbsolutePath());

					formItem = new Layer(Layer.DIV);
					formItem.setStyleClass("formItem");
					label = new Label();
					label.setLabel("Dump file");
					formItem.add(label);
					formItem.add(fileLink);
					form.add(formItem);
				}

				Layer buttonLayer = new Layer(Layer.DIV);
				buttonLayer.setStyleClass("buttonLayer");
				form.add(buttonLayer);

				SubmitButton commit = new SubmitButton("Commit");
				//Link commit = new Link(new Span(new Text("Commit")));
				commit.setStyleClass("button");
				commit.setID("commit");
				commit.setValueOnClick(PARAM_QUERY, "commit");
				//commit.setToFormSubmit(form);

				SubmitButton rollback = new SubmitButton("Rollback");
				//Link rollback = new Link(new Span(new Text("Rollback")));
				rollback.setStyleClass("button");
				rollback.setID("rollback");
				rollback.setValueOnClick(PARAM_QUERY, "rollback");
				//rollback.setToFormSubmit(form);

				SubmitButton execute = new SubmitButton("Execute");
				execute.setStyleClass("button");
				execute.setID("execute");

				buttonLayer.add(execute);
				buttonLayer.add(commit);
				buttonLayer.add(rollback);
			}

			try {
				if (queryString != null) {
					Connection conn = getConnection(iwc);

					Statement stmt = conn.createStatement();
					StringTokenizer tokener = new StringTokenizer(queryString, ";");
					int alterCount = 0;
					while (tokener.hasMoreTokens()) {
						queryString = tokener.nextToken();
						if (!"".equals(queryString)) {
							FieldSet resultSet = new FieldSet(new Legend("Result"));
							resultSet.setStyleClass("resultSet");
							topLayer.add(resultSet);

							Text queryText = new Text(queryString);
							queryText.setStyleClass("query");

							Paragraph paragraph = new Paragraph();
							paragraph.add(new Text("Your query was:"));
							paragraph.add(new Break());
							paragraph.add(queryText);
							resultSet.add(paragraph);

							if (queryString.trim().toLowerCase().startsWith("select")) {
								Table2 table = new Table2();
								table.setCellpadding(0);
								table.setCellspacing(0);
								table.setStyleClass("developerTable");
								table.setStyleClass("ruler");
								resultSet.add(table);

								TableRowGroup group = table.createHeaderRowGroup();
								TableRow row = group.createRow();

								long time = System.currentTimeMillis();
								ResultSet rs = stmt.executeQuery(queryString);
								long queryTime = System.currentTimeMillis() - time;
								ResultSetMetaData rsMeta = rs.getMetaData();

								// Get the N of Cols in the ResultSet
								int noCols = rsMeta.getColumnCount();

								TableCell2 cell = row.createHeaderCell();
								cell.setStyleClass("firstColumn");
								cell.add(new Text(CoreConstants.HASH));

								for (int c = 1; c <= noCols; c++) {
									String el = rsMeta.getColumnLabel(c);
									int type = rsMeta.getColumnType(c);

									cell = row.createHeaderCell();
									cell.add(new Text(el));

									if (c == noCols) {
										cell.setStyleClass("lastColumn");
									}

									if (type == Types.DOUBLE || type == Types.FLOAT || type == Types.INTEGER) {
										cell.setStyleClass("number");
									}
									else {
										cell.setStyleClass("string");
									}
								}

								group = table.createBodyRowGroup();

								int counter = 0;
								while (rs.next() && (counter < this.numberOfViewedResults)) {
									row = group.createRow();

									cell = row.createCell();
									cell.setStyleClass("firstColumn");
									cell.add(new Text(String.valueOf(counter + 1)));

									for (int c = 1; c <= noCols; c++) {
										int type = rsMeta.getColumnType(c);
										String el = null;
										if (type == Types.BLOB) {
											Blob blob = rs.getBlob(c);
											if (blob == null) {
												el = CoreConstants.EMPTY;
											} else {
												InputStream stream = blob.getBinaryStream(1, (int) blob.length());
												el = StringHandler.getContentFromInputStream(stream);
												IOUtil.close(stream);
											}
										} else if (type == Types.CLOB) {
											Clob clob = rs.getClob(c);
											if (clob == null) {
												el = CoreConstants.EMPTY;
											} else {
												long length = clob.length();
												Reader chrInstream = clob.getCharacterStream();
												int intLength = (length < Integer.MAX_VALUE) ? (int) length : Integer.MAX_VALUE;
												char chrBuffer[] = new char[intLength];
												chrInstream.read(chrBuffer);
												el = new String(chrBuffer);
												IOUtil.close(chrInstream);
											}
										} else {
											el = rs.getString(c);
										}

										cell = row.createCell();
										cell.add(new Text(el));

										if (c == noCols) {
											cell.setStyleClass("lastColumn");
										}

										if (type == Types.DOUBLE || type == Types.FLOAT || type == Types.INTEGER) {
											cell.setStyleClass("number");
										}
										else {
											cell.setStyleClass("string");
										}
									}
									counter++;

									if (counter % 2 == 0) {
										row.setStyleClass("evenRow");
									}
									else {
										row.setStyleClass("oddRow");
									}
								}

								group = table.createFooterRowGroup();
								row = group.createRow();

								cell = row.createCell();
								cell.setColumnSpan(noCols + 1);
								cell.add(new Text("Query time: " + queryTime + " ms"));
							}
							else if (queryString.trim().toLowerCase().startsWith("commit")) {
								conn.commit();
								iwc.removeSessionAttribute(SESSION_ATTRIBUTE_CONNECTION);
								resultSet.add(new Text("Changes commited."));
							}
							else if (queryString.trim().toLowerCase().startsWith("rollback")) {
								conn.rollback();
								iwc.removeSessionAttribute(SESSION_ATTRIBUTE_CONNECTION);
								resultSet.add(new Text("Changes rollbacked."));
							}
							else {
								int i = stmt.executeUpdate(queryString);
								alterCount += i;
								resultSet.add(new Text(alterCount + " rows altered"));
							}
						}
					}
				} // end if querystring

			} // end of try
			catch (SQLException ex) {
				if (iwc.getIWMainApplication().getSettings().getBoolean("print_sql_querier_error", Boolean.FALSE)) {
					ex.printStackTrace();
				}
				while (ex != null) {
					add("Message:   " + ex.getMessage());
					this.addBreak();
					add("SQLState:  " + ex.getSQLState());
					this.addBreak();
					add("ErrorCode: " + ex.getErrorCode());
					this.addBreak();
					ex = ex.getNextException();
					// out.println("");
				}
			}
		}
		else {
			add("Not logged on");
		}
	}

	public Connection getConnection(IWContext iwc) {
		SQLSessionConnection conn = (SQLSessionConnection) iwc.getSessionAttribute(SESSION_ATTRIBUTE_CONNECTION);
		if (conn == null) {
			conn = new SQLSessionConnection();
			iwc.setSessionAttribute(SESSION_ATTRIBUTE_CONNECTION, conn);
		}
		return conn.getConnection();
	}

	@Override
	public Object clone() {
		SQLQueryer obj = null;
		try {
			obj = (SQLQueryer) super.clone();
			obj.query = this.query;
			obj.displayForm = this.displayForm;
			obj.resultName = this.resultName;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}