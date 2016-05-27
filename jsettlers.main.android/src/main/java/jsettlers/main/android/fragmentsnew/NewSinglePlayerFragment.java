package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;
import jsettlers.main.android.utils.FragmentUtil;

public class NewSinglePlayerFragment extends Fragment {

	public static NewSinglePlayerFragment newInstance() {
		return new NewSinglePlayerFragment();
	}

	public NewSinglePlayerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_local, container, false);
		FragmentUtil.setActionBar(this, view);
		return view;
	}

}
