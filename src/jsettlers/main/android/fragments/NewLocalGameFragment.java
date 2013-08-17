package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.progress.StartGameProgess;
import jsettlers.main.android.maplist.MapDefinitionListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class NewLocalGameFragment extends MapSelectionFragment<IStartableMapDefinition> {

	@Override
	protected MapListAdapter<IStartableMapDefinition> generateListAdapter() {
		LayoutInflater inflater =
		        (LayoutInflater) getActivity().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		return new MapDefinitionListAdapter<IStartableMapDefinition>(inflater, getJsettlersActivity()
		        .getStartConnector().getSingleplayerMaps());
	}

	@Override
	protected String getItemDescription(IStartableMapDefinition item) {
		return item.getDescription();
	}

	@Override
	protected boolean supportsDeletion() {
		return false;
	}

	@Override
	protected boolean supportsPlayerCount() {
		return true;
	}

	@Override
	protected int getSuggestedPlayerCount(IStartableMapDefinition game) {
		return game.getMaxPlayers();
	}

	@Override
	protected void deleteGame(IStartableMapDefinition game) {
	}

	@Override
	protected void startGame(IStartableMapDefinition game) {
		int players = getPlayerCount();
		if (players < game.getMinPlayers()) {
			showText(R.string.illegal_playercount_too_low);
		} else if (players > game.getMaxPlayers()) {
			showText(R.string.illegal_playercount_too_high);
		} else {
			IStartingGame started = getJsettlersActivity().getStartConnector().startSingleplayerGame(game);
			getJsettlersActivity().showFragment(new StartGameProgess(started));
		}
	}

	private void showText(int id) {
		Toast text = Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT);
		text.show();
	}

	@Override
	public String getName() {
		return "new-local";
	}

	@Override
	protected int getHeadlineText() {
		return R.string.maplist_local_new_headline;
	}

	@Override
	protected int getStartButtonText() {
		return R.string.maplist_local_new_submit;
	}

}
