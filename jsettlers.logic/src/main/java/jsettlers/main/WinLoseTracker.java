/*******************************************************************************
 * Copyright (c) 2019
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
package jsettlers.main;


import java8.util.Objects;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.player.EWinState;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.partition.data.BuildingCounts;
import jsettlers.logic.player.Player;
import jsettlers.network.synchronic.timer.INetworkTimerable;

import static java8.util.J8Arrays.stream;

class WinLoseTracker implements INetworkTimerable {

	private MainGrid mainGrid;
	private byte     localPlayerId;

	public WinLoseTracker(MainGrid mainGrid, byte localPlayerId) {
		this.mainGrid = mainGrid;
		this.localPlayerId = localPlayerId;
	}

	public void timerEvent() {
		// Update defeated status
		Player[] players = mainGrid.getPartitionsGrid().getPlayers();

		for (Player player : players) {
			if (player == null || player.getWinState() != EWinState.UNDECIDED) {
				continue;
			}

			// Get all buildings in any partition and count military ones
			BuildingCounts buildingCounts = new BuildingCounts(player.playerId, (short) 0);
			int militaryBuildingsCount = buildingCounts.buildings(EBuildingType.TOWER) + buildingCounts.buildings(EBuildingType.BIG_TOWER) + buildingCounts.buildings(EBuildingType.CASTLE);

			if (militaryBuildingsCount == 0) {
				player.setWinState(EWinState.LOST);
				System.out.println(player + " was defeated");
				disableFogOfWarIfLocalPlayer(player);
			}
		}

		// Check if any player won by defeating other players
		for (Player player : players) {
			if (player == null || player.getWinState() != EWinState.UNDECIDED) {
				continue;
			}

			boolean allEnemiesDefeated = stream(players).filter(Objects::nonNull).filter(otherPlayer -> !otherPlayer.hasSameTeam(player)).noneMatch(enemy -> enemy.getWinState() != EWinState.LOST);

			if (allEnemiesDefeated) {
				player.setWinState(EWinState.WON);
				System.out.println(player + " has won the game");
				disableFogOfWarIfLocalPlayer(player);
			}
		}
	}

	private void disableFogOfWarIfLocalPlayer(Player player) {
		// Disable fog of war if local player has won/lost game
		if (player.playerId == localPlayerId) {
			mainGrid.disableFogOfWar();
		}
	}
}
