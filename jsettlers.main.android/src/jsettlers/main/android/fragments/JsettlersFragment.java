package jsettlers.main.android.fragments;

import jsettlers.main.android.JsettlersActivity;
import android.app.Fragment;

public abstract class JsettlersFragment extends Fragment {
	/**
	 * Gets a name that identifies the fragment type.
	 * 
	 * @return
	 */
	public abstract String getName();

	public JsettlersActivity getJsettlersActivity() {
		return (JsettlersActivity) getActivity();
	}

	public boolean shouldAddToBackStack() {
		return true;
	}

	public boolean onBackButtonPressed() {
		return false;
	}
}
