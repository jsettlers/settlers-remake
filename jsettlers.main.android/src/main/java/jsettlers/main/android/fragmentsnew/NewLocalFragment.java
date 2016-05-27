package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;
import jsettlers.main.android.utils.FragmentUtil;

public class NewLocalFragment extends Fragment {

	public static NewLocalFragment newInstance() {
		return new NewLocalFragment();
	}

	public NewLocalFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_local, container, false);
		FragmentUtil.setActionBar(this, view);
		return view;
	}

}
