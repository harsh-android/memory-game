package com.avstore.braingame.events.ui;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;

public class StartGame extends AbstractEvent {

	public static final String TYPE = StartGame.class.getName();

	@Override
	protected void fire(EventObserver eventObserver) {
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
