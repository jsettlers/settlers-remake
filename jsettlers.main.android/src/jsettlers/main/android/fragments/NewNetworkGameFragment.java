package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.progress.JoinGameProgress;
import jsettlers.main.android.maplist.MapDefinitionListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;

public class NewNetworkGameFragment extends MapSelectionFragment<IMapDefinition> {

	@Override
	protected MapListAdapter<IMapDefinition> generateListAdapter() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return new MapDefinitionListAdapter<IMapDefinition>(inflater, getJsettlersActivity()
				.getStartConnector().getMultiplayerMaps());
	}

	@Override
	protected String getItemDescription(IMapDefinition item) {
		return item.getDescription();
	}

	@Override
	protected boolean supportsDeletion() {
		return false;
	}

	@Override
	protected void deleteGame(IMapDefinition game) {
	}

	@Override
	protected void startGame(final IMapDefinition game) {
		// TODO: Allow user to select name in GUI.
		final String name = "Android network game";
		IJoiningGame joining = getJsettlersActivity().generateMultiplayerConnector().openNewMultiplayerGame(new IOpenMultiplayerGameInfo() {
			@Override
			public int getMaxPlayers() {
				return game.getMaxPlayers();
			}

			@Override
			public String getMatchName() {
				return name;
			}

			@Override
			public IMapDefinition getMapDefinition() {
				return game;
			}
		});
		getJsettlersActivity().showFragment(new JoinGameProgress(joining));
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
