package com.idega.developer.notifier.presentation;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.developer.notifier.business.SystemRestartNotifier;
import com.idega.development.business.DeveloperConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.presentation.IWContext;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class SystemRestartNotification extends BasicNotification {

	@Autowired
	private SystemRestartNotifier restartNotifier;
	
	@Override
	public void present(IWContext iwc) {
		ELUtil.getInstance().autowire(this);
		
		String text = restartNotifier.getTextAndTimeLeftToRestart();
		if (StringUtil.isEmpty(text)) {
			return;
		}
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		setTitle(iwrb.getLocalizedString("system_restart.title", "ePlatform will be re-started"));
		setText(text);
		setSticky(true);
		
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
				"/dwr/interface/" + SystemRestartNotifier.DWR_OBJECT + ".js",
				getBundle(iwc).getVirtualPathWithFileNameString("javascript/RestartNotifierHelper.js")
		));
		setJavaScriptActionsAfterNotificationAdded(Arrays.asList(
				new StringBuilder("RestartNotifierHelper.startTicking(id);").toString()
		));
	}

	@Override
	public String getBundleIdentifier() {
		return DeveloperConstants.BUNDLE_IDENTIFIER;
	}
}