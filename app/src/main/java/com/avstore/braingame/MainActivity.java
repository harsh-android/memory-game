package com.avstore.braingame;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.avstore.braingame.common.Shared;
import com.avstore.braingame.engine.Engine;
import com.avstore.braingame.engine.ScreenControle;
import com.avstore.braingame.engine.ScreenControle.Screen;
import com.avstore.braingame.events.EventBus;
import com.avstore.braingame.events.ui.BackGameEvent;
import com.avstore.braingame.ui.PopupManager;
import com.avstore.braingame.utils.Utils;

public class MainActivity extends FragmentActivity {

	private ImageView bgimage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Shared.context = getApplicationContext();
		Shared.engine = Engine.getInstance();
		Shared.eventBus = EventBus.getInstance();

		setContentView(R.layout.activity_main);
		bgimage = (ImageView) findViewById(R.id.background_image);

		Shared.factivity = this;
		Shared.engine.start();
		Shared.engine.setBackgroundImageView(bgimage);

		// set background
		setBGImage();

		// set menu
		ScreenControle.getInstance().openScreen(Screen.MENU);


	}

	@Override
	protected void onDestroy() {
		Shared.engine.stop();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (PopupManager.isShown()) {
			PopupManager.closePopup();
			if (ScreenControle.getLastScreen() == Screen.GAME) {
				Shared.eventBus.notify(new BackGameEvent());
			}
		} else if (ScreenControle.getInstance().onBack()) {
			super.onBackPressed();
		}
	}

	private void setBGImage() {
		Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
		bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth());
		bitmap = Utils.downscaleBitmap(bitmap, 2);
		bgimage.setImageBitmap(bitmap);
	}

}
