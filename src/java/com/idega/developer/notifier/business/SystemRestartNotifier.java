package com.idega.developer.notifier.business;

import java.sql.Timestamp;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.developer.notifier.presentation.SystemRestartNotification;
import com.idega.development.business.DeveloperConstants;
import com.idega.dwr.business.DWRAnnotationPersistance;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.notifier.business.BasicNotifier;
import com.idega.notifier.business.Notifier;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.type.NotificationType;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(SystemRestartNotifier.BEAN_IDENTIFIER)
@RemoteProxy(creator=SpringCreator.class, creatorParams={
	@Param(name="beanName", value=SystemRestartNotifier.BEAN_IDENTIFIER),
	@Param(name="javascript", value=SystemRestartNotifier.DWR_OBJECT)
}, name=SystemRestartNotifier.DWR_OBJECT)
public class SystemRestartNotifier extends BasicNotifier implements Notifier, DWRAnnotationPersistance {

	private static final long serialVersionUID = 6981753260280110203L;

	public static final String BEAN_IDENTIFIER = "systemRestartNotifier";
	public static final String DWR_OBJECT = "SystemRestartNotifier";
	
	private int restartIn = 30;
	private Timestamp restartInSetAt;
	
	public int getRestartIn() {
		return restartIn;
	}

	public void setRestartIn(String restartIn) {
		if (StringUtil.isEmpty(restartIn)) {
			return;
		}
		
		Integer realValue = null;
		try {
			realValue = Integer.valueOf(restartIn);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (realValue == null) {
			return;
		}
		
		this.restartIn = realValue;
		restartInSetAt = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public BasicNotification getNotification(HttpSession session) {
		if (!canShow(session)) {
			return null;
		}
		
		BasicNotification notification = new SystemRestartNotification();
		
		session.setAttribute(getNotificationKey(), String.valueOf(Boolean.TRUE));
		
		return notification;
	}
	
	@RemoteMethod
	public String getTextAndTimeLeftToRestart() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		Integer restartIn = getTimeLeftToRestart();
		if (restartIn <= 0) {
			return null;
		}
		
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(DeveloperConstants.BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		return new StringBuilder(iwrb.getLocalizedString("system_restart.text", "Due to maintenance issues ePlatform will be re-started in"))
		.append(" ").append(restartIn).append(" min.").toString();
	}
	
	public int getTimeLeftToRestart() {
		if (restartInSetAt == null) {
			return -1;
		}
		
		IWTimestamp restartSetAt = new IWTimestamp(restartInSetAt);
		IWTimestamp currentTime = new IWTimestamp(System.currentTimeMillis());
		int past = IWTimestamp.getMinutesBetween(restartSetAt, currentTime);
		if (past < 0) {
			return -1;
		}
		
		return restartIn > past ? restartIn - past : 0;
	}

	@Override
	public UIComponent getManagementPanel() {
		Layer layer = new Layer();
		
		int leftTillRestart = getTimeLeftToRestart();
		TextInput restartInValue = new TextInput("setRestartIn" + getValueIdentifier(),
				leftTillRestart < 0 ? CoreConstants.EMPTY : String.valueOf(leftTillRestart));
		Label restartInLabel = new Label("Set time (minutes) till system will be restarted", restartInValue);
		layer.add(restartInLabel);
		layer.add(restartInValue);
		
		return layer;
	}

	@Override
	public NotificationType getType() {
		return NotificationType.SHOW_ALWAYS;
	}

	@Override
	public String getNotificationIdentifier() {
		return String.valueOf(serialVersionUID);
	}

}
