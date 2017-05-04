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

package jsettlers.main.android.gameplay.ui.activities;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;

import java.util.List;

import org.androidannotations.annotations.EActivity;

import jsettlers.main.android.R;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.core.navigation.BackPressedListener;
import jsettlers.main.android.gameplay.navigation.GameNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.main.android.gameplay.ui.fragments.LoadingFragment;
import jsettlers.main.android.gameplay.ui.fragments.MapFragment;
import jsettlers.main.android.mainmenu.navigation.Actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

@EActivity(R.layout.activity_game)
public class GameActivity extends FullScreenAppCompatActivity implements GameNavigator, MenuNavigatorProvider {
	private static final String TAG_FRAGMENT_MAP = "map_fragment";
	private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

	private GameStarter gameStarter;

	private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter) getApplication();

		IntentFilter intentFilter = new IntentFilter(ACTION_QUIT_CONFIRM);
		localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

		if (savedInstanceState != null)
			return;

		if (Actions.RESUME_GAME.equals(getIntent().getAction())) {
			showMap();
		} else {
			showLoading();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onBackPressed() {
		if (!gameStarter.getStartingGame().isStartupFinished()) {
			return; // Don't let the user back out of the loading screen
		}

		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		boolean handled = false;

		for (Fragment fragment : fragments) {
			if (fragment instanceof BackPressedListener) {
				BackPressedListener backPressedListener = (BackPressedListener) fragment;
				if (backPressedListener.onBackPressed()) {
					handled = true;
				}
			}
		}

		if (!handled) {
			super.onBackPressed();
		}
	}

	/**
	 * MenuNavigatorProvider implementation
	 */
	@Override
	public MenuNavigator getMenuNavigator() {
		return (MenuNavigator) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MAP);
	}

	/**
	 * GameNavigator implementation
	 */
	@Override
	public void showMap() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, MapFragment.newInstance(), TAG_FRAGMENT_MAP)
				.commitNow();
	}

	private void showLoading() {
		getSupportFragmentManager().beginTransaction()
				.add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
				.commitNow();
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case ACTION_QUIT_CONFIRM:
				finish();
				break;
			}
		}
	};
}
