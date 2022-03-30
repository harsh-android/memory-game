package com.avstore.braingame.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.avstore.braingame.engine.Engine;
import com.avstore.braingame.events.EventBus;

public class Shared {

	public static Context context;
	public static FragmentActivity factivity; // it's fine for this app, but better move to weak reference
	public static Engine engine;
	public static EventBus eventBus;

}
