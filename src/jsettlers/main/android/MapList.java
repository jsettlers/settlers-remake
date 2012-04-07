package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.startscreen.GameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.graphics.startscreen.NetworkGameSettings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	
	private final TextView description;
	private final TextView name;
	private IMapItem selectedMap;
	private final boolean requestNetworkStart;
	private final IStartScreenConnector connector;
	private Button startButton;
	private EditText playerField;

	public MapList(View mainView, IStartScreenConnector connector,
	        boolean requestNetworkStart, LayoutInflater inflater) {
		this.connector = connector;
		this.requestNetworkStart = requestNetworkStart;
		name = (TextView) mainView.findViewById(R.id.maplist_name);
		description =
		        (TextView) mainView.findViewById(R.id.maplist_description);

		List<? extends IMapItem> maps = connector.getMaps();
		ListView list = (ListView) mainView.findViewById(R.id.maplist);
		list.setAdapter(new MapListAdapter(inflater, maps));
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemid,
			        long arg3) {
				MapListAdapter adapter = (MapListAdapter) arg0.getAdapter();
				selectMap(adapter.get(itemid));
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

		selectMap(null);
	}

	protected void selectMap(IMapItem iMapItem) {
		this.selectedMap = iMapItem;
		if (selectedMap != null) {
			name.setText(iMapItem.getName());
			description.setText(iMapItem.getDescription());
			startButton.setEnabled(true);

			int players;
			try {
				players = Integer.parseInt(playerField.getText().toString());
			}catch (NumberFormatException e) {
				players = 0;
			}
			if (players < selectedMap.getMinPlayers()) {
				playerField.setText(selectedMap.getMinPlayers() + "");
			} else if (players > selectedMap.getMaxPlayers()) {
				playerField.setText(selectedMap.getMaxPlayers() + "");
			}
			
		} else {
			name.setText("");
			description.setText("");
			startButton.setEnabled(false);
		}
	}

	protected void tryStartGame() {
		if (selectedMap != null) {
			try {
				int players =
				        Integer.parseInt(playerField.getText().toString());
				if (players < selectedMap.getMinPlayers()) {
					showText(R.string.illegal_playercount_too_low);
				} else if (players > selectedMap.getMaxPlayers()) {
					showText(R.string.illegal_playercount_too_high);
				} else {
					if (requestNetworkStart) {
						connector.startNetworkGame(new NetworkGameSettings(
						        selectedMap, "android network game", players, TEST_IP_ADDR));
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
		Toast text = Toast.makeText(playerField.getContext(), id, Toast.LENGTH_SHORT);
		text.show();
	}

}
