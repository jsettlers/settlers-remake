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

import jsettlers.ai.construction.BuildingCount;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 *
 *
 * @author codingberlin
 */
public class LooserGeneral implements ArmyGeneral {
	private final AiStatistics aiStatistics;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final MovableGrid movableGrid;

	public LooserGeneral(AiStatistics aiStatistics, byte playerId, MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.taskScheduler = taskScheduler;
		this.movableGrid = movableGrid;
	}

	@Override public void commandTroops() {
		Situation situation = calculateSituation();
		AttackInformation attackInformation = determineAttackInformation(situation);
		if (attackInformation != null) {
			System.out.println("I (" + playerId + ") should attack " + attackInformation.targetPlayerId);
			attack(situation, attackInformation);
		} else {
			System.out.println("I (" + playerId + ") should attack nobody");
		}
	}

	private AttackInformation determineAttackInformation(Situation situation) {
		if (situation.amountOfMyAttackingTroops < 30) {
			return null;
		}

		List<Byte> enemies = aiStatistics.getEnemiesOf(playerId);
		if (enemies.size() == 0) {
			return null;
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
			if (situation.amountOfMyAttackingTroops > amountOfEnemyTroops) {
				Building towerToAttack = determineTowerToAttack(enemy);
				if (towerToAttack != null) {
					return new AttackInformation(enemy, amountOfEnemyTroops, towerToAttack);
				}
			}
		}
		return null;
	}

	private void attack(Situation situation, AttackInformation attackInformation) {
		byte numberOfSpearmen = (byte) Math.min(attackInformation.amountOfAttackers / 3, 20);
		List<ShortPoint2D> attackerPositions = aiStatistics.detectNearestPointsFromList(attackInformation.towerToAttack.getDoor(), situation.spearmenPositions, numberOfSpearmen);
		int numberOfBowmen = attackInformation.amountOfAttackers - attackerPositions.size();
		attackerPositions.addAll(aiStatistics.detectNearestPointsFromList(attackInformation.towerToAttack.getDoor(), situation.bowmenPositions, numberOfBowmen));

		List<Integer> attackerIds = new Vector<Integer>();
		for (ShortPoint2D attackerPosition: attackerPositions) {
			attackerIds.add(movableGrid.getMovableAt(attackerPosition.x, attackerPosition.y).getID());
		}

		taskScheduler.scheduleTask(new MoveToGuiTask(playerId, attackInformation.towerToAttack.getDoor(), attackerIds));
	}

	private Building determineTowerToAttack(byte enemyToAttackId) {
		List<ShortPoint2D> myMilitaryBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.TOWER, playerId);
		myMilitaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.BIG_TOWER, playerId));
		myMilitaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.CASTLE, playerId));
		ShortPoint2D myBaseAveragePoint = aiStatistics.calculateAveragePointFromList(myMilitaryBuildings);

		List<ShortPoint2D> enemyMilitaryBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.TOWER, enemyToAttackId);
		enemyMilitaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.BIG_TOWER, enemyToAttackId));
		enemyMilitaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.CASTLE, enemyToAttackId));

		if (enemyMilitaryBuildings.size() == 0) {
			return null;
		}

		return aiStatistics.getBuildingAt(aiStatistics.detectNearestPointFromList(myBaseAveragePoint, enemyMilitaryBuildings));
	}

	private Situation calculateSituation() {
		Situation situation = new Situation();
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L1, playerId));
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L2, playerId));
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L3, playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L1, playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L2, playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L3, playerId));
		situation.amountOfMyAttackingTroops = situation.bowmenPositions.size() + situation.spearmenPositions.size();

		return situation;
	}

	private class Situation {
		private List<ShortPoint2D> bowmenPositions = new Vector<ShortPoint2D>();
		private List<ShortPoint2D> spearmenPositions = new Vector<ShortPoint2D>();
		private int amountOfMyAttackingTroops = 0;
	}

	private class AttackInformation {
		private byte targetPlayerId;
		private int amountOfAttackers;
		private Building towerToAttack;

		public AttackInformation(byte targetPlayerId, int amountOfAttackers, Building towerToAttack) {
			this.targetPlayerId = targetPlayerId;
			this.amountOfAttackers = amountOfAttackers;
			this.towerToAttack = towerToAttack;
		}
	}

}
