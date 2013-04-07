package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.LoadableMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class LoadLocalGameFragment extends MapSelectionFragment<ILoadableGame> {

	@Override
	protected MapListAdapter<ILoadableGame> generateListAdapter() {
		LayoutInflater inflater =
		        (LayoutInflater) getActivity().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		return new LoadableMapListAdapter(inflater, getJsettlersActivity()
		        .getStartConnector().getLoadableGames());
	}

	@Override
	protected String getItemDescription(ILoadableGame item) {
		return item.getSaveTime().toLocaleString();
	}

	@Override
	protected boolean supportsDeletion() {
		return true;
	}

	@Override
	protected void deleteGame(ILoadableGame game) {
		getJsettlersActivity().getStartConnector().deleteLoadableGame(game);
	}

	@Override
	protected void startGame(ILoadableGame game) {
		getJsettlersActivity().getStartConnector().loadGame(game);
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(ILoadableGame game) {
		return 0;
	}

	@Override
	public String getName() {
		return "load-local";
	}

	@Override
	protected int getHeadlineText() {
		return R.string.maplist_local_load_headline;
	}

	@Override
	protected int getStartButtonText() {
		return R.string.maplist_local_load_submit;
	}

}
