package jsettlers.main.android.fragments;

import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.graphics.startscreen.NetworkGameSettings;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.FreshMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class NewNetworkGameFragment extends MapSelectionFragment<IMapItem> {

	@Override
	protected MapListAdapter<IMapItem> generateListAdapter() {
		LayoutInflater inflater =
		        (LayoutInflater) getActivity().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		return new FreshMapListAdapter(inflater, getJsettlersActivity()
		        .getStartConnector().getMaps());
	}

	@Override
	protected String getItemDescription(IMapItem item) {
		return item.getDescription();
	}

	@Override
	protected boolean supportsDeletion() {
		return false;
	}

	@Override
	protected void deleteGame(IMapItem game) {
	}

	@Override
	protected void startGame(IMapItem game) {
		// TODO: Allow user to select name in GUI.
		String name = "Android network game";
		IMatchSettings gameSettings =
		        new NetworkGameSettings(game, name, game.getMaxPlayers(), null);
		getJsettlersActivity().getStartConnector().startNetworkGame(
		        gameSettings);
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(IMapItem game) {
		return 0;
	}

	@Override
	public String getName() {
		return "new-multi";
	}

	@Override
	protected int getHeadlineText() {
		return R.string.maplist_network_new_headline;
	}

	@Override
	protected int getStartButtonText() {
		return R.string.maplist_network_new_submit;
	}

}
