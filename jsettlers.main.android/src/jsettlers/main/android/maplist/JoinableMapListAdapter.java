package jsettlers.main.android.maplist;

import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import android.view.LayoutInflater;

public class JoinableMapListAdapter extends MapListAdapter<IJoinableGame> {

	public JoinableMapListAdapter(LayoutInflater inflater,
			IMultiplayerConnector networkConnector) {
		super(inflater, networkConnector.getJoinableMultiplayerGames());
	}

	@Override
	public String getTitle(IJoinableGame item) {
		return item.getName();
	}

	@Override
	protected short[] getImage(IJoinableGame item) {
		return item.getMap().getImage();
	}

	@Override
	protected String getDescriptionString(IJoinableGame item) {
		return "";
		// TODO: use current players here.
	}

}
