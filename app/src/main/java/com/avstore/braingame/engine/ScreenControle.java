package com.avstore.braingame.engine;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.avstore.braingame.R;
import com.avstore.braingame.common.Shared;
import com.avstore.braingame.events.ui.ResetBackground;
import com.avstore.braingame.fragments.DifficultySelectFragment;
import com.avstore.braingame.fragments.GameFragment;
import com.avstore.braingame.fragments.MenuFragment;
import com.avstore.braingame.fragments.SelectThemeFragment;

public class ScreenControle {

	private static ScreenControle mInstance = null;
	private static List<Screen> openedScreens = new ArrayList<Screen>();
	private FragmentManager mFragmentMngr;

	private ScreenControle() {
	}

	public static ScreenControle getInstance() {
		if (mInstance == null) {
			mInstance = new ScreenControle();
		}
		return mInstance;
	}

	public static enum Screen {
		MENU,
		GAME,
		DIFFICULTY,
		THEME_SELECT
	}
	
	public static Screen getLastScreen() {
		return openedScreens.get(openedScreens.size() - 1);
	}

	public void openScreen(Screen screen) {
		mFragmentMngr = Shared.factivity.getSupportFragmentManager();
		
		if (screen == Screen.GAME && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
			openedScreens.remove(openedScreens.size() - 1);
		} else if (screen == Screen.DIFFICULTY && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
			openedScreens.remove(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
		}
		Fragment fragment = getFragment(screen);
		FragmentTransaction fragmentTransaction = mFragmentMngr.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
		openedScreens.add(screen);
	}

	public boolean onBack() {
		if (openedScreens.size() > 0) {
			Screen screenToRemove = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			if (openedScreens.size() == 0) {
				return true;
			}
			Screen screen = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			openScreen(screen);
			if ((screen == Screen.THEME_SELECT || screen == Screen.MENU) && 
					(screenToRemove == Screen.DIFFICULTY || screenToRemove == Screen.GAME)) {
				Shared.eventBus.notify(new ResetBackground());
			}
			return false;
		}
		return true;
	}

	private Fragment getFragment(Screen screen) {
		switch (screen) {
		case MENU:
			return new MenuFragment();
		case DIFFICULTY:
			return new DifficultySelectFragment();
		case GAME:
			return new GameFragment();
		case THEME_SELECT:
			return new SelectThemeFragment();
		default:
			break;
		}
		return null;
	}
}
