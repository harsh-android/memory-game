package com.avstore.braingame.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.avstore.braingame.R;
import com.avstore.braingame.common.Sound;
import com.avstore.braingame.common.Shared;
import com.avstore.braingame.events.ui.StartGame;
import com.avstore.braingame.ui.PopupManager;
import com.avstore.braingame.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MenuFragment extends Fragment {

	private ImageView mTitle;
	private ImageView mStartGmButton;
	private ImageView mStartButtonLights;
	private ImageView mTooltip;
	private ImageView mSettingsGameButton;
	private ImageView mGooglePlayGmButton;

	private InterstitialAd mInterstitialAd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_fragment, container, false);

		mTitle = (ImageView) view.findViewById(R.id.title);
		mStartGmButton = (ImageView) view.findViewById(R.id.start_game_button);
		mSettingsGameButton = (ImageView) view.findViewById(R.id.settings_game_button);
		mSettingsGameButton.setSoundEffectsEnabled(false);
		mSettingsGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupManager.showPopupSettings();
			}
		});
		mGooglePlayGmButton = (ImageView) view.findViewById(R.id.google_play_button);
		mGooglePlayGmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=AV+Store")));
				}
				catch (Exception e){
				}
//				Toast.makeText(getActivity(), "Leaderboards will be available in the next game updates", Toast.LENGTH_LONG).show();
			}
		});

		AdView adView = new AdView(getContext());
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(getString(R.string.banner));
		adView = view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		mInterstitialAd = new InterstitialAd(getContext());
		mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
		mInterstitialAd.loadAd(new AdRequest.Builder().build());

		mStartButtonLights = (ImageView) view.findViewById(R.id.start_game_button_lights);
		mTooltip = (ImageView) view.findViewById(R.id.tooltip);
		mStartGmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mInterstitialAd.loadAd(new AdRequest.Builder().build());
				if (mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();
				} else {
					// animate title from place and navigation buttons from place
					animateAllAssetsOff(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							Shared.eventBus.notify(new StartGame());
						}
					});
					Log.d("TAG", "The interstitial wasn't loaded yet.");
				}

			}
		});

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				// Code to be executed when an ad finishes loading.
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// Code to be executed when an ad request fails.
			}

			@Override
			public void onAdOpened() {
				// Code to be executed when the ad is displayed.
			}

			@Override
			public void onAdLeftApplication() {
				// Code to be executed when the user has left the app.
			}

			@Override
			public void onAdClosed() {
				// Code to be executed when the interstitial ad is closed.
				animateAllAssetsOff(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						Shared.eventBus.notify(new StartGame());
					}
				});
			}
		});

		startLightsAnimation();
		startTootipAnimation();

		// play background music
		Sound.playBackgroundMusic();
		return view;
	}

	protected void animateAllAssetsOff(AnimatorListenerAdapter adapter) {
		// title
		// 120dp + 50dp + buffer(30dp)
		ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(mTitle, "translationY", Utils.px(-200));
		titleAnimator.setInterpolator(new AccelerateInterpolator(2));
		titleAnimator.setDuration(300);

		// lights
		ObjectAnimator lightsAnimatorX = ObjectAnimator.ofFloat(mStartButtonLights, "scaleX", 0f);
		ObjectAnimator lightsAnimatorY = ObjectAnimator.ofFloat(mStartButtonLights, "scaleY", 0f);

		// tooltip
		ObjectAnimator tooltipAnimator = ObjectAnimator.ofFloat(mTooltip, "alpha", 0f);
		tooltipAnimator.setDuration(100);

		// settings button
		ObjectAnimator settingsAnimator = ObjectAnimator.ofFloat(mSettingsGameButton, "translationY", Utils.px(120));
		settingsAnimator.setInterpolator(new AccelerateInterpolator(2));
		settingsAnimator.setDuration(300);

		// google play button
		ObjectAnimator googlePlayAnimator = ObjectAnimator.ofFloat(mGooglePlayGmButton, "translationY", Utils.px(120));
		googlePlayAnimator.setInterpolator(new AccelerateInterpolator(2));
		googlePlayAnimator.setDuration(300);

		// start button
		ObjectAnimator startButtonAnimator = ObjectAnimator.ofFloat(mStartGmButton, "translationY", Utils.px(130));
		startButtonAnimator.setInterpolator(new AccelerateInterpolator(2));
		startButtonAnimator.setDuration(300);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(titleAnimator, lightsAnimatorX, lightsAnimatorY, tooltipAnimator, settingsAnimator, googlePlayAnimator, startButtonAnimator);
		animatorSet.addListener(adapter);
		animatorSet.start();
	}

	private void startTootipAnimation() {
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTooltip, "scaleY", 0.8f);
		scaleY.setDuration(200);
		ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(mTooltip, "scaleY", 1f);
		scaleYBack.setDuration(500);
		scaleYBack.setInterpolator(new BounceInterpolator());
		final AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setStartDelay(1000);
		animatorSet.playSequentially(scaleY, scaleYBack);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animatorSet.setStartDelay(2000);
				animatorSet.start();
			}
		});
		mTooltip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animatorSet.start();
	}

	private void startLightsAnimation() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(mStartButtonLights, "rotation", 0f, 360f);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		animator.setDuration(6000);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		mStartButtonLights.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animator.start();
	}

}
