package com.avstore.braingame.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avstore.braingame.R;
import com.avstore.braingame.common.Shared;
import com.avstore.braingame.events.engine.FlipDownCards;
import com.avstore.braingame.events.engine.GameWon;
import com.avstore.braingame.events.engine.HideCards;
import com.avstore.braingame.model.Game;
import com.avstore.braingame.ui.BoardView;
import com.avstore.braingame.ui.PopupManager;
import com.avstore.braingame.utils.Clock;
import com.avstore.braingame.utils.Clock.OnTimerCount;
import com.avstore.braingame.utils.Loader;
import com.avstore.braingame.utils.Loader.Font;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GameFragment extends MainFragment {

	private BoardView mBoardView;
	private TextView mTime;
	private ImageView mTimeImg;
	private LinearLayout ads;
	private InterstitialAd mInterstitialAd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.game_fragment, container, false);
		view.setClipChildren(false);
		((ViewGroup)view.findViewById(R.id.game_board)).setClipChildren(false);
		mTime = (TextView) view.findViewById(R.id.time_bar_text);
		mTimeImg = (ImageView) view.findViewById(R.id.time_bar_image);
		Loader.setTypeface(Shared.context, new TextView[] {mTime}, Font.GROBOLD);
		mBoardView = BoardView.fromXml(getActivity().getApplicationContext(), view);
		FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.game_container);
		frameLayout.addView(mBoardView);
		frameLayout.setClipChildren(false);

		AdView adView = new AdView(getContext());
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(getString(R.string.banner));
		adView = view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		mInterstitialAd = new InterstitialAd(getContext());
		mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
		mInterstitialAd.loadAd(new AdRequest.Builder().build());


		// build board
		buildBoard();
		Shared.eventBus.listen(FlipDownCards.TYPE, this);
		Shared.eventBus.listen(HideCards.TYPE, this);
		Shared.eventBus.listen(GameWon.TYPE, this);
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		Shared.eventBus.unlisten(FlipDownCards.TYPE, this);
		Shared.eventBus.unlisten(HideCards.TYPE, this);
		Shared.eventBus.unlisten(GameWon.TYPE, this);
		super.onDestroy();
	}

	private void buildBoard() {
		Game game = Shared.engine.getActiveGame();
		int time = game.boardConfiguration.time;
		setTime(time);
		mBoardView.setBoard(game);
		
		startClock(time);
	}
	
	private void setTime(int time) {
		int min = time / 60;
		int sec = time - min*60;
		mTime.setText(" " + String.format("%02d", min) + ":" + String.format("%02d", sec));
	}

	private void startClock(int sec) {
		Clock clock = Clock.getInstance();
		clock.startTimer(sec*1000, 1000, new OnTimerCount() {
			
			@Override
			public void onTick(long millisUntilFinished) {
				setTime((int) (millisUntilFinished/1000));
			}
			
			@Override
			public void onFinish() {
				setTime(0);
			}
		});
	}

	@Override
	public void onEvent(final GameWon event) {

		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		} else {
			mTime.setVisibility(View.GONE);
			mTimeImg.setVisibility(View.GONE);
			PopupManager.showPopupWon(event.gameState);
		}
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
				mTime.setVisibility(View.GONE);
				mTimeImg.setVisibility(View.GONE);
				PopupManager.showPopupWon(event.gameState);
				// Code to be executed when the interstitial ad is closed.
			}
		});
	}

	@Override
	public void onEvent(FlipDownCards event) {
		mBoardView.flipDownAll();
	}

	@Override
	public void onEvent(HideCards event) {
		mBoardView.hideCards(event.id1, event.id2);
	}

}
