package com.avstore.braingame.engine;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.avstore.braingame.R;
import com.avstore.braingame.common.Memory;
import com.avstore.braingame.common.Sound;
import com.avstore.braingame.common.Shared;
import com.avstore.braingame.engine.ScreenControle.Screen;
import com.avstore.braingame.events.EventObserverAdapter;
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
import com.avstore.braingame.model.BoardArrangment;
import com.avstore.braingame.model.BoardConfiguration;
import com.avstore.braingame.model.Game;
import com.avstore.braingame.model.GameState;
import com.avstore.braingame.themes.Theme;
import com.avstore.braingame.themes.Themes;
import com.avstore.braingame.ui.PopupManager;
import com.avstore.braingame.utils.Clock;
import com.avstore.braingame.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Engine extends EventObserverAdapter {

	private static Engine mInstance = null;
	private Game mPlayingGame = null;
	private int mFlippedId = -1;
	private int mToFlip = -1;
	private ScreenControle mScreenControle;
	private Theme mSelectedTheme;
	private ImageView mBGImage;
	private Handler mHandler;

	private Engine() {
		mScreenControle = ScreenControle.getInstance();
		mHandler = new Handler();
	}

	public static Engine getInstance() {
		if (mInstance == null) {
			mInstance = new Engine();
		}
		return mInstance;
	}

	public void start() {
		Shared.eventBus.listen(DifficultySelectedEvent.TYPE, this);
		Shared.eventBus.listen(FlipCardEvent.TYPE, this);
		Shared.eventBus.listen(StartGame.TYPE, this);
		Shared.eventBus.listen(SelectTheme.TYPE, this);
		Shared.eventBus.listen(BackGameEvent.TYPE, this);
		Shared.eventBus.listen(NextGame.TYPE, this);
		Shared.eventBus.listen(ResetBackground.TYPE, this);
	}

	public void stop() {
		mPlayingGame = null;
		mBGImage.setImageDrawable(null);
		mBGImage = null;
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;

		Shared.eventBus.unlisten(DifficultySelectedEvent.TYPE, this);
		Shared.eventBus.unlisten(FlipCardEvent.TYPE, this);
		Shared.eventBus.unlisten(StartGame.TYPE, this);
		Shared.eventBus.unlisten(SelectTheme.TYPE, this);
		Shared.eventBus.unlisten(BackGameEvent.TYPE, this);
		Shared.eventBus.unlisten(NextGame.TYPE, this);
		Shared.eventBus.unlisten(ResetBackground.TYPE, this);

		mInstance = null;
	}

	@Override
	public void onEvent(ResetBackground event) {
		Drawable drawable = mBGImage.getDrawable();
		if (drawable != null) {
			((TransitionDrawable) drawable).reverseTransition(2000);
		} else {
			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {
					Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
					return bitmap;
				}

				protected void onPostExecute(Bitmap bitmap) {
					mBGImage.setImageBitmap(bitmap);
				};

			}.execute();
		}
	}

	@Override
	public void onEvent(StartGame event) {
		mScreenControle.openScreen(Screen.THEME_SELECT);
	}

	@Override
	public void onEvent(NextGame event) {
		PopupManager.closePopup();
		int difficulty = mPlayingGame.boardConfiguration.difficulty;
		if (mPlayingGame.gameState.achievedStars == 3 && difficulty < 6) {
			difficulty++;
		}
		Shared.eventBus.notify(new DifficultySelectedEvent(difficulty));
	}

	@Override
	public void onEvent(BackGameEvent event) {
		PopupManager.closePopup();
		mScreenControle.openScreen(Screen.DIFFICULTY);
	}

	@Override
	public void onEvent(SelectTheme event) {
		mSelectedTheme = event.theme;
		mScreenControle.openScreen(Screen.DIFFICULTY);
		AsyncTask<Void, Void, TransitionDrawable> task = new AsyncTask<Void, Void, TransitionDrawable>() {

			@Override
			protected TransitionDrawable doInBackground(Void... params) {
				Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
				Bitmap backgroundImage = Themes.getBackgroundImage(mSelectedTheme);
				backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth());
				Drawable backgrounds[] = new Drawable[2];
				backgrounds[0] = new BitmapDrawable(Shared.context.getResources(), bitmap);
				backgrounds[1] = new BitmapDrawable(Shared.context.getResources(), backgroundImage);
				TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
				return crossfader;
			}

			@Override
			protected void onPostExecute(TransitionDrawable result) {
				super.onPostExecute(result);
				mBGImage.setImageDrawable(result);
				result.startTransition(2000);
			}
		};
		task.execute();
	}

	@Override
	public void onEvent(DifficultySelectedEvent event) {
		mFlippedId = -1;
		mPlayingGame = new Game();
		mPlayingGame.boardConfiguration = new BoardConfiguration(event.difficulty);
		mPlayingGame.theme = mSelectedTheme;
		mToFlip = mPlayingGame.boardConfiguration.numTiles;

		// arrange board
		arrangeBoard();

		// start the screen
		mScreenControle.openScreen(Screen.GAME);
	}

	private void arrangeBoard() {
		BoardConfiguration boardConfiguration = mPlayingGame.boardConfiguration;
		BoardArrangment boardArrangment = new BoardArrangment();

		// build pairs
		// result {0,1,2,...n} // n-number of tiles
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < boardConfiguration.numTiles; i++) {
			ids.add(i);
		}
		// shuffle
		// result {4,10,2,39,...}
		Collections.shuffle(ids);

		// place the board
		List<String> tileImageUrls = mPlayingGame.theme.tileImageUrls;
		Collections.shuffle(tileImageUrls);
		boardArrangment.pairs = new HashMap<Integer, Integer>();
		boardArrangment.tileUrls = new HashMap<Integer, String>();
		int j = 0;
		for (int i = 0; i < ids.size(); i++) {
			if (i + 1 < ids.size()) {
				// {4,10}, {2,39}, ...
				boardArrangment.pairs.put(ids.get(i), ids.get(i + 1));
				// {10,4}, {39,2}, ...
				boardArrangment.pairs.put(ids.get(i + 1), ids.get(i));
				// {4,
				boardArrangment.tileUrls.put(ids.get(i), tileImageUrls.get(j));
				boardArrangment.tileUrls.put(ids.get(i + 1), tileImageUrls.get(j));
				i++;
				j++;
			}
		}

		mPlayingGame.boardArrangment = boardArrangment;
	}

	@Override
	public void onEvent(FlipCardEvent event) {
		// Log.i("my_tag", "Flip: " + event.id);
		int id = event.id;
		if (mFlippedId == -1) {
			mFlippedId = id;
			// Log.i("my_tag", "Flip: mFlippedId: " + event.id);
		} else {
			if (mPlayingGame.boardArrangment.isPair(mFlippedId, id)) {
				// Log.i("my_tag", "Flip: is pair: " + mFlippedId + ", " + id);
				// send event - hide id1, id2
				Shared.eventBus.notify(new HideCards(mFlippedId, id), 1000);
				// play music
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Sound.playCorrent();
					}
				}, 1000);
				mToFlip -= 2;
				if (mToFlip == 0) {
					int passedSeconds = (int) (Clock.getInstance().getPassedTime() / 1000);
					Clock.getInstance().pause();
					int totalTime = mPlayingGame.boardConfiguration.time;
					GameState gameState = new GameState();
					mPlayingGame.gameState = gameState;
					// remained seconds
					gameState.remainedSeconds = totalTime - passedSeconds;
					gameState.passedSeconds = passedSeconds;

					// calc stars
					if (passedSeconds <= totalTime / 2) {
						gameState.achievedStars = 3;
					} else if (passedSeconds <= totalTime - totalTime / 5) {
						gameState.achievedStars = 2;
					} else if (passedSeconds < totalTime) {
						gameState.achievedStars = 1;
					} else {
						gameState.achievedStars = 0;
					}

					// calc score
					gameState.achievedScore = mPlayingGame.boardConfiguration.difficulty * gameState.remainedSeconds * mPlayingGame.theme.id;

					// save to memory
					Memory.save(mPlayingGame.theme.id, mPlayingGame.boardConfiguration.difficulty, gameState.achievedStars);
					Memory.saveTime(mPlayingGame.theme.id, mPlayingGame.boardConfiguration.difficulty ,gameState.passedSeconds);



					Shared.eventBus.notify(new GameWon(gameState), 1200);
				}
			} else {
				// Log.i("my_tag", "Flip: all down");
				// send event - flip all down
				Shared.eventBus.notify(new FlipDownCards(), 1000);
			}
			mFlippedId = -1;
			// Log.i("my_tag", "Flip: mFlippedId: " + mFlippedId);
		}
	}

	public Game getActiveGame() {
		return mPlayingGame;
	}

	public Theme getSelectedTheme() {
		return mSelectedTheme;
	}

	public void setBackgroundImageView(ImageView backgroundImage) {
		mBGImage = backgroundImage;
	}
}
