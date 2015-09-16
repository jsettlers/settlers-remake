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
package jsettlers.ai.army;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;

import java.util.List;

/**
 *
 *
 * @author codingberlin
 */
public class LooserGeneral implements ArmyGeneral {
	private final AiStatistics aiStatistics;
	private final byte playerId;

	private List<ShortPoint2D> bowmenPositions;
	private List<ShortPoint2D> spearmenPositions;
	private int amountOfMyAttackingTroops;


	public LooserGeneral(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
	}

	@Override public void commandTroops() {
		updateSituation();
		byte enemyToAttackId = determineEnemyToAttack();
		if (enemyToAttackId != -1) {
			//System.out.println("I (" + playerId + ") should attack " + enemyToAttackId);
			attack(enemyToAttackId);
		} else {
			//System.out.println("I (" + playerId + ") should attack nobody");
		}
	}

	private byte determineEnemyToAttack() {
		if (amountOfMyAttackingTroops < 30) {
			return -1;
		}

		List<Byte> enemies = aiStatistics.getEnemiesOf(playerId);
		if (enemies.size() == 0) {
			return -1;
		}

		for (Byte enemy : enemies) {
			int amountOfEnemyTroops = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L1, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L2, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L3, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L1, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L2, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L3, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L1, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L2, enemy).size();
			amountOfEnemyTroops += aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L3, enemy).size();
			if (amountOfMyAttackingTroops > amountOfEnemyTroops) {
				return enemy;
			}
		}
		return -1;
	}

	private void attack(byte enemyToAttackId) {

	}

	private void updateSituation() {
		bowmenPositions = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L1, playerId);
		bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L2, playerId));
		bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L3, playerId));
		spearmenPositions = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L1, playerId);
		spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L2, playerId));
		spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L3, playerId));
		amountOfMyAttackingTroops = bowmenPositions.size() + spearmenPositions.size();
	}
}
