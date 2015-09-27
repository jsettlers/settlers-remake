package jsettlers.common.player;

import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public interface ICombatStrengthInformation {

	float getCombatStrengthAtPosition(ShortPoint2D position);

	float getCombatStrength();
}
