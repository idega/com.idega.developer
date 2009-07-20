package com.idega.development.presentation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.development.business.DeveloperConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.notifier.business.Notifier;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SelectOption;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;

public class NotificationsManager extends Block {

	private static final String PARAMETER_SELECTED_NOTIFIER = "selectedNotifier";
	private static final String PARAMETER_SET_ACTIVE = "setActiveNotifier";
	
	@SuppressWarnings("unchecked")
	@Override
	public void main(IWContext iwc) {
		PresentationUtil.addStyleSheetToHeader(iwc, getBundle(iwc).getVirtualPathWithFileNameString("style/developer.css"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("notificationsManager");
		add(topLayer);

		FieldSet fieldSet = new FieldSet(iwrb.getLocalizedString("manage_notifiers", "Manage notifiers"));
		topLayer.add(fieldSet);
		
		Form form = new Form();
		fieldSet.add(form);
		
		String selectedNotifier = iwc.getParameter(PARAMETER_SELECTED_NOTIFIER);
		form.addParameter(PARAMETER_SELECTED_NOTIFIER, StringUtil.isEmpty(selectedNotifier) ? String.valueOf(-1) : selectedNotifier);
		String setActiveNotifier = iwc.getParameter(PARAMETER_SET_ACTIVE);
		
		DropdownMenu notifiers = new DropdownMenu("notifier");
		notifiers.setFirstSelectOption(new SelectOption(iwrb.getLocalizedString("choose_notifier", "Select notifier"), -1));
		notifiers.setOnChange(new StringBuilder("this.form['").append(PARAMETER_SELECTED_NOTIFIER).append("'].value = dwr.util.getValue('")
				.append(notifiers.getId()).append("'); this.form.submit();").toString());
		
		Label label = new Label(iwrb.getLocalizedString("choose_notifier", "Select notifier"), notifiers);
		form.add(label);
		form.add(notifiers);
		
		Map<String, ? extends Notifier> beans = WebApplicationContextUtils.getWebApplicationContext(iwc.getServletContext()).getBeansOfType(Notifier.class);
		if (beans == null || beans.isEmpty()) {
			return;
		}
		
		Notifier activeNotifier = null;
		for (String notifierKey: beans.keySet()) {
			Notifier notifier = beans.get(notifierKey);
			notifiers.addOption(new SelectOption(notifierKey, notifier.getClass().getName()));
			
			if (activeNotifier == null && !StringUtil.isEmpty(selectedNotifier) && selectedNotifier.equals(notifier.getClass().getName())) {
				activeNotifier = notifier;
			}
		}
		if (!StringUtil.isEmpty(selectedNotifier)) {
			notifiers.setSelectedOption(selectedNotifier);
		}
		
		if (activeNotifier == null) {
			return;
		}
		
		if (!StringUtil.isEmpty(setActiveNotifier)) {
			activeNotifier.setActive(Boolean.TRUE.toString().equals(setActiveNotifier));
		}
		form.addParameter(PARAMETER_SET_ACTIVE, String.valueOf(activeNotifier.isActive()));
		
		Layer activeContainer = new Layer();
		form.add(activeContainer);
		CheckBox setActiveBox = new CheckBox();
		setActiveBox.setChecked(activeNotifier.isActive());
		setActiveBox.setOnClick(new StringBuilder("this.form['").append(PARAMETER_SET_ACTIVE).append("'].value = document.getElementById('")
				.append(setActiveBox.getId()).append("').checked == true; this.form.submit();").toString());
		Label setActiveLabel = new Label(iwrb.getLocalizedString("set_active_notifier", "Active"), setActiveBox);
		activeContainer.add(setActiveLabel);
		activeContainer.add(setActiveBox);
		
		Enumeration<String> keys = iwc.getParameterNames();
		if (keys == null) {
			return;
		}
		
		List<AdvancedProperty> properties = new ArrayList<AdvancedProperty>();
		String activeNotifierClass = activeNotifier.getClass().getName();
		String needless = activeNotifier.getValueIdentifier();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.indexOf(activeNotifierClass) == -1) {
				continue;
			}
			
			String value = iwc.getParameter(key);
			properties.add(new AdvancedProperty(key.replaceFirst(needless, CoreConstants.EMPTY), value));
		}
		Method[] methods = activeNotifier.getClass().getMethods();
		for (AdvancedProperty property: properties) {
			Method toInvoke = getMethod(methods, property.getId());
			if (toInvoke == null) {
				continue;
			} else {
				try {
					toInvoke.invoke(activeNotifier, property.getValue());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		UIComponent managementPanel = activeNotifier.getManagementPanel();
		if (managementPanel != null) {
			Layer managementContainer = new Layer();
			form.add(managementContainer);
			managementContainer.add(managementPanel);
			
			SubmitButton save = new SubmitButton(iwrb.getLocalizedString("save", "Save"));
			form.add(save);
		}
		
		
	}
	
	private Method getMethod(Method[] methods, String name) {
		for (Method method: methods) {
			if (name.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}
	
	@Override
	public String getBundleIdentifier() {
		return DeveloperConstants.BUNDLE_IDENTIFIER;
	}
	
}