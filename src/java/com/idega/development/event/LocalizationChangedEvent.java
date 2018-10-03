package com.idega.development.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

public class LocalizationChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -1264632126678290250L;

	private Map<String, String> localizations;

	public LocalizationChangedEvent(Object source, Map<String, String> localizations) {
		super(source);

		this.localizations = localizations;
	}

	public Map<String, String> getLocalizations() {
		return localizations;
	}

}