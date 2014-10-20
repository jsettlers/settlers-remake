package jsettlers.main.android.fragments.progress;

import jsettlers.main.android.resources.UpdateListener;

/**
 * This is a fragment that automatically starts an update when displayed.
 * 
 * @author michael
 */
public class UpdateResourcesFragment extends ProgressFragment implements UpdateListener {

	@Override
	public void onResume() {
		super.onResume();

		getJsettlersActivity().getResourceProvider().startUpdate(this);
	}

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public void resourceUpdateFinished() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getJsettlersActivity().showStartScreen();
			}
		});
	}
	
	
}
