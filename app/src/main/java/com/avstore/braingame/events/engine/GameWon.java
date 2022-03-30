package com.avstore.braingame.events.engine;

import com.avstore.braingame.events.AbstractEvent;
import com.avstore.braingame.events.EventObserver;
import com.avstore.braingame.model.GameState;

/**
 * When the 'back to menu' was pressed.
 */
public class GameWon extends AbstractEvent {

	public static final String TYPE = GameWon.class.getName();

	public GameState gameState;

	
	public GameWon(GameState gameState) {
		this.gameState = gameState;
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
