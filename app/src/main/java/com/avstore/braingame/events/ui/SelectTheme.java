package com.avstore.braingame.events.ui;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;
import com.avstore.braingame.themes.Theme;

public class SelectTheme extends AbstractEvent {

	public static final String TYPE = SelectTheme.class.getName();
	public final Theme theme;

	public SelectTheme(Theme theme) {
		this.theme = theme;
	}

	@Override
	protected void fire(EventObserver eventObserver) {
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
