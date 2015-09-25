package jsettlers.common.player;

import jsettlers.common.player.EManaType;

/**
 * @author codingberlin
 */
public interface IManaInformation {
	boolean isUpgradePossible(EManaType type);

	byte getLevel(EManaType type);

	void upgrade(EManaType type);

	byte getNextUpdateProgressPercent();
}
