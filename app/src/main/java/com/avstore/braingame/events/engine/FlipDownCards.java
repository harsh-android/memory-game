package com.avstore.braingame.events.engine;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;

/**
 * When the 'back to menu' was pressed.
 */
public class FlipDownCards extends AbstractEvent {

	public static final String TYPE = FlipDownCards.class.getName();

	public FlipDownCards() {
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
