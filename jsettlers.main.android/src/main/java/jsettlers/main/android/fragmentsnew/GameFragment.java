package jsettlers.main.android.fragmentsnew;

import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;
import go.graphics.region.Region;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
	private GameProvider gameProvider;

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	public GameFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameProvider = (GameProvider)getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game, container, false);
		FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.frame_layout);

		Region goRegion = new Region(Region.POSITION_CENTER);
		Area goArea = new Area();
		goArea.add(goRegion);
		GOSurfaceView goView = new GOSurfaceView(getActivity(), goArea);

		goRegion.setContent(gameProvider.getMapContent());

		frameLayout.addView(goView);

		return view;
	}


}
