/*
 * Copyright (c) 2017
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
 */

package jsettlers.common.player;

/**
 * Created by Andreas Eberle on 27.06.2017.
 */
public interface IPlayer {
	/**
	 * The id of the player. It is unique in this game
	 * @return The id as byte
	 */
	byte getPlayerId();

	byte getTeamId();

	class DummyPlayer implements IPlayer {
		private final byte playerAndTeamid;

		public DummyPlayer() {
			this.playerAndTeamid = 0;
		}

		public DummyPlayer(byte playerAndTeamid) {
			this.playerAndTeamid = playerAndTeamid;
		}

		@Override
		public byte getPlayerId() {
			return playerAndTeamid;
		}

		@Override
		public byte getTeamId() {
			return playerAndTeamid;
		}
	}
}
