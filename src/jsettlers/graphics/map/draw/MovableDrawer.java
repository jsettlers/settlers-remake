package jsettlers.graphics.map.draw;

import jsettlers.common.Color;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.sound.SoundManager;

/**
 * This is the movable drawer that draws movable objects (settlers).
 * <p>
 * It uses the settler image map to get the right image. TODO: cleanup.
 * 
 * @author michael
 */
public class MovableDrawer {

	private SettlerImageMap imageMap = SettlerImageMap.getInstance();
	private final SoundManager sound;

	public MovableDrawer(SoundManager sound) {
		this.sound = sound;
	}

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

		if (!movable.isSoundPlayed()) {
			EAction action = movable.getAction();
			if (action == EAction.ACTION1) {
				playSoundAction1(movable.getMovableType());
				movable.setSoundPlayed();
			} else if (action == EAction.ACTION2) {
				playSoundAction2(movable.getMovableType());
				movable.setSoundPlayed();
			}
		}
	}

	private void playSoundAction1(EMovableType type) {
		switch (type) {
			case LUMBERJACK:
				sound.playSound(1, 1, 1);
				break;
			case STONECUTTER:
				sound.playSound(3, 1, 1);
				break;
			case DIGGER:
				sound.playSound(2, 1, 1);
				break;
			case SAWMILLER:
				sound.playSound(5, 1, 1);
				break;
			case SMITH:
				sound.playSound(6, 1, 1);
				break;
			case FARMER:
				sound.playSound(12, 1, 1);
				break;
			case SWORDSMAN_L1:
			case SWORDSMAN_L2:
			case SWORDSMAN_L3:
				sound.playSound(30, 1, 1);
				break;
			case BOWMAN_L1:
			case BOWMAN_L2:
			case BOWMAN_L3:
				sound.playSound(33, 1, 1);
				break;
		}
	}

	private void playSoundAction2(EMovableType type) {
		// TODO Auto-generated method stub

	}

	private static void drawImage(MapDrawContext context, IMovable movable,
	        Image image) {
		ISPosition2D pos = movable.getPos();
		byte fogstatus = context.getVisibleStatus(pos.getX(), pos.getY());
		if (fogstatus == 0) {
			return; // break
		}

		Color color = context.getPlayerColor(movable.getPlayer());
		float shade = MapObjectDrawer.getColor(fogstatus);

		// draw settler
		image.draw(context.getGl(), color, shade);

		if (movable.isSelected()) {
			context.getGl().glTranslatef(0, 0, 0.2f);
			drawSelectionMark(context, movable.getHealth());
		}
	}

	private static void drawSelectionMark(MapDrawContext context, float health) {
		Image image =
		        ImageProvider.getInstance().getSettlerSequence(4, 7)
		                .getImageSafe(0);
		image.drawAt(context.getGl(), 0, 20);

		Sequence<? extends Image> sequence =
		        ImageProvider.getInstance().getSettlerSequence(4, 6);
		int healthId =
		        Math.min((int) ((1 - health) * sequence.length()),
		                sequence.length() - 1);
		Image healthImage = sequence.getImageSafe(healthId);
		healthImage.drawAt(context.getGl(), 0, 38);
	}
}
