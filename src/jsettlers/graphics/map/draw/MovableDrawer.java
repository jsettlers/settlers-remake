package jsettlers.graphics.map.draw;

import java.awt.Color;

import jsettlers.common.movable.IMovable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is the movable drawer that draws movable objects (settlers).
 * <p>
 * It uses the settler image map to get the right image. TODO: cleanup.
 * 
 * @author michael
 */
public class MovableDrawer {

	private SettlerImageMap imageMap = SettlerImageMap.getInstance();

	/**
	 * Draws a movable
	 * 
	 * @param context
	 *            The context to draw at.
	 * @param movable
	 *            The movable.
	 */
	public void draw(MapDrawContext context, IMovable movable) {
		Image image = this.imageMap.getImageForSettler(movable);
		drawImage(context, movable, image);
	}

	private void drawImage(MapDrawContext context, IMovable movable, Image image) {
		/*
		 * ISPosition2D currentPos = context.getTile(movable.getPos());
		 * ISPosition2D nextPos = context.getTileInDirection(currentPos,
		 * movable.getDirection()); int x; int y; if (nextPos != null &&
		 * isWalking) { float progress = movable.getMoveProgress(); x = (int)
		 * ((1 - progress) * context.getT(currentPos) + progress
		 * context.getX(nextPos)); y = (int) ((1 - progress) *
		 * context.getY(currentPos) + progress context.getY(nextPos)); } else {
		 * x = context.getX(currentPos); y = context.getY(currentPos); }
		 */

		Color color = context.getPlayerColor(movable.getPlayer());

		// draw settler
		image.draw(context.getGl(), color);

		if (movable.isSelected()) {
			context.getGl().glTranslatef(0, 0, 0.2f);
			drawSelectionMark(context, movable.getHealth());
		}
	}

	private void drawSelectionMark(MapDrawContext context, float health) {
		Image image =
		        ImageProvider.getInstance().getSettlerSequence(4, 7)
		                .getImageSafe(0);
		image.drawAt(context.getGl(), 0, 20);

		Sequence<? extends Image> sequence =
		        ImageProvider.getInstance().getSettlerSequence(4, 6);
		int healthId =
		        Math.min((int) ((1 - health) * sequence.length()), sequence
		                .length() - 1);
		Image healthImage = sequence.getImageSafe(healthId);
		healthImage.drawAt(context.getGl(), 0, 38);
	}
}
