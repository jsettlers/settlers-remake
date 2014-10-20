package jsettlers.graphics.startscreen.joining;

import java.util.Collections;

import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.interfaces.ENetworkMessage;
import jsettlers.graphics.startscreen.interfaces.IChatMessageListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.graphics.utils.UIList;
import jsettlers.graphics.utils.UIListItem;

// TODO: List height/width for the Strings. Nice wrapping...
public class ChatList extends UIList<String> implements IChatMessageListener {

	private static class StringItem extends GenericListItem {
		public StringItem(String string) {
			super(string, "");
		}
	}

	public ChatList() {
		super(Collections.<String> emptyList(),
		        new ListItemGenerator<String>() {
			        @Override
			        public UIListItem getItem(String item) {
				        return new StringItem(item);
			        }
		        }, .05f);
	}

	private void addChatText(String text) {

	}

	// TODO: Use queue from Android.
	@Override
	public void chatMessageReceived(String authorId, String message) {
		addChatText(authorId + ": " + message);
	}

	@Override
	public void systemMessageReceived(IMultiplayerPlayer player,
	        ENetworkMessage message) {
		String playerName = player != null ? player.getName() : "";
		addChatText(playerName + ": " + message);
	}
}
