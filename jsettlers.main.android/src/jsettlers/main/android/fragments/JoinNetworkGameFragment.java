package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.progress.JoinGameProgress;
import jsettlers.main.android.maplist.JoinableMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class JoinNetworkGameFragment extends MapSelectionFragment<IJoinableGame> {

	private IMultiplayerConnector connector;

	@Override
	protected MapListAdapter<IJoinableGame> generateListAdapter() {
		LayoutInflater inflater =
				(LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		connector = getJsettlersActivity()
				.generateMultiplayerConnector();
		return new JoinableMapListAdapter(inflater, connector);
	}

	@Override
	protected String getItemDescription(IJoinableGame item) {
		return String.format("map id: %s\nmatch id: %s", item.getMap().getId(),
				item.getId());
	}

	@Override
	protected boolean supportsDeletion() {
		return false;
	}

	@Override
	protected void deleteGame(IJoinableGame game) {
	}

	@Override
	protected void startGame(IJoinableGame game) {
		IJoiningGame joining = connector.joinMultiplayerGame(game);
		getJsettlersActivity().showFragment(new JoinGameProgress(joining));
	}

	@Override
	protected boolean supportsPlayerCount() {
		return false;
	}

	@Override
	protected int getSuggestedPlayerCount(IJoinableGame game) {
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
