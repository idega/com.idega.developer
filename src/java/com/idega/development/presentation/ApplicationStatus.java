package com.idega.development.presentation;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.IWTimestamp;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ApplicationStatus extends Block {

	private static final String RESTART_PARAMETER = "iw_app_re";

	public ApplicationStatus() {
		// empty
	}

	public void main(IWContext iwc) throws Exception {
		ICApplicationBindingBusiness applicationBindingBusiness = getApplicationBindingBusiness(iwc);
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		Table table = new Table(3, 8);
		table.setAlignment(3, 1, "right");
		form.add(table);

		SubmitButton restart = new SubmitButton("Restart", RESTART_PARAMETER, "true");
		table.add(IWDeveloper.getText("Restart application"), 1, 1);
		table.add(restart, 3, 1);

		// Adding some fancy stuff :.

		String shutdown = applicationBindingBusiness.get("last_shutdown");
		String startup = applicationBindingBusiness.get("last_startup");
		String reboot = applicationBindingBusiness.get("last_restart");
		IWTimestamp start = null, shut = null, rest = null;
		if (shutdown != null && !shutdown.equals(""))
			shut = new IWTimestamp(shutdown);
		if (startup != null && !startup.equals(""))
			start = new IWTimestamp(startup);
		if (reboot != null && !reboot.equals(""))
			rest = new IWTimestamp(reboot);

		table.add(IWDeveloper.getText("Last startup"), 1, 3);
		if (shut != null)
			table.add(IWDeveloper.getText(shut.toSQLString()), 3, 3);
		else
			table.add(IWDeveloper.getText("Unknown"), 3, 3);

		table.add(IWDeveloper.getText("Last shutdown"), 1, 4);
		if (start != null)
			table.add(IWDeveloper.getText(start.toSQLString()), 3, 4);
		else
			table.add(IWDeveloper.getText("Unknown"), 3, 4);

		table.add(IWDeveloper.getText("Last restart"), 1, 5);
		if (start != null)
			table.add(IWDeveloper.getText(start.toSQLString()), 3, 5);
		else
			table.add(IWDeveloper.getText("Unknown"), 3, 5);

		table.add(IWDeveloper.getText("Uptime"), 1, 7);
		IWTimestamp now = IWTimestamp.RightNow();
		int minutes = 0, maxmin = 0;
		String MaxMinutes = applicationBindingBusiness.get("max_minutes");
		if (MaxMinutes != null && !MaxMinutes.equals("")) {
			maxmin = Integer.parseInt(MaxMinutes);
		}

		if (rest != null) {
			minutes = IWTimestamp.getMinutesBetween(rest, now);
			table.add(IWDeveloper.getText(minutes + " Minutes"), 3, 7);
		}
		else if (shut != null) {
			minutes = IWTimestamp.getMinutesBetween(shut, now);
			table.add(IWDeveloper.getText(minutes + " Minutes"), 3, 7);
		}
		else
			table.add(IWDeveloper.getText("Unknown"), 3, 7);

		if (minutes > maxmin) {
			maxmin = minutes;
			applicationBindingBusiness.put("max_minutes", Integer.toString(maxmin));
		}

		table.add(IWDeveloper.getText("Max. uptime"), 1, 8);
		if (start != null)
			table.add(IWDeveloper.getText(maxmin + " Minutes"), 3, 8);

		doBusiness(iwc, applicationBindingBusiness);
	}

	private void doBusiness(IWContext iwc, ICApplicationBindingBusiness applicationBindingBusiness) throws Exception {
		String check = iwc.getParameter(RESTART_PARAMETER);
		if (check != null) {
			add(IWDeveloper.getText("Done Restarting!"));
			applicationBindingBusiness.put("last_restart", com.idega.util.IWTimestamp.RightNow().toString());
			iwc.getIWMainApplication().restartApplication();
		}
	}

	private ICApplicationBindingBusiness getApplicationBindingBusiness(IWApplicationContext iwac) {
		try {
			return (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
		}
		catch (IBOLookupException ibe) {
			throw new IBORuntimeException(ibe);
		}
	}
}
