package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.LoadableMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class LoadLocalGameFragment extends MapSelectionFragment<IMapDefinition> {

	@Override
	protected MapListAdapter<IMapDefinition> generateListAdapter() {
		LayoutInflater inflater =
				(LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		return new LoadableMapListAdapter(inflater, getJsettlersActivity()
				.getStartConnector().getStoredSingleplayerGames());
	}

	@Override
	protected String getItemDescription(IMapDefinition item) {
		return item.getCreationDate().toLocaleString();
	}

	@Override
	protected boolean supportsDeletion() {
		return true;
	}

	@Override
	protected void deleteGame(IMapDefinition game) {
		// TODO RE-Enable delete for loadable games.
		// getJsettlersActivity().getStartConnector().deleteLoadableGame(game);
	}

	@Override
	protected void startGame(IMapDefinition game) {
		getJsettlersActivity().getStartConnector().loadSingleplayerGame(game);
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(IMapDefinition game) {
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
