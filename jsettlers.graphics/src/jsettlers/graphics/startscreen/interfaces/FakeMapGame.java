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
package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.player.IManaInformation;
import jsettlers.common.statistics.IStatisticable;

/**
 * This is a simple implementation of {@link IStartedGame} that allows you to supply a map as a game.
 * 
 * @author michael
 */
public class FakeMapGame implements IStartedGame {

	public final class NullStatistics implements IStatisticable {
		@Override
		public int getGameTime() {
			return 0;
		}

		@Override
		public int getNumberOf(EMovableType movableType) {
			return 0;
		}

		@Override
		public int getNumberOf(EMaterialType materialType) {
			return 0;
		}

		@Override
		public int getJoblessBearers() {
			return 0;
		}
	}

	private final IGraphicsGrid map;

	public FakeMapGame(IGraphicsGrid map) {
		this.map = map;
	}

	@Override
	public IGraphicsGrid getMap() {
		return map;
	}

	@Override
	public IStatisticable getPlayerStatistics() {
		return new NullStatistics();
	}

	@Override
	public IInGamePlayer getInGamePlayer() {
		return new IInGamePlayer() {
			@Override
			public IManaInformation getManaInformation() {
				return new IManaInformation() {
					@Override
					public void upgrade(ESoldierType type) {
					}

					@Override
					public void stopFutureManaIncreasingByBigTemple() {
					}

					@Override
					public boolean isUpgradePossible(ESoldierType type) {
						return false;
					}

					@Override
					public void increaseManaByBigTemple() {
					}

					@Override
					public void increaseMana() {
					}

					@Override
					public byte getNextUpdateProgressPercent() {
						return 0;
					}

					@Override
					public EMovableType getMovableTypeOf(ESoldierType type) {
						return EMovableType.SWORDSMAN_L1;
					}

					@Override
					public byte getMaximumLevel() {
						return 0;
					}

					@Override
					public byte getLevel(ESoldierType type) {
						return 0;
					}
				};
			}

			@Override
			public ICombatStrengthInformation getCombatStrengthInformation() {
				return new ICombatStrengthInformation() {
					@Override
					public float getCombatStrength(boolean ownLand) {
						return 0;
					}
				};
			}
		};
	}

	@Override
	public void setGameExitListener(IGameExitListener exitListener) {
	}

}
