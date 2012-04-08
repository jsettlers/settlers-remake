package jsettlers.main.android.maplist;

import java.util.List;

import jsettlers.graphics.startscreen.GameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.graphics.startscreen.NetworkGameSettings;
import jsettlers.main.android.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class manages the map list.
 * 
 * @author michael
 */
public class MapList {
	private static final String TEST_IP_ADDR = "";

	public static final int STARTMODE_SINGLE = 1;
	public static final int STARTMODE_MULTIPLAYER = 2;
	public static final int STARTMODE_LOAD_SINGLE = 3;
	public static final int STARTMODE_LOAD_MULTIPLAYER = 4;

	private final TextView description;
	private final TextView name;
	private IMapItem selectedMap;
	private final int startmode;
	private final IStartScreenConnector connector;
	private Button startButton;
	private EditText playerField;

	private ILoadableGame selectedLoadableMap;

	public MapList(View mainView, IStartScreenConnector connector,
	        int startmode, LayoutInflater inflater) {
		this.connector = connector;
		this.startmode = startmode;
		name = (TextView) mainView.findViewById(R.id.maplist_name);
		description =
		        (TextView) mainView.findViewById(R.id.maplist_description);

		initializeGUI(mainView, inflater);

		clearMapFields();
	}

	private void initializeGUI(View mainView, LayoutInflater inflater) {
		ListAdapter adapter;
		if (startmode == STARTMODE_LOAD_SINGLE) {
			List<? extends ILoadableGame> maps;
			maps = connector.getLoadableGames();
			adapter = new LoadableMapListAdapter(inflater, maps);
		} else if (startmode == STARTMODE_LOAD_MULTIPLAYER) {
			adapter = null; // TODO: load network games
		} else {
			List<? extends IMapItem> maps = connector.getMaps();
			adapter = new FreshMapListAdapter(inflater, maps);
		}

		ListView list = (ListView) mainView.findViewById(R.id.maplist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemid,
			        long arg3) {
				if (startmode == STARTMODE_LOAD_SINGLE) {
					LoadableMapListAdapter adapter =
					        (LoadableMapListAdapter) arg0.getAdapter();
					selectLoadableMap(adapter.getItem(itemid));
				} else if (startmode == STARTMODE_LOAD_MULTIPLAYER) {
					// TODO: load network games
				} else {
					FreshMapListAdapter adapter =
					        (FreshMapListAdapter) arg0.getAdapter();
					selectFreshMap(adapter.getItem(itemid));
				}
			}
		});

		startButton = (Button) mainView.findViewById(R.id.maplist_startbutton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tryStartGame();
			}
		});

		playerField = (EditText) mainView.findViewById(R.id.maplist_players);
		if (startmode == STARTMODE_LOAD_MULTIPLAYER
		        || startmode == STARTMODE_LOAD_SINGLE) {
			playerField.setVisibility(View.GONE);
		}
	}

	protected void selectLoadableMap(ILoadableGame item) {
		this.selectedLoadableMap = item;
		if (selectedLoadableMap != null) {
			name.setText(item.getName());
			// description.setText(item.getDescription());
			startButton.setEnabled(true);

		} else {
			clearMapFields();
		}
	}

	protected void selectFreshMap(IMapItem iMapItem) {
		this.selectedMap = iMapItem;
		if (selectedMap != null) {
			name.setText(iMapItem.getName());
			description.setText(iMapItem.getDescription());
			startButton.setEnabled(true);

			int players;
			try {
				players = Integer.parseInt(playerField.getText().toString());
			} catch (NumberFormatException e) {
				players = 0;
			}
			if (players < selectedMap.getMinPlayers()) {
				playerField.setText(selectedMap.getMinPlayers() + "");
			} else if (players > selectedMap.getMaxPlayers()) {
				playerField.setText(selectedMap.getMaxPlayers() + "");
			}

		} else {
			clearMapFields();
		}
	}

	private void clearMapFields() {
		name.setText("");
		description.setText("");
		startButton.setEnabled(false);
	}

	protected void tryStartGame() {
		if (startmode == STARTMODE_LOAD_SINGLE) {
			loadSingleGame();
		} else if (startmode == STARTMODE_LOAD_MULTIPLAYER) {

		} else {
			startNewGame();
		}
	}

	private void loadSingleGame() {
		if (selectedLoadableMap != null) {
			connector.loadGame(selectedLoadableMap);
		}
	}

	private void startNewGame() {
		if (selectedMap != null) {
			try {
				int players =
				        Integer.parseInt(playerField.getText().toString());
				if (players < selectedMap.getMinPlayers()) {
					showText(R.string.illegal_playercount_too_low);
				} else if (players > selectedMap.getMaxPlayers()) {
					showText(R.string.illegal_playercount_too_high);
				} else {
					if (startmode == STARTMODE_MULTIPLAYER) {
						connector.startNetworkGame(new NetworkGameSettings(
						        selectedMap, "android network game", players,
						        TEST_IP_ADDR));
					} else {
						connector.startNewGame(new GameSettings(selectedMap,
						        players));
					}
				}
			} catch (NumberFormatException e) {
				showText(R.string.illegal_playercount);
			}
		}
	}

	private void showText(int id) {
		Toast text =
		        Toast.makeText(playerField.getContext(), id, Toast.LENGTH_SHORT);
		text.show();
	}

}
