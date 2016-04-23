/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.android.fragments;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.progress.StartGameProgess;
import jsettlers.main.android.maplist.MapDefinitionListAdapter;
import jsettlers.main.android.maplist.MapListAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class NewLocalGameFragment extends MapSelectionFragment<IMapDefinition> {

	@Override
	protected MapListAdapter<IMapDefinition> generateListAdapter() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return new MapDefinitionListAdapter<IMapDefinition>(inflater, getJsettlersActivity().getStartConnector().getSingleplayerMaps());
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
	protected boolean supportsPlayerCount() {
		return true;
	}

	@Override
	protected int getSuggestedPlayerCount(IMapDefinition game) {
		return game.getMaxPlayers();
	}

	@Override
	protected void deleteGame(IMapDefinition game) {
	}

	@Override
	protected void startGame(IMapDefinition game) {
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
