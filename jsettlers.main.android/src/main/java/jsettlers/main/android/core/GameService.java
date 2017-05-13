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

import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.core.controls.GameMenu.NOTIFICATION_ID;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

@EService
public class GameService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		GameManager gameManager = (GameManager) getApplication();
		startForeground(NOTIFICATION_ID, gameManager.getGameMenu().createNotification());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Receiver(actions = ACTION_QUIT_CONFIRM, local = true)
	void quitConfirmReceived() {
		stopForeground(true);
		stopSelf();
	}
}
