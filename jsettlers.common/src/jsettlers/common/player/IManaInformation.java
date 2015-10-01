package jsettlers.common.player;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;

/**
 * @author codingberlin
 */
public interface IManaInformation {

	boolean isUpgradePossible(ESoldierType type);

	byte getLevel(ESoldierType type);

	void upgrade(ESoldierType type);

	byte getNextUpdateProgressPercent();

	byte getMaximumLevel();

	EMovableType getMovableTypeOf(ESoldierType type);

	void increaseMana();

	void increaseManaByBigTemple();

	void stopFutureManaIncreasingByBigTemple();
}
