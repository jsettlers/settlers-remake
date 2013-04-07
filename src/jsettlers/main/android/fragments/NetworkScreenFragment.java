package jsettlers.main.android.fragments;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkScreenListener;
import jsettlers.main.android.ChatAdapter;
import jsettlers.main.android.PlayerList;
import jsettlers.main.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

public class NetworkScreenFragment extends JsettlersFragment implements
        INetworkScreenListener {

	private INetworkScreenAdapter networkScreen;
	private PlayerList playerList;
	private ChatAdapter chatAdapter;

	public NetworkScreenFragment(INetworkScreenAdapter networkScreen) {
		this.networkScreen = networkScreen;
	}

	@Override
	public String getName() {
		return "net-start";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		return inflater.inflate(R.layout.networkinit, container, false);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		networkScreen.setListener(this);

		loadPlayerList(root);

		loadChat(root);

		CheckBox acceptButton =
		        (CheckBox) root.findViewById(R.id.network_allowstart);
		acceptButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
			        boolean isChecked) {
				networkScreen.setReady(isChecked);
			}
		});

		Button startButton = (Button) root.findViewById(R.id.network_start);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				networkScreen.startNetworkMatch();
			}
		});
	}

	private void loadChat(View root) {
		chatAdapter = new ChatAdapter(getActivity());
		ListView chatListView = (ListView) root.findViewById(R.id.network_chat);
		chatListView.setAdapter(chatAdapter);

		Button chatButton = (Button) root.findViewById(R.id.network_sendchat);
		chatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendChatMessage();
			}
		});
	}

	private void loadPlayerList(View root) {
		ListView playerListView =
		        (ListView) root.findViewById(R.id.network_playerlist);
		playerList = new PlayerList(playerListView, networkScreen);
		playerListView.setAdapter(playerList);
	}

	private void sendChatMessage() {
		EditText chatTextField =
		        (EditText) getView().findViewById(R.id.network_chatmessage);
		String message = chatTextField.getText().toString();
		if (!message.isEmpty()) {
			networkScreen.sendChatMessage(message);

			chatTextField.setText("");
		}
	}

	@Override
	public void playerListChanged() {
		playerList.changed();
	}

	@Override
	public void addChatMessage(String message) {
		chatAdapter.addChatMessage(message);
	}
}
