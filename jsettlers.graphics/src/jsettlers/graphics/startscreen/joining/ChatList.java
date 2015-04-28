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
