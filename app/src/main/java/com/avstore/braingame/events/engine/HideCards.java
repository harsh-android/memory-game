package com.avstore.braingame.events.engine;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;

/**
 * When the 'back to menu' was pressed.
 */
public class HideCards extends AbstractEvent {

	public static final String TYPE = HideCards.class.getName();
	public int id1;
	public int id2;

	public HideCards(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
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
