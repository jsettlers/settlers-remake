package jsettlers.main.android.fragments;

import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.main.android.resources.ResourceProvider;
import jsettlers.main.android.resources.UpdateListener;

/**
 * This is a fragment that automatically starts an update when displayed.
 * 
 * @author michael
 */
public class UpdateResourcesFragment extends ProgressFragment {

	private ResourceProvider provider;

	public UpdateResourcesFragment(ResourceProvider provider) {
		this.provider = provider;
	}

	@Override
	public void onResume() {
		super.onResume();

		ProgressConnector c = getConnector();

		provider.startUpdate(new UpdateListener() {
			@Override
			public void resourceUpdateFinished() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getJsettlersActivity().showStartScreen();
					}
				});
			}
		}, c);
	}

	@Override
	public String getName() {
		return "update";
	}

}
