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
package jsettlers.main.android;

import java.util.UUID;

import jsettlers.common.CommonConstants;

import android.content.Context;
import android.content.SharedPreferences;

public class AndroidPreferences {

	private final SharedPreferences preferences;

	public AndroidPreferences(Context context) {
		this.preferences = context.getSharedPreferences("prefs", 0);
	}

	public boolean hasMissingMultiplayerPreferences() {
		return getPlayerName().isEmpty() || getServer().isEmpty();
	}

	public String getPlayerName() {
		return preferences.getString("player-name", "");
	}

	public String getPlayerId() {
		String id = preferences.getString("player-id", "");
		if (id.isEmpty()) {
			id = UUID.randomUUID().toString();
			preferences.edit().putString("player-id", id).commit();
		}
		return id;
	}

	public String getServer() {
		return preferences.getString("server", CommonConstants.DEFAULT_SERVER_ADDRESS);
	}

	public void setPlayerName(String name) {
		preferences.edit().putString("player-name", name).commit();
	}

	public void setServer(String serverName) {
		preferences.edit().putString("server", serverName).commit();
	}
}
