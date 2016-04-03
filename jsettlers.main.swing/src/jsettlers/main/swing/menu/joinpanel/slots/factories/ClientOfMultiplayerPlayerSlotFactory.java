/*******************************************************************************
 * Copyright (c) 2015, 2016
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.menu.joinpanel.slots.factories;

import jsettlers.common.ai.EPlayerType;
import jsettlers.logic.map.MapLoader;
import jsettlers.main.swing.menu.joinpanel.slots.PlayerSlot;

/**
 * @author codingberlin
 */
public class ClientOfMultiplayerPlayerSlotFactory implements IPlayerSlotFactory {

	@Override
	public PlayerSlot createPlayerSlot(byte slot, MapLoader mapLoader) {
		PlayerSlot playerSlot = new PlayerSlot();
		playerSlot.setPossibleTypes(new EPlayerType[] { EPlayerType.HUMAN, EPlayerType.AI_VERY_HARD });
		playerSlot.setSlotAndTeams((byte) mapLoader.getMaxPlayers());
		playerSlot.setReady(false);
		playerSlot.setReadyButtonEnabled(false);
		playerSlot.disableAllInputs();
		return playerSlot;
	}
}