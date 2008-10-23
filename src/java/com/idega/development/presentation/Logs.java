package com.idega.development.presentation;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.FileUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.text.TextSoap;

/**
 * This Developer page allows you to view two logs, out and err (or just any two logs really)<br>
 * The default settings should work for tomcat but you can set the paths yourself in Application Properties.<br>
 * The param: LOG_FILE_FOLDER_PATH points to the root folder for the log files <br>
 * The param: LOG_FILE_OUT_NAME is the name of the out log file <br>
 * The param: LOG_FILE_ERROR_NAME is the name of the error log file <br>
 * If the out or error log file names contain any "/" or "\" the root folder path is ignored and the name is considered a full path<br>
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson </a>
 * @version 1.1
 */
public class Logs extends Block {

	private static final String LOG_FILE_FOLDER_PATH = "LOG_FILE_FOLDER_PATH";

	private static final String LOG_FILE_ERROR_NAME = "LOG_FILE_ERROR_NAME";

	private static final String LOG_FILE_OUT_NAME 	= "LOG_FILE_OUT_NAME";

	private static final String PARAM_VIEW_OUT_LOG 	= "iw_dev_view_out_log";

	private static final String PARAM_VIEW_ERR_LOG 	= "iw_dev_view_err_log";
	private static final String PARAM_LINES_CNT 	= "iw_dev_lines_cnt_log";
	private static final int 	DEFAULT_LINES_CNT 	= 200;
	private static final String	SYM_NEW_LINE 		= "\n";
	
	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.developer";
	
	
	private static final String defaultText = "This Developer page allows you to view two logs, out and err (or just any two logs really).\nThe default settings should work for tomcat but you can set the paths yourself in the Application Property Setter.\n\nThe param: LOG_FILE_FOLDER_PATH points to the root folder for the log files.\nThe param: LOG_FILE_OUT_NAME is the name of the out log file.\nThe param: LOG_FILE_ERROR_NAME is the name of the error log file.\n\nIf the out or error log file names contain any \"/\" or \"\\\" the root folder path is ignored and the name is considered a full path.\n\n";

	//	private static final String PARAM_CLEAR_OUT_LOG = "iw_dev_clear_out_log";
	//	private static final String PARAM_CLEAR_ERR_LOG = "iw_dev_clear_err_log";
	public Logs() {
		// empty
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		String text = iwrb.getLocalizedString("Logs.tutorial_text",defaultText);
		text = TextSoap.findAndReplace(text, "\n", "<br>");
		
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		form.maintainParameter(IWDeveloper.actionParameter);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(1, 4);
		table.setHeight(1, 2, "30");
		table.setHeight(1, 4, Table.HUNDRED_PERCENT);
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		table.setAlignment(1, 2, Table.HORIZONTAL_ALIGN_LEFT);
		table.setAlignment(1, 3, Table.HORIZONTAL_ALIGN_LEFT);
		
		table.add(text, 1, 1);
		
		form.add(table);
		SubmitButton viewOut = new SubmitButton(PARAM_VIEW_OUT_LOG, "View Out Log");
		//		SubmitButton clearOut = new SubmitButton(PARAM_CLEAR_OUT_LOG, "Clear
		// Out Log");
		SubmitButton viewErr = new SubmitButton(PARAM_VIEW_ERR_LOG, "View Error Log");
		//		SubmitButton clearErr = new SubmitButton(PARAM_CLEAR_ERR_LOG, "Clear
		// Error Log");
		table.add(viewOut, 1, 2);
		table.add(viewErr, 1, 2);
		
		TextInput lines_cnt = new TextInput(PARAM_LINES_CNT);
		lines_cnt.setContent(iwc.isParameterSet(PARAM_LINES_CNT) ? iwc.getParameter(PARAM_LINES_CNT) : String.valueOf(DEFAULT_LINES_CNT));
		
		Label lines_cnt_label = new Label("Line count:", lines_cnt);
		lines_cnt_label.setStyleAttribute("padding", "0 0 0 15px");
		
		table.add(lines_cnt_label, 1, 2);
		table.add(lines_cnt, 1, 2);
		
		//		table.add(clearOut, 1, 1);
		//		table.add(clearErr, 1, 1);
		if (iwc.isParameterSet(PARAM_VIEW_OUT_LOG) || iwc.isParameterSet(PARAM_VIEW_ERR_LOG)) {
			processBusiness(iwc, table);
		}
	}

	private void processBusiness(IWContext iwc, Table table) throws Exception {
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		//this works only for the default tomcat setup but you can change the
		// path in application properties
		table.setColor(1,3,"#dddddd");
		table.setColor(1,4,"#cecece");
		String defaultLogFolderPath = 
			new StringBuilder(System.getProperty("user.dir"))
			.append(File.separator)
			.append("..")
			.append(File.separator)
			.append("logs")
			.append(File.separator)
			.toString();
		
		String defaultLogFileName = "catalina.out";
		
		String logDir = settings.getProperty(LOG_FILE_FOLDER_PATH, defaultLogFolderPath);
		String outLogName = settings.getProperty(LOG_FILE_OUT_NAME, defaultLogFileName);
		String errLogName = settings.getProperty(LOG_FILE_ERROR_NAME, defaultLogFileName);
		
		if (iwc.isParameterSet(PARAM_VIEW_OUT_LOG)) {
			if(outLogName.indexOf("/")>0 || outLogName.indexOf("\\")>0 ){
				//it's a full path
				logDir = outLogName;
			}
			else{
				logDir = logDir + outLogName;
			}
		}
		else if (iwc.isParameterSet(PARAM_VIEW_ERR_LOG)) {
			if(outLogName.indexOf("/")>0 || outLogName.indexOf("\\")>0 ){
				//it's a full path
				logDir = errLogName;
			}
			else{
				logDir = logDir + errLogName;
			}
		}
		
		table.add(logDir,1,3);

		int lines_cnt = DEFAULT_LINES_CNT;
		
		if(iwc.isParameterSet(PARAM_LINES_CNT)) {
			
			try {
				lines_cnt = Integer.parseInt(iwc.getParameter(PARAM_LINES_CNT));
			} catch (Exception e) { }
		}
		
		List output = FileUtil.tail(new File(logDir), lines_cnt);
		
		StringBuffer output_str = new StringBuffer("<pre>");
		
		if(output != null)
			for (Iterator iter = output.iterator(); iter.hasNext();) {
				output_str.append((String)iter.next());
				output_str.append(SYM_NEW_LINE);
			}
		
		output_str.append("</pre>");
		
		table.add(output_str.toString(), 1, 4);
		
		//		else if (iwc.isParameterSet(PARAM_CLEAR_OUT_LOG)) {
		//			tomcatLogDir = tomcatLogDir + outLogName;
		//			FileUtil.delete(tomcatLogDir);
		//			FileUtil.createFile(tomcatLogDir);
		//			table.add("<b>Out log cleared!</b><br>", 1, 2);
		//			table.add("<pre>" + FileUtil.getStringFromFile(tomcatLogDir) +
		// "</pre>", 1, 2);
		//		}
		//		else if (iwc.isParameterSet(PARAM_CLEAR_ERR_LOG)) {
		//			tomcatLogDir = tomcatLogDir + errLogName;
		//			FileUtil.delete(tomcatLogDir);
		//			FileUtil.createFile(tomcatLogDir);
		//			table.add("<b>Error log cleared!</b><br>", 1, 2);
		//			table.add("<pre>" + FileUtil.getStringFromFile(tomcatLogDir) +
		// "</pre>", 1, 2);
		//		}
	}
	
	@Override
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
}