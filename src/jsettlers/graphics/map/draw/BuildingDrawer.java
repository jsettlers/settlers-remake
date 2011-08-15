package jsettlers.graphics.map.draw;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.sequence.Sequence;

/**
 * This class is used to draw buildings.
 * 
 * @author michael
 */
public class BuildingDrawer {
	private static final int SELECTMARK_SEQUENCE = 11;

	private static final int SELECTMARK_FILE = 4;

	private static final int FILE = 13;

	private static final int BUILD_SIZE = 10;

	private ImageProvider imageProvider = ImageProvider.getInstance();

	/**
	 * Draws a given buildng to the context.
	 * 
	 * @param context
	 * @param building
	 */
	public void draw(MapDrawContext context, IBuilding building) {

		Sequence<? extends Image> sequence =
		        getBuildingSequence(building.getBuildingType());

		float state = building.getConstructionState();
		float maskState;
		if (state < 0.5f) {
			maskState = state * 2;
			Image image = sequence.getImageSafe(1);
			drawWithConstructionMask(context, maskState, image);

		} else if (state < 0.99) {
			maskState = state * 2 - 1;
			sequence.getImage(1).draw(context.getGl());
			Image image = sequence.getImageSafe(0);
			drawWithConstructionMask(context, maskState, image);
		} else {
			sequence.getImageSafe(0).draw(context.getGl());
		}

		if (building.isSelected()) {
			drawSelectMarker(context);
		}
	}

	private void drawSelectMarker(MapDrawContext context) {
		context.getGl().glTranslatef(0, 20, .2f);
		Image image =
		        imageProvider.getSettlerSequence(SELECTMARK_FILE,
		                SELECTMARK_SEQUENCE).getImageSafe(0);
		image.draw(context.getGl());
	}

	private void drawWithConstructionMask(MapDrawContext context,
	        float maskState, Image image) {
		GL2 gl = context.getGl();

		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(false);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xFFFFFFFF); // draw stencil buffer
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);

		int startx = image.getOffsetX();
		int endx = startx + image.getWidth();
		int bottomy = image.getOffsetY();
		int lowy =
		        (int) (bottomy + maskState * image.getHeight()) - BUILD_SIZE
		                / 2;

		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		boolean isLow = true;
		for (int currentX = startx; currentX < endx; currentX += BUILD_SIZE) {
			int currentY = isLow ? lowy : lowy + BUILD_SIZE;
			gl.glVertex2i(currentX, currentY);
			isLow = !isLow;
		}

		gl.glVertex2i(endx, bottomy);
		gl.glVertex2i(startx, bottomy);
		gl.glEnd();

		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 0xFFFFFFFF);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		image.draw(context.getGl());

		gl.glDisable(GL.GL_STENCIL_TEST);

		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	}

	/**
	 * Gets a building sequence for the given building type.
	 * 
	 * @param type
	 *            The type
	 * @return The sequence.
	 */
	public Sequence<? extends Image> getBuildingSequence(EBuildingType type) {
		return this.imageProvider
		        .getSettlerSequence(FILE, type.getImageIndex());
	}

}
