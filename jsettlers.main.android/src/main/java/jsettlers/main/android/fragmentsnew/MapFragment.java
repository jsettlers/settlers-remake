package jsettlers.main.android.fragmentsnew;

import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.GameService;
import jsettlers.main.android.Menus.GameMenu;
import jsettlers.main.android.R;
import jsettlers.main.android.dialogs.GameMenuDialog;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MapFragment extends Fragment implements BackPressedListener, GameMenuProvider {
	private GameMenu gameMenu;

	private boolean bound = false;

	private IContextDestroyedListener contextDestroyedListener = new IContextDestroyedListener() {
		@Override
		public void glContextDestroyed() {
			ImageProvider.getInstance().invalidateAll();
		}
	};

	public static MapFragment newInstance() {
		return new MapFragment();
	}

	public MapFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	//	getActivity().bindService(new Intent(getActivity(), GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);


//		MapContentProvider mapContentProvider = (MapContentProvider) getActivity();
//		addMapViews(mapContentProvider.getMapContent());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (bound) {
			gameMenu.unMute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (bound) {
			gameMenu.mute();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		if (bound) {
//			getActivity().unbindService(serviceConnection);
//		}
	}


	/**
	 * BackPressedListener implementation
	 */
	@Override
	public boolean onBackPressed() {
		if (bound) {
			GameMenuDialog.newInstance().show(getChildFragmentManager(), null);
		}
		return true;
	}

	/**
	 * GameMenuProvider implementation
	 */
	@Override
	public GameMenu getGameMenu() {
		return gameMenu;
	}

	public void gameReady(MapContent mapContent) {
		GameMenuProvider gameMenuProvider = (GameMenuProvider) getActivity();
		gameMenu = gameMenuProvider.getGameMenu();

		addMapViews(mapContent);

		bound = true;
	}

	public void addMapViews(MapContent mapContent) {
		FrameLayout frameLayout = (FrameLayout)getView().findViewById(R.id.frame_layout);

		Region goRegion = new Region(Region.POSITION_CENTER);
		goRegion.setContent(mapContent);

		Area goArea = new Area();
		goArea.add(goRegion);

		GOSurfaceView goView = new GOSurfaceView(getActivity(), goArea);
		goView.setContextDestroyedListener(contextDestroyedListener);
		frameLayout.addView(goView);
	}

//	private ServiceConnection serviceConnection = new ServiceConnection() {
//		@Override
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			GameService.GameBinder binder = (GameService.GameBinder) service;
//			GameService gameService = binder.getService();
//
//			gameMenu = gameService.getGameMenu();
//			bound = true;
//
//			addMapViews(gameService.getMapContent());
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			bound = false;
//		}
//	};
}
