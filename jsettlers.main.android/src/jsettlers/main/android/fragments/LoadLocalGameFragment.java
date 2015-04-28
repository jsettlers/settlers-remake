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
