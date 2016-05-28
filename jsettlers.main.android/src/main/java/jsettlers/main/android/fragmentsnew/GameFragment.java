package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	public GameFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_game, container, false);
	}

}
