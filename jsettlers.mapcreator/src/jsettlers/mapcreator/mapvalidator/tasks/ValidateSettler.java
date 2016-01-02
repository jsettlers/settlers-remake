package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.mapvalidator.result.fix.DeleteObjectFix;

/**
 * Check that all "normal" settlers are within the land of the right player
 * 
 * @author Andreas Butti
 */
public class ValidateSettler extends AbstractValidationTask {

	/**
	 * Fix for wrong placed settlers
	 */
	private DeleteObjectFix fix = new DeleteObjectFix();

	/**
	 * Constructor
	 */
	public ValidateSettler() {
	}

	@Override
	public void doTest() {
		addHeader("settler.header", fix);

		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof MovableObject) {
					testMoveableObject(x, y, (MovableObject) mapObject);
				}
			}
		}
	}

	/**
	 * Test if this movable object is valid at this position
	 * 
	 * @param x
	 *            X Pos
	 * @param y
	 *            Y Pos
	 * @param movableObject
	 *            MovableObject
	 */
	private void testMoveableObject(int x, int y, MovableObject movableObject) {
		EMovableType type = movableObject.getType();

		if (!type.needsPlayersGround()) {
			// don't need player ground, so don't check it here
			return;
		}

		if (players[x][y] != movableObject.getPlayerId()) {
			ShortPoint2D point = new ShortPoint2D(x, y);
			addErrorMessage("settler.wrong-land", point, Labels.getName(type), movableObject.getPlayerId(), players[x][y]);
			fix.addInvalidObject(point);
		}
	}

}
