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

package jsettlers.main.android.core.controls;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import jsettlers.main.android.R;
import jsettlers.main.android.core.GameService;
import jsettlers.main.android.gameplay.GameActivity_;
import jsettlers.main.android.mainmenu.navigation.Actions;

/**
 * Created by Andreas Eberle on 13.05.2017.
 */
@EBean
public class NotificationBuilder {
	private final Context context;
	private NotificationCompat.Builder builder;

	@StringRes(R.string.notification_game_in_progress)
	String title;
	@StringRes(R.string.game_menu_quit)
	String quit;
	@StringRes(R.string.game_menu_quit_confirm)
	String quitConfirmString;
	@StringRes(R.string.save_string)
	String saveString;
	@StringRes(R.string.pause_string)
	String pauseString;
	@StringRes(R.string.game_menu_unpause)
	String unpauseString;

	public NotificationBuilder(Context context) {
		this.context = context;
	}

	@AfterInject
	void setupBuilder() {
		Intent gameActivityIntent = GameActivity_.intent(context).action(Actions.ACTION_RESUME_GAME).get();
		PendingIntent gameActivityPendingIntent = PendingIntent.getActivity(context, 0, gameActivityIntent, 0);

		builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
				.setSmallIcon(R.drawable.icon)
				.setContentTitle(title)
				.setContentIntent(gameActivityPendingIntent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
	}

	public NotificationBuilder addQuitButton() {
		PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_QUIT), 0);
		builder.addAction(R.drawable.ic_stop, quit, quitPendingIntent);
		return this;
	}

	public NotificationBuilder addQuitConfirmButton() {
		PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_QUIT_CONFIRM), 0);
		builder.addAction(R.drawable.ic_stop, quitConfirmString, quitPendingIntent);
		return this;
	}

	public NotificationBuilder addSaveButton() {
		PendingIntent savePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_SAVE), 0);
		builder.addAction(R.drawable.ic_save, saveString, savePendingIntent);
		return this;
	}

	public NotificationBuilder addPauseButton() {
		PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_PAUSE), 0);
		builder.addAction(R.drawable.ic_pause, pauseString, pausePendingIntent);
		return this;
	}

	public NotificationBuilder addUnPauseButton() {
		PendingIntent unPausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_UNPAUSE), 0);
		builder.addAction(R.drawable.ic_play, unpauseString, unPausePendingIntent);
		return this;
	}

	public Notification build() {
		return builder.build();
	}
}