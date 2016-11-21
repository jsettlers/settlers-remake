package jsettlers.main.android.fragmentsnew;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.dialogs.GameMenuDialog;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MapFragment extends Fragment implements BackPressedListener, GameMenuProvider {
	private static final String TAG_FRAGMENT_GAME_MENU = "com.jsettlers.gamemenufragment";

	private GameMenu gameMenu;
	private LocalBroadcastManager localBroadcastManager;

	private boolean isAttachedToGame = false;

	public static MapFragment newInstance() {
		return new MapFragment();
	}

	public MapFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		localBroadcastManager.registerReceiver(mapVisibileBroadcastReceiver, intentFilter);

		if (isAttachedToGame) {
			resumeView();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		localBroadcastManager.unregisterReceiver(mapVisibileBroadcastReceiver);

		if (isAttachedToGame) {
			pauseView();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * BackPressedListener implementation
	 */
	@Override
	public boolean onBackPressed() {
		showGameMenu();
		return true;
	}

	/**
	 * GameMenuProvider implementation
	 */
	@Override
	public GameMenu getGameMenu() {
		return gameMenu;
	}

	public void attachToGame() {
		GameMenuProvider gameMenuProvider = (GameMenuProvider) getActivity();
		gameMenu = gameMenuProvider.getGameMenu();

		MapContentProvider mapContentProvider = (MapContentProvider) getActivity();
		addMapViews(mapContentProvider.getMapContent());

		isAttachedToGame = true;

		resumeView();
	}

	private void addMapViews(MapContent mapContent) {
		FrameLayout frameLayout = (FrameLayout)getView().findViewById(R.id.frame_layout);

		Region goRegion = new Region(Region.POSITION_CENTER);
		goRegion.setContent(mapContent);

		Area goArea = new Area();
		goArea.add(goRegion);

		GOSurfaceView goView = new GOSurfaceView(getActivity(), goArea);
		goView.setContextDestroyedListener(contextDestroyedListener);
		frameLayout.addView(goView);
	}

	private void showGameMenu() {
		if (isAttachedToGame && getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_GAME_MENU) == null) {
			GameMenuDialog.newInstance().show(getChildFragmentManager(), TAG_FRAGMENT_GAME_MENU);
		}
	}

	private void resumeView() {
		if (gameMenu.isPaused()) {
			showGameMenu();
		} else {
			gameMenu.unMute();
		}
	}

	private void pauseView() {
		gameMenu.mute();
	}

	private final BroadcastReceiver mapVisibileBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case ACTION_PAUSE:
					showGameMenu();
					break;
				case ACTION_UNPAUSE:
					gameMenu.unMute();
					break;
			}
		}
	};

	private IContextDestroyedListener contextDestroyedListener = new IContextDestroyedListener() {
		@Override
		public void glContextDestroyed() {
			ImageProvider.getInstance().invalidateAll();
		}
	};
}
