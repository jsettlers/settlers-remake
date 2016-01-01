package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.position.ShortPoint2D;

/**
 * Validate all players start position
 * 
 * @author Andreas Butti
 */
public class ValidatePlayerStartPosition extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidatePlayerStartPosition() {
	}

	@Override
	public void doTest() {
		for (int player = 0; player < data.getPlayerCount(); player++) {
			ShortPoint2D point = data.getStartPoint(player);
			if (players[point.x][point.y] != player) {
				testFailed("Player " + player + " has invalid start point", point);
			}
			// mark
			borders[point.x][point.y] = true;
		}
	}

}
