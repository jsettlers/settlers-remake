package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.utils.FragmentUtil;

public class NewSinglePlayerFragment extends Fragment {
	private GameStarter gameStarter;
	private IMapDefinition map;

	public static NewSinglePlayerFragment newInstance() {
		return new NewSinglePlayerFragment();
	}

	public NewSinglePlayerFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter)getActivity();
		map = gameStarter.getSelectedMap();
		String name = map.getMapName();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_single_player, container, false);
		FragmentUtil.setActionBar(this, view);
		return view;
	}

}
