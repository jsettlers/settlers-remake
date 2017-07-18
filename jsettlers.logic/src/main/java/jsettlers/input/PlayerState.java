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
package jsettlers.input;

import java.io.Serializable;

import jsettlers.algorithms.fogofwar.FogOfWar;
import jsettlers.common.menu.UIState;

/**
 *
 * @author Andreas Eberle
 *
 */
public class PlayerState implements Serializable {
	private static final long serialVersionUID = -1077800774789265575L;

	private final byte playerId;
	private final UIState uiState;
	private final FogOfWar fogOfWar;

	public PlayerState(byte playerId, UIState uiState, FogOfWar fogOfWar) {
		this.playerId = playerId;
		this.uiState = uiState;
		this.fogOfWar = fogOfWar;
	}

	public PlayerState(byte playerId, UIState uiState) {
		this(playerId, uiState, null);
	}

	public byte getPlayerId() {
		return playerId;
	}

	public UIState getUiState() {
		return uiState;
	}

	public FogOfWar getFogOfWar() {
		return fogOfWar;
	}
}
