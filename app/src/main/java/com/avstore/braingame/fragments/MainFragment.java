package com.avstore.braingame.fragments;

import android.support.v4.app.Fragment;

import com.avstore.braingame.events.EventObserver;
import com.avstore.braingame.events.engine.FlipDownCards;
import com.avstore.braingame.events.engine.GameWon;
import com.avstore.braingame.events.engine.HideCards;
import com.avstore.braingame.events.ui.BackGameEvent;
import com.avstore.braingame.events.ui.FlipCardEvent;
import com.avstore.braingame.events.ui.NextGame;
import com.avstore.braingame.events.ui.ResetBackground;
import com.avstore.braingame.events.ui.SelectTheme;
import com.avstore.braingame.events.ui.DifficultySelectedEvent;
import com.avstore.braingame.events.ui.StartGame;

public class MainFragment extends Fragment implements EventObserver {

	@Override
	public void onEvent(FlipCardEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(DifficultySelectedEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(HideCards event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(FlipDownCards event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(StartGame event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(SelectTheme event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(GameWon event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(BackGameEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(NextGame event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(ResetBackground event) {
		throw new UnsupportedOperationException();
	}

}
