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
		addHeader("playerstart.header", null /* no autofix possible */);

		for (int player = 0; player < data.getPlayerCount(); player++) {
			ShortPoint2D point = data.getStartPoint(player);
			if (players[point.x][point.y] != player) {
				addErrorMessage("playerstart.text", point, player);
			}
			// mark
			borders[point.x][point.y] = true;
		}
	}

}
