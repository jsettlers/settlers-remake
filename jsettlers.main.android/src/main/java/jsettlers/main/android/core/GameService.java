/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.core;

import org.androidannotations.annotations.EService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.controls.NotificationBuilder;
import jsettlers.main.android.core.controls.NotificationBuilder_;

@EService
public class GameService extends Service {
	public static final String ACTION_PAUSE = "com.jsettlers.pause";
	public static final String ACTION_UNPAUSE = "com.jsettlers.unpause";
	public static final String ACTION_SAVE = "com.jsettlers.save";
	public static final String ACTION_QUIT = "com.jsettlers.quit";
	public static final String ACTION_QUIT_CONFIRM = "com.jsettlers.quitconfirm";
	public static final String ACTION_QUIT_CANCELLED = "com.jsettlers.quitcancelled";

	public static final int NOTIFICATION_ID = 100;

	private final Observer<Boolean> pauseObserver = paused -> postNotification();
	private final Observer<GameMenu.GameState> gameStateObserver = gameState -> postNotification();

	private GameMenu gameMenu;
	private NotificationManager notificationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		GameManager gameManager = (GameManager) getApplication();
		gameMenu = gameManager.getGameMenu();
		notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		intentFilter.addAction(ACTION_SAVE);
		intentFilter.addAction(ACTION_QUIT);
		intentFilter.addAction(ACTION_QUIT_CONFIRM);
		registerReceiver(broadcastReceiver, intentFilter);

		createNotificationChannel();

		startForeground(NOTIFICATION_ID, createNotification());

		gameMenu.isPausedState().observeForever(pauseObserver);
		gameMenu.getGameState().observeForever(gameStateObserver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
		gameMenu.isPausedState().removeObserver(pauseObserver);
		gameMenu.getGameState().removeObserver(gameStateObserver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void postNotification() {
		if (gameMenu.getGameState().getValue() == GameMenu.GameState.QUITTED) {
			notificationManager.cancel(NOTIFICATION_ID);
			stopForeground(true);
			stopSelf();
		} else {
			notificationManager.notify(NOTIFICATION_ID, createNotification());
		}
	}

	private Notification createNotification() {
		NotificationBuilder notificationBuilder = NotificationBuilder_.getInstance_(getApplicationContext());

		if (gameMenu.getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT) {
			notificationBuilder.addQuitConfirmButton();
		} else {
			notificationBuilder.addQuitButton();
		}

		notificationBuilder.addSaveButton();

		if (gameMenu.isPausedState().getValue() == Boolean.TRUE) {
			notificationBuilder.addUnPauseButton();
		} else {
			notificationBuilder.addPauseButton();
		}

		return notificationBuilder.build();
	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.app_name);
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
			notificationManager.createNotificationChannel(channel);
		}
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case ACTION_PAUSE:
				gameMenu.pause();
				break;
			case ACTION_UNPAUSE:
				gameMenu.unPause();
				break;
			case ACTION_SAVE:
				gameMenu.save();
				break;
			case ACTION_QUIT:
				gameMenu.quit();
				break;
			case ACTION_QUIT_CONFIRM:
				gameMenu.quitConfirm();
				break;
			}
		}
	};
}
