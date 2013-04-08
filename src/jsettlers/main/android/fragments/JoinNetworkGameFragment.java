package jsettlers.main.android.fragments;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.JoinableMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class JoinNetworkGameFragment extends MapSelectionFragment<IMatch> {

	@Override
	protected MapListAdapter<IMatch> generateListAdapter() {
		LayoutInflater inflater =
		        (LayoutInflater) getActivity().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		INetworkConnector networkConnector =
		        getJsettlersActivity().getStartConnector()
		                .getNetworkConnector();
		networkConnector.setServerAddress(null);
		return new JoinableMapListAdapter(inflater, networkConnector);
	}

	@Override
	protected String getItemDescription(IMatch item) {
		return String.format("map id: %s\nmatch id: %s", item.getMapID(),
		        item.getMatchID());
	}

	@Override
	protected boolean supportsDeletion() {
		return false;
	}

	@Override
	protected void deleteGame(IMatch game) {
	}

	@Override
	protected void startGame(IMatch game) {
		getJsettlersActivity().getStartConnector().joinNetworkGame(game);
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(IMatch game) {
		return 0;
	}

	@Override
	public String getName() {
		return "join-select";
	}

	@Override
	protected int getHeadlineText() {
		return R.string.maplist_network_join_headline;
	}

	@Override
	protected int getStartButtonText() {
		return R.string.maplist_network_join_submit;
	}

}
