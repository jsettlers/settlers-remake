package jsettlers.main.android.mainmenu.ui.fragments.picker;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.picker.MapPickerPresenter;

import android.support.v4.app.Fragment;

/**
 * Created by tompr on 19/01/2017.
 */

public class LoadSinglePlayerPickerFragment extends MapPickerFragment {
	public static Fragment newInstance() {
		return new LoadSinglePlayerPickerFragment();
	}

	@Override
	protected MapPickerPresenter getPresenter() {
		return PresenterFactory.createLoadSinglePlayerPickerPresenter(getActivity(), this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.load_single_player_game);
	}

	@Override
	protected boolean showMapDates() {
		return true;
	}
}
