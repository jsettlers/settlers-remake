package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.interfaces.ILoadableMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.LoadableMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class LoadLocalGameFragment extends MapSelectionFragment<ILoadableMapDefinition> {

	@Override
	protected MapListAdapter<ILoadableMapDefinition> generateListAdapter() {
		LayoutInflater inflater =
		        (LayoutInflater) getActivity().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		return new LoadableMapListAdapter(inflater, getJsettlersActivity()
		        .getStartConnector().getStoredSingleplayerGames());
	}

	@Override
	protected String getItemDescription(ILoadableMapDefinition item) {
		return item.getSaveTime().toLocaleString();
	}

	@Override
	protected boolean supportsDeletion() {
		return true;
	}

	@Override
	protected void deleteGame(ILoadableMapDefinition game) {
		//TODO RE-Enable delete for loadable games.
		//getJsettlersActivity().getStartConnector().deleteLoadableGame(game);
	}

	@Override
	protected void startGame(ILoadableMapDefinition game) {
		getJsettlersActivity().getStartConnector().loadSingleplayerGame(game);
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(ILoadableMapDefinition game) {
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
