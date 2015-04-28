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

import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerListener;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.main.android.ChatAdapter;
import jsettlers.main.android.PlayerList;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.progress.StartGameProgess;
import android.annotation.SuppressLint;
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

@SuppressLint("ValidFragment")
public class JoinPhaseFragment extends JsettlersFragment implements
		IMultiplayerListener {

	private PlayerList playerList;
	private ChatAdapter chatAdapter;
	private final IJoinPhaseMultiplayerGameConnector connector;

	@SuppressLint("ValidFragment")
	public JoinPhaseFragment(IJoinPhaseMultiplayerGameConnector connector) {
		this.connector = connector;
		connector.setMultiplayerListener(this);
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

		loadPlayerList(root);

		loadChat(root);

		CheckBox acceptButton = (CheckBox) root
				.findViewById(R.id.network_allowstart);
		acceptButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				connector.setReady(isChecked);
			}
		});

		Button startButton = (Button) root.findViewById(R.id.network_start);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				connector.startGame();
			}
		});
	}

	private void loadChat(View root) {
		chatAdapter = new ChatAdapter(getActivity().getLayoutInflater(),
				connector);
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
		ListView playerListView = (ListView) root
				.findViewById(R.id.network_playerlist);
		playerList = new PlayerList(getActivity(), connector.getPlayers());
		playerListView.setAdapter(playerList);
	}

	private void sendChatMessage() {
		EditText chatTextField = (EditText) getView().findViewById(
				R.id.network_chatmessage);
		String message = chatTextField.getText().toString();
		if (!message.isEmpty()) {
			connector.sendChatMessage(message);

			chatTextField.setText("");
		}
	}

	@Override
	public boolean onBackButtonPressed() {
		connector.abort();
		getJsettlersActivity().showStartScreen();
		return true;
	}

	@Override
	public void gameIsStarting(IStartingGame game) {
		getJsettlersActivity().showFragment(new StartGameProgess(game));
	}

	@Override
	public void gameAborted() {
		// TODO Error message
		getJsettlersActivity().showStartScreen();
	};
}
