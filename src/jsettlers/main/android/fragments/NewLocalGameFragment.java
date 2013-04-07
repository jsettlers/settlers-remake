package jsettlers.main.android.fragments;

import jsettlers.graphics.startscreen.GameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.main.android.R;
import jsettlers.main.android.maplist.FreshMapListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class NewLocalGameFragment extends MapSelectionFragment<IMapItem> {

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
	protected boolean supportsPlayerCount() {
		return true;
	}

	@Override
	protected int getSuggestedPlayerCount(IMapItem game) {
		return game.getMaxPlayers();
	}

	@Override
	protected void deleteGame(IMapItem game) {
	}

	@Override
	protected void startGame(IMapItem game) {
		int players = getPlayerCount();
		if (players < game.getMinPlayers()) {
			showText(R.string.illegal_playercount_too_low);
		} else if (players > game.getMaxPlayers()) {
			showText(R.string.illegal_playercount_too_high);
		} else {
			getJsettlersActivity().getStartConnector().startNewGame(
			        new GameSettings(game, 2));
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
