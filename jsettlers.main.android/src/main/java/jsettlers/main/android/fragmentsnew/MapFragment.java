package jsettlers.main.android.fragmentsnew;

import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.Menus.GameMenu;
import jsettlers.main.android.dialogs.GameMenuDialog;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MapFragment extends Fragment implements BackPressedListener, GameMenuProvider {
	private GameMenu gameMenu;

	private boolean isAttachedToGame = false;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isAttachedToGame) {
			gameMenu.unMute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isAttachedToGame) {
			gameMenu.mute();
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
		if (isAttachedToGame) {
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

	public void attachToGame() {
		GameMenuProvider gameMenuProvider = (GameMenuProvider) getActivity();
		gameMenu = gameMenuProvider.getGameMenu();

		MapContentProvider mapContentProvider = (MapContentProvider) getActivity();
		addMapViews(mapContentProvider.getMapContent());

		isAttachedToGame = true;
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
}
