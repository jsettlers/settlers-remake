package jsettlers.main.android;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkScreenListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

public class NetworkView implements INetworkScreenListener {

	private PlayerList playerList;
	private ChatAdapter chatAdapter;
	private EditText chatTextField;
	private final INetworkScreenAdapter networkScreen;
	private CheckBox acceptButton;
	private Button startButton;

	public NetworkView(View root, final INetworkScreenAdapter networkScreen) {
		this.networkScreen = networkScreen;
		networkScreen.setListener(this);
		playerList = new PlayerList(root.getContext(), networkScreen);
		chatAdapter = new ChatAdapter(root.getContext());
		
	    ListView playerListView = (ListView) root.findViewById(R.id.network_playerlist);
	    playerListView.setAdapter(playerList);
	    
	    ListView chatListView = (ListView) root.findViewById(R.id.network_chat);
	    chatListView.setAdapter(chatAdapter);
	    
	    chatTextField = (EditText) root.findViewById(R.id.network_chatmessage);
	    Button chatButton = (Button) root.findViewById(R.id.network_sendchat);
	    chatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendChatMessage();
			}
		});
	    
	    acceptButton = (CheckBox) root.findViewById(R.id.network_allowstart);
	    acceptButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				NetworkView.this.networkScreen.setStartAllowed(isChecked);
			}
		});
	    
	    startButton = (Button) root.findViewById(R.id.network_start);
	    startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				networkScreen.startGame();
			}
		});
	}

    protected void sendChatMessage() {
	    String message = chatTextField.getText().toString();
	    if (!message.isEmpty()) {
	    	networkScreen.sendChatMessage(message);
	    	
	    	chatTextField.setText("");
	    }
    }

	public void playerListChanged() {
	    playerList.changed();
    }

    public void addChatMessage(String message) {
	    chatAdapter.addChatMessage(message);
    }
}
