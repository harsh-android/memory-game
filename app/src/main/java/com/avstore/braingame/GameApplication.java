package com.avstore.braingame;

import android.app.Application;

import com.avstore.braingame.utils.Loader;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Loader.loadFonts(this);

	}
}
