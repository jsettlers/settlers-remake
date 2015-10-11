package jsettlers.common.player;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;

/**
 * @author codingberlin
 */
public interface IManaInformation {

	boolean isUpgradePossible(ESoldierType type);

	byte getLevel(ESoldierType type);

	byte getNextUpdateProgressPercent();

	byte getMaximumLevel();
}
