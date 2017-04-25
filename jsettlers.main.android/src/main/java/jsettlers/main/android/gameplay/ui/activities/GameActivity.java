package jsettlers.main.android.gameplay.ui.activities;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;

import java.util.List;

import jsettlers.main.android.FullScreenAppCompatActivity;
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

public class GameActivity extends FullScreenAppCompatActivity implements GameNavigator, MenuNavigatorProvider {
	private static final String TAG_FRAGMENT_MAP = "map_fragment";
	private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

	private GameStarter gameStarter;

	private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter) getApplication();

		setContentView(R.layout.activity_game);

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
