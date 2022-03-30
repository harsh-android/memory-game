package com.avstore.braingame.events;

import com.avstore.braingame.events.engine.FlipDownCards;
import com.avstore.braingame.events.engine.GameWon;
import com.avstore.braingame.events.engine.HideCards;
import com.avstore.braingame.events.ui.BackGameEvent;
import com.avstore.braingame.events.ui.DifficultySelectedEvent;
import com.avstore.braingame.events.ui.FlipCardEvent;
import com.avstore.braingame.events.ui.NextGame;
import com.avstore.braingame.events.ui.ResetBackground;
import com.avstore.braingame.events.ui.StartGame;
import com.avstore.braingame.events.ui.SelectTheme;


public interface EventObserver {

	void onEvent(FlipCardEvent event);

	void onEvent(DifficultySelectedEvent event);

	void onEvent(HideCards event);

	void onEvent(FlipDownCards event);

	void onEvent(StartGame event);

	void onEvent(SelectTheme event);

	void onEvent(GameWon event);

	void onEvent(BackGameEvent event);

	void onEvent(NextGame event);

	void onEvent(ResetBackground event);

}
