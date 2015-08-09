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
		return String.format("map id: %s\nmatch id: %s", item.getMap().getMapId(),
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
