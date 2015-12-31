/**
 * ****************************************************************************
 * Copyright (c) 2015
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * *****************************************************************************
 */
package jsettlers.ai.army;

import java.util.List;
import java.util.Vector;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.SetMaterialProductionAction.EMaterialProductionType;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.SetMaterialProductionGuiTask;
import jsettlers.input.tasks.UpgradeSoldiersGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.player.Player;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * This general is named winner because his attacks and defence should be very hard for human enemies. This should be realized by creating locally
 * superiority. (You can kill 200 bowmen with just 100 bowmen if you fight 100 vs 20 in loops. This general should lay the focus on some swordsmen to
 * occupy own towers, 20 spearmen to defeat rushes and the rest only bowmen because in mass this is the strongest military unit. It upgrades bowmen
 * first because this is the main unit and the 20 defeating spearmen defeats with lv1 as well. This general should store bows until level3 is reached
 * to get as many level3 bowmen as posibble. TODO: store bows until level3 is reached TODO: group soldiers in direction of enemy groups to defeat them
 * TODO: group soldiers in direction of enemy groups to attack them TODO: introduce rush defency by early weaponsmith when enemy rushes
 *
 * @author codingberlin
 */
public class WinnerGeneral implements ArmyGeneral {
	private static final byte MIN_ATTACKER_SIZE = 20;
	private static final byte SWORDSMEN_BUFFER_TO_OCCUPY_MILITARY_BUILDINGS = 10;
	private static final byte RUSH_DEFENSE_SPEARMEN = 20;
	private static final byte MIN_NEAR_COMBAT_SOLDIERS = 10;

	private final AiStatistics aiStatistics;
	private final Player player;
	private final ITaskScheduler taskScheduler;
	private final MovableGrid movableGrid;

	public WinnerGeneral(AiStatistics aiStatistics, Player player, MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		this.aiStatistics = aiStatistics;
		this.player = player;
		this.taskScheduler = taskScheduler;
		this.movableGrid = movableGrid;
	}

	@Override
	public void commandTroops() {
		Situation situation = calculateSituation();
		if (aiStatistics.getEnemiesInTownOf(player.playerId).size() > 0) {
			defend(situation);
		} else {
			AttackInformation attackInformation = determineAttackInformation(situation);
			if (attackInformation != null) {
				attack(situation, attackInformation);
			}
		}
	}

	@Override
	public void levyUnits() {
		if (!upgradeSoldiers(ESoldierType.BOWMAN))
			if (!upgradeSoldiers(ESoldierType.PIKEMAN))
				upgradeSoldiers(ESoldierType.SWORDSMAN);
		int numberOfMissingSwordsmen = Math.max(0, SWORDSMEN_BUFFER_TO_OCCUPY_MILITARY_BUILDINGS
				- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L1, player.playerId).size()
				- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L2, player.playerId).size()
				- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L3, player.playerId).size());
		setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SWORD, numberOfMissingSwordsmen);
		if (numberOfMissingSwordsmen >= SWORDSMEN_BUFFER_TO_OCCUPY_MILITARY_BUILDINGS / 2) {
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, 0);
		} else {
			int numberOfMissingSpearmen = Math.max(0, RUSH_DEFENSE_SPEARMEN
					- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L1, player.playerId).size()
					- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L2, player.playerId).size()
					- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L3, player.playerId).size());
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, numberOfMissingSpearmen);
		}
		setRatioOfMaterial(player.playerId, EMaterialType.SWORD, 0f);
		setRatioOfMaterial(player.playerId, EMaterialType.SPEAR, 0f);
		setRatioOfMaterial(player.playerId, EMaterialType.BOW, 1f);
	}

	private void setNumberOfFutureProducedMaterial(byte playerId, EMaterialType materialType, int numberToProduce) {
		if (aiStatistics.getMaterialProduction(playerId).numberOfFutureProducedMaterial(materialType) != numberToProduce) {
			taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, aiStatistics.getPositionOfPartition(playerId), materialType,
					EMaterialProductionType.SET_PRODUCTION, numberToProduce));
		}
	}

	private void setRatioOfMaterial(byte playerId, EMaterialType materialType, float ratio) {
		if (aiStatistics.getMaterialProduction(playerId).configuredRatioOfMaterial(materialType) != ratio) {
			taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, aiStatistics.getPositionOfPartition(playerId), materialType,
					EMaterialProductionType.SET_RATIO, ratio));
		}
	}

	private boolean upgradeSoldiers(ESoldierType type) {
		if (player.getManaInformation().isUpgradePossible(type)) {
			taskScheduler.scheduleTask(new UpgradeSoldiersGuiTask(player.playerId, type));
			return true;
		}
		return false;
	}

	private void defend(Situation situation) {
		List<ShortPoint2D> allMyTroops = new Vector<ShortPoint2D>();
		allMyTroops.addAll(situation.bowmenPositions);
		allMyTroops.addAll(situation.spearmenPositions);
		allMyTroops.addAll(situation.swordsmenPositions);
		sendTroopsTo(allMyTroops, aiStatistics.getEnemiesInTownOf(player.playerId).iterator().next());
	}

	private AttackInformation determineAttackInformation(Situation situation) {
		if (situation.amountOfMyAttackingTroops < MIN_ATTACKER_SIZE) {
			return null;
		}

		List<Byte> enemies = aiStatistics.getEnemiesOf(player.playerId);
		if (enemies.size() == 0) {
			return null;
		}

		AttackInformation resultAttackInformation = null;
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
			if (situation.amountOfMyAttackingTroops > amountOfEnemyTroops
					&& (resultAttackInformation == null || resultAttackInformation.amountOfAttackers < amountOfEnemyTroops)) {
				Building militaryBuildingToAttack = determineMilitaryBuildingToAttack(enemy);
				if (militaryBuildingToAttack != null) {
					resultAttackInformation = new AttackInformation(enemy, Math.max(amountOfEnemyTroops, 10), militaryBuildingToAttack);
				}
			}
		}
		return resultAttackInformation;
	}

	private void attack(Situation situation, AttackInformation attackInformation) {
		List<ShortPoint2D> attackerPositions = new Vector<ShortPoint2D>();
		int numberOfBowmen = Math.min(attackInformation.amountOfAttackers - MIN_NEAR_COMBAT_SOLDIERS, situation.bowmenPositions.size());
		attackerPositions.addAll(AiStatistics
				.detectNearestPointsFromList(attackInformation.militaryBuildingToAttack.getDoor(), situation.bowmenPositions, numberOfBowmen));
		int numberOfSpearmen = attackInformation.amountOfAttackers - attackerPositions.size();
		attackerPositions.addAll(AiStatistics
				.detectNearestPointsFromList(attackInformation.militaryBuildingToAttack.getDoor(), situation.spearmenPositions, numberOfSpearmen));
		int numberOfSwordsmen = attackInformation.amountOfAttackers - attackerPositions.size();
		attackerPositions.addAll(AiStatistics
				.detectNearestPointsFromList(attackInformation.militaryBuildingToAttack.getDoor(), situation.swordsmenPositions, numberOfSwordsmen));

		sendTroopsTo(attackerPositions, attackInformation.militaryBuildingToAttack.getDoor());
	}

	private void sendTroopsTo(List<ShortPoint2D> attackerPositions, ShortPoint2D target) {
		List<Integer> attackerIds = new Vector<Integer>();
		for (ShortPoint2D attackerPosition : attackerPositions) {
			attackerIds.add(movableGrid.getMovableAt(attackerPosition.x, attackerPosition.y).getID());
		}

		taskScheduler.scheduleTask(new MoveToGuiTask(player.playerId, target, attackerIds));
	}

	private Building determineMilitaryBuildingToAttack(byte enemyToAttackId) {
		List<ShortPoint2D> myMilitaryBuildings =
				aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.getMilitaryBuildings(), player.playerId);
		ShortPoint2D myBaseAveragePoint = aiStatistics.calculateAveragePointFromList(myMilitaryBuildings);

		List<ShortPoint2D> enemyMilitaryBuildings
				= aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.getMilitaryBuildings(), enemyToAttackId);

		if (enemyMilitaryBuildings.size() == 0) {
			return null;
		}

		return aiStatistics.getBuildingAt(AiStatistics.detectNearestPointFromList(myBaseAveragePoint, enemyMilitaryBuildings));
	}

	private Situation calculateSituation() {
		Situation situation = new Situation();
		situation.swordsmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L1, player.playerId));
		situation.swordsmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L2, player.playerId));
		situation.swordsmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.SWORDSMAN_L3, player.playerId));
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L1, player.playerId));
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L2, player.playerId));
		situation.bowmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BOWMAN_L3, player.playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L1, player.playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L2, player.playerId));
		situation.spearmenPositions.addAll(aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.PIKEMAN_L3, player.playerId));
		situation.amountOfMyAttackingTroops = Math.max(situation.swordsmenPositions.size() - SWORDSMEN_BUFFER_TO_OCCUPY_MILITARY_BUILDINGS, 0)
				+ situation.bowmenPositions.size()
				+ situation.spearmenPositions.size();

		return situation;
	}

	private class Situation {
		private List<ShortPoint2D> swordsmenPositions = new Vector<ShortPoint2D>();
		private List<ShortPoint2D> bowmenPositions = new Vector<ShortPoint2D>();
		private List<ShortPoint2D> spearmenPositions = new Vector<ShortPoint2D>();
		private int amountOfMyAttackingTroops = 0;
	}

	private class AttackInformation {
		// private byte targetPlayerId;
		private int amountOfAttackers;
		private Building militaryBuildingToAttack;

		public AttackInformation(byte targetPlayerId, int amountOfAttackers, Building militaryBuildingToAttack) {
			// this.targetPlayerId = targetPlayerId;
			this.amountOfAttackers = amountOfAttackers;
			this.militaryBuildingToAttack = militaryBuildingToAttack;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
