package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.navigation.MainMenuNavigator;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.utils.FragmentUtil;

public class NewSinglePlayerFragment extends Fragment {
	private GameStarter gameStarter;
	private IMapDefinition map;

	private View.OnClickListener startGameClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			gameStarter.startGame();
		}
	};

	public static NewSinglePlayerFragment newInstance() {
		return new NewSinglePlayerFragment();
	}

	public NewSinglePlayerFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_single_player, container, false);
		FragmentUtil.setActionBar(this, view);

		Button startGameButton = (Button)view.findViewById(R.id.button_start_game);
		startGameButton.setOnClickListener(startGameClickListener);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gameStarter = (GameStarter)getActivity();
		map = gameStarter.getSelectedMap();

		TextView textViewName = (TextView)getView().findViewById(R.id.text_view_name);
		textViewName.setText(map.getMapName());
	}
}
