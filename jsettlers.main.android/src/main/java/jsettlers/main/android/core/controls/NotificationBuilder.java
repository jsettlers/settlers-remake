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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import jsettlers.main.android.R;
import jsettlers.main.android.core.GameService;
import jsettlers.main.android.gameplay.GameActivity;
import jsettlers.main.android.mainmenu.navigation.Actions;

/**
 * Created by Andreas Eberle on 13.05.2017.
 */
public class NotificationBuilder {
	private final Context context;
	private final Resources resources;
	private final NotificationCompat.Builder builder;

	public NotificationBuilder(Context context) {
		this.context = context;
		resources = context.getResources();

		Intent gameActivityIntent = new Intent(context, GameActivity.class);
		gameActivityIntent.setAction(Actions.ACTION_RESUME_GAME);
		PendingIntent gameActivityPendingIntent = PendingIntent.getActivity(context, 0, gameActivityIntent, 0);

		builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
				.setSmallIcon(R.drawable.icon)
				.setContentTitle(resources.getString(R.string.notification_game_in_progress))
				.setContentIntent(gameActivityPendingIntent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
	}

	public NotificationBuilder addQuitButton() {
		PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_QUIT), 0);
		builder.addAction(R.drawable.ic_stop, resources.getString(R.string.game_menu_quit), quitPendingIntent);
		return this;
	}

	public NotificationBuilder addQuitConfirmButton() {
		PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_QUIT_CONFIRM), 0);
		builder.addAction(R.drawable.ic_stop, resources.getString(R.string.game_menu_quit_confirm), quitPendingIntent);
		return this;
	}

	public NotificationBuilder addSaveButton() {
		PendingIntent savePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_SAVE), 0);
		builder.addAction(R.drawable.ic_save, resources.getString(R.string.save_string), savePendingIntent);
		return this;
	}

	public NotificationBuilder addPauseButton() {
		PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_PAUSE), 0);
		builder.addAction(R.drawable.ic_pause, resources.getString(R.string.pause_string), pausePendingIntent);
		return this;
	}

	public NotificationBuilder addUnPauseButton() {
		PendingIntent unPausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(GameService.ACTION_UNPAUSE), 0);
		builder.addAction(R.drawable.ic_play, resources.getString(R.string.game_menu_unpause), unPausePendingIntent);
		return this;
	}

	public Notification build() {
		return builder.build();
	}
}