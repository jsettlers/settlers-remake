package jsettlers.main.android.maplist;

import java.util.ArrayList;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.graphics.startscreen.INetworkConnector.INetworkListener;
import android.os.Handler;
import android.view.LayoutInflater;

public class JoinableMapListAdapter extends MapListAdapter<IMatch> implements
        INetworkListener {

	private final INetworkConnector networkConnector;

	private final ArrayList<IMatch> matches = new ArrayList<IMatch>();

	private final Handler handler;

	public JoinableMapListAdapter(LayoutInflater inflater,
	        INetworkConnector networkConnector) {
		super(inflater);
		handler = new Handler(inflater.getContext().getMainLooper());
		this.networkConnector = networkConnector;
		reloadGameList();
		networkConnector.setListener(this);
	}

	private void reloadGameList() {
		matches.clear();
		for (IMatch match : networkConnector.getMatches()) {
			if (match != null) {
				matches.add(match);
			}
		}
	}

	@Override
	public int getCount() {
		return matches.size();
	}

	@Override
	public String getTitle(int arg0) {
		return getItem(arg0).getMatchName();
	}

	@Override
	protected short[] getImage(int arg0) {
		return null;
	}

	@Override
	public IMatch getItem(int position) {
		return matches.get(position);
	}

	@Override
	protected String getDescriptionString(int mapn) {
		IMatch match = getItem(mapn);
		// TODO: use current players here.
		return String.format("? / %d", match.getMaxPlayers());
	}

	@Override
	public void matchListChanged(INetworkConnector connector) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				reloadGameList();
				notifyDataSetChanged();
			}
		});
	}

}
