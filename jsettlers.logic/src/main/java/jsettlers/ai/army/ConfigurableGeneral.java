/*******************************************************************************
 * Copyright (c) 2016 - 2018
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.action.SetMaterialProductionAction.EMaterialProductionType;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ChangeTowerSoldiersGuiTask;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.SetMaterialProductionGuiTask;
import jsettlers.input.tasks.UpgradeSoldiersGuiTask;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.player.Player;
import jsettlers.network.client.interfaces.ITaskScheduler;

import static java8.util.stream.StreamSupport.stream;

/**
 * This general is named winner because his attacks and defence should be very hard for human enemies. This should be realized by creating locally superiority. (You can kill 200 BOWMEN with just 100
 * BOWMEN if you fight 100 vs 20 in loops. This general should lay the focus on some SWORDSMEN to occupy own towers, 20 spearmen to defeat rushes and the rest only BOWMEN because in mass this is the
 * strongest military unit. It upgrades BOWMEN first because this is the main unit and the 20 defeating spearmen defeats with lv1 as well. This general should store bows until level3 is reached to get
 * as many level3 BOWMEN as posibble. TODO: store bows until level3 is reached TODO: group SOLDIERS in direction of enemy groups to defeat them TODO: group SOLDIERS in direction of enemy groups to
 * attack them
 *
 * @author codingberlin
 */
public class ConfigurableGeneral implements ArmyGeneral {

	private static float[] ATTACKER_COUNT_FACTOR_BY_PLAYER_TYPE = { 1.1F, 1F, 0.9F, 0.8F, 0F };

	private static final byte MIN_ATTACKER_COUNT = 20;
	private static final byte MIN_SWORDSMEN_COUNT = 10;
	private static final byte MIN_PIKEMEN_COUNT = 20;
	private static final int BOWMEN_COUNT_OF_KILLING_INFANTRY = 300;
	private static final EBuildingType[] MIN_BUILDING_REQUIREMENTS_FOR_ATTACK = { EBuildingType.COALMINE, EBuildingType.IRONMINE, EBuildingType.IRONMELT, EBuildingType.WEAPONSMITH,
			EBuildingType.BARRACK };
	private static final ESoldierType[] SOLDIER_UPGRADE_ORDER = new ESoldierType[] { ESoldierType.BOWMAN, ESoldierType.PIKEMAN, ESoldierType.SWORDSMAN };

	private final AiStatistics aiStatistics;
	private final Player player;
	private final ITaskScheduler taskScheduler;
	private final MovableGrid movableGrid;
	private final float attackerCountFactor;

	public ConfigurableGeneral(AiStatistics aiStatistics, Player player, MovableGrid movableGrid, ITaskScheduler taskScheduler, EPlayerType playerType) {
		this.aiStatistics = aiStatistics;
		this.player = player;
		this.taskScheduler = taskScheduler;
		this.movableGrid = movableGrid;
		this.attackerCountFactor = ATTACKER_COUNT_FACTOR_BY_PLAYER_TYPE[playerType.ordinal()];
	}

	@Override
	public void commandTroops(Set<Integer> soldiersWithOrders) {
		ensureAllTowersFullyMounted();

		SoldierPositions soldierPositions = calculateSituation(player.playerId);
		if (aiStatistics.getEnemiesInTownOf(player.playerId).size() > 0) {
			defend(soldierPositions, soldiersWithOrders);
		} else if (existsAliveEnemy()) {
			IPlayer weakestEnemy = getWeakestEnemy();
			SoldierPositions enemySoldierPositions = calculateSituation(weakestEnemy.getPlayerId());
			boolean infantryWouldDie = wouldInfantryDie(enemySoldierPositions);
			if (attackIsPossible(soldierPositions, enemySoldierPositions, infantryWouldDie)) {
				attack(soldierPositions, infantryWouldDie, soldiersWithOrders);
			}
		}
	}

	private void ensureAllTowersFullyMounted() {
		stream(aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.MILITARY_BUILDINGS, player.playerId))
				.map(aiStatistics::getBuildingAt)
				.filter(building -> building instanceof OccupyingBuilding)
				.map(building -> (OccupyingBuilding) building)
				.filter(building -> !building.isSetToBeFullyOccupied())
				.forEach(building -> taskScheduler.scheduleTask(new ChangeTowerSoldiersGuiTask(player.playerId, building.getPosition(), ChangeTowerSoldiersGuiTask.EChangeTowerSoldierTaskType.FULL, null)));
	}

	private boolean attackIsPossible(SoldierPositions soldierPositions, SoldierPositions enemySoldierPositions, boolean infantryWouldDie) {
		for (EBuildingType requiredType : MIN_BUILDING_REQUIREMENTS_FOR_ATTACK) {
			if (aiStatistics.getNumberOfBuildingTypeForPlayer(requiredType, player.playerId) < 1) {
				return false;
			}
		}

		float combatStrength = player.getCombatStrengthInformation().getCombatStrength(false);
		float effectiveAttackerCount;
		if (infantryWouldDie) {
			effectiveAttackerCount = soldierPositions.bowmenPositions.size() * combatStrength;
		} else {
			effectiveAttackerCount = soldierPositions.getSoldiersCount() * combatStrength;
		}
		return effectiveAttackerCount >= MIN_ATTACKER_COUNT && effectiveAttackerCount * attackerCountFactor > enemySoldierPositions.getSoldiersCount();

	}

	private boolean wouldInfantryDie(SoldierPositions enemySoldierPositions) {
		return enemySoldierPositions.bowmenPositions.size() > BOWMEN_COUNT_OF_KILLING_INFANTRY;
	}

	private boolean existsAliveEnemy() {
		return !aiStatistics.getAliveEnemiesOf(player).isEmpty();
	}

	@Override
	public void levyUnits() {
		upgradeSoldiers();

		int missingSwordsmenCount = Math.max(0, MIN_SWORDSMEN_COUNT - aiStatistics.getCountOfMovablesOfPlayer(player, EMovableType.SWORDSMEN));
		int missingPikemenCount = Math.max(0, MIN_PIKEMEN_COUNT - aiStatistics.getCountOfMovablesOfPlayer(player, EMovableType.PIKEMEN));
		int bowmenCount = aiStatistics.getCountOfMovablesOfPlayer(player, EMovableType.BOWMEN);

		if (missingSwordsmenCount > 0) {
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SWORD, missingSwordsmenCount);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.BOW, 0);
			setRatioOfMaterial(player.playerId, EMaterialType.SWORD, 0F);
			setRatioOfMaterial(player.playerId, EMaterialType.SPEAR, 1F);
			setRatioOfMaterial(player.playerId, EMaterialType.BOW, 0F);
		} else if (missingPikemenCount > 0) {
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SWORD, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, missingPikemenCount);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.BOW, 0);
			setRatioOfMaterial(player.playerId, EMaterialType.SWORD, 0F);
			setRatioOfMaterial(player.playerId, EMaterialType.SPEAR, 0.3F);
			setRatioOfMaterial(player.playerId, EMaterialType.BOW, 1F);
		} else if (bowmenCount * player.getCombatStrengthInformation().getCombatStrength(false) < BOWMEN_COUNT_OF_KILLING_INFANTRY) {
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SWORD, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.BOW, 0);
			setRatioOfMaterial(player.playerId, EMaterialType.SWORD, 0F);
			setRatioOfMaterial(player.playerId, EMaterialType.SPEAR, 0.3F);
			setRatioOfMaterial(player.playerId, EMaterialType.BOW, 1F);
		} else {
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SWORD, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.SPEAR, 0);
			setNumberOfFutureProducedMaterial(player.playerId, EMaterialType.BOW, 0);
			setRatioOfMaterial(player.playerId, EMaterialType.SWORD, 0F);
			setRatioOfMaterial(player.playerId, EMaterialType.SPEAR, 0F);
			setRatioOfMaterial(player.playerId, EMaterialType.BOW, 1F);
		}
	}

	private void setNumberOfFutureProducedMaterial(byte playerId, EMaterialType materialType, int numberToProduce) {
		if (aiStatistics.getMaterialProduction(playerId).getAbsoluteProductionRequest(materialType) != numberToProduce) {
			taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, aiStatistics.getPositionOfPartition(playerId), materialType,
					EMaterialProductionType.SET_PRODUCTION, numberToProduce));
		}
	}

	private void setRatioOfMaterial(byte playerId, EMaterialType materialType, float ratio) {
		if (aiStatistics.getMaterialProduction(playerId).getUserConfiguredRelativeRequestValue(materialType) != ratio) {
			taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, aiStatistics.getPositionOfPartition(playerId), materialType,
					EMaterialProductionType.SET_RATIO, ratio));
		}
	}

	private void upgradeSoldiers() {
		for (ESoldierType type : SOLDIER_UPGRADE_ORDER) {
			if (player.getMannaInformation().isUpgradePossible(type)) {
				taskScheduler.scheduleTask(new UpgradeSoldiersGuiTask(player.playerId, type));
			}
		}
	}

	private void defend(SoldierPositions soldierPositions, Set<Integer> soldiersWithOrders) {
		List<ShortPoint2D> allMyTroops = new Vector<>();
		allMyTroops.addAll(soldierPositions.bowmenPositions);
		allMyTroops.addAll(soldierPositions.pikemenPositions);
		allMyTroops.addAll(soldierPositions.swordsmenPositions);
		sendTroopsTo(allMyTroops, aiStatistics.getEnemiesInTownOf(player.playerId).iterator().next(), soldiersWithOrders);
	}

	private void attack(SoldierPositions soldierPositions, boolean infantryWouldDie, Set<Integer> soldiersWithOrders) {
		IPlayer weakestEnemy = getWeakestEnemy();
		ShortPoint2D targetDoor = getTargetEnemyDoorToAttack(weakestEnemy);
		if (infantryWouldDie) {
			sendTroopsTo(soldierPositions.bowmenPositions, targetDoor, soldiersWithOrders);
		} else {
			List<ShortPoint2D> soldiers = new ArrayList<>(soldierPositions.bowmenPositions.size() + soldierPositions.pikemenPositions.size() + soldierPositions.swordsmenPositions.size());
			soldiers.addAll(soldierPositions.bowmenPositions);
			soldiers.addAll(soldierPositions.pikemenPositions);
			soldiers.addAll(soldierPositions.swordsmenPositions);
			sendTroopsTo(soldiers, targetDoor, soldiersWithOrders);
		}
	}

	private IPlayer getWeakestEnemy() {
		IPlayer weakestEnemyPlayer = null;
		int minAmountOfEnemyId = Integer.MAX_VALUE;

		for (IPlayer enemyPlayer : aiStatistics.getAliveEnemiesOf(player)) {
			int amountOfEnemyTroops = aiStatistics.getCountOfMovablesOfPlayer(enemyPlayer, EMovableType.SOLDIERS);
			if (amountOfEnemyTroops < minAmountOfEnemyId) {
				minAmountOfEnemyId = amountOfEnemyTroops;
				weakestEnemyPlayer = enemyPlayer;
			}
		}

		return weakestEnemyPlayer;
	}

	private void sendTroopsTo(List<ShortPoint2D> attackerPositions, ShortPoint2D target, Set<Integer> soldiersWithOrders) {
		List<Integer> attackerIds = new Vector<>();
		for (ShortPoint2D attackerPosition : attackerPositions) {
			int movableId = movableGrid.getMovableAt(attackerPosition.x, attackerPosition.y).getID();
			if (!soldiersWithOrders.contains(movableId)) {
				attackerIds.add(movableId);
			}
		}

		taskScheduler.scheduleTask(new MoveToGuiTask(player.playerId, target, attackerIds));
	}

	private ShortPoint2D getTargetEnemyDoorToAttack(IPlayer enemyToAttack) {
		List<ShortPoint2D> myMilitaryBuildings = aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.MILITARY_BUILDINGS, player.playerId);
		ShortPoint2D myBaseAveragePoint = AiStatistics.calculateAveragePointFromList(myMilitaryBuildings);
		List<ShortPoint2D> enemyMilitaryBuildings = aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.MILITARY_BUILDINGS, enemyToAttack.getPlayerId());
		ShortPoint2D nearestEnemyBuildingPosition = AiStatistics.detectNearestPointFromList(myBaseAveragePoint, enemyMilitaryBuildings);
		return aiStatistics.getBuildingAt(nearestEnemyBuildingPosition).getDoor();
	}

	private SoldierPositions calculateSituation(byte playerId) {
		SoldierPositions soldierPositions = new SoldierPositions();
		soldierPositions.swordsmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.SWORDSMAN_L1));
		soldierPositions.swordsmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.SWORDSMAN_L2));
		soldierPositions.swordsmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.SWORDSMAN_L3));
		soldierPositions.bowmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BOWMAN_L1));
		soldierPositions.bowmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BOWMAN_L2));
		soldierPositions.bowmenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BOWMAN_L3));
		soldierPositions.pikemenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.PIKEMAN_L1));
		soldierPositions.pikemenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.PIKEMAN_L2));
		soldierPositions.pikemenPositions.addAll(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.PIKEMAN_L3));
		return soldierPositions;
	}

	private static class SoldierPositions {
		private final List<ShortPoint2D> swordsmenPositions = new Vector<>();
		private final List<ShortPoint2D> bowmenPositions = new Vector<>();
		private final List<ShortPoint2D> pikemenPositions = new Vector<>();

		int getSoldiersCount() {
			return swordsmenPositions.size() + bowmenPositions.size() + pikemenPositions.size();
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
