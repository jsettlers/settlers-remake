/*******************************************************************************
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
 *******************************************************************************/

package jsettlers.logic.player;

import static java8.util.stream.StreamSupport.stream;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.ISettlerInformation;
import jsettlers.logic.movable.Movable;

class SettlerInformation implements ISettlerInformation {

	private final int[] movables = new int[EMovableType.NUMBER_OF_MOVABLETYPES];

	SettlerInformation(byte playerId) {
		stream(Movable.getAllMovables())
				.filter(movable -> movable.getPlayer().getPlayerId() == playerId)
				.forEach(movable -> {
					int movableTypeIndex = movable.getMovableType().ordinal();
					movables[movableTypeIndex]++;
				});
	}

	@Override
	public int getMovableCount(EMovableType type) {
		return movables[type.ordinal()];
	}
}
