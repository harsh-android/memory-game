package com.avstore.braingame.events.ui;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;

/**
 * When the 'back to menu' was pressed.
 */
public class ResetBackground extends AbstractEvent {

	public static final String TYPE = ResetBackground.class.getName();

	@Override
	protected void fire(EventObserver eventObserver) {
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
