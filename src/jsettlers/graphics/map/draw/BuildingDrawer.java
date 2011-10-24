package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;
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

		float state = building.getStateProgress();
		float maskState;
		if (state < 0.5f) {
			maskState = state * 2;
			Image image = sequence.getImageSafe(1);
			drawWithConstructionMask(context, maskState, image);

		} else if (state < 0.99) {
			maskState = state * 2 - 1;
			sequence.getImageSafe(1).draw(context.getGl());
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
		// number of tiles in x direction, can be adjustet for performance
		int tiles = 6;

		float toplineBottom = maskState;
		float toplineTop = Math.min(1, toplineBottom + .1f);

		float[] tris = new float[(tiles + 2) * 3 * 5];

		addToArray(tris, 0, 0, 0, image);
		addToArray(tris, 1, 1, 0, image);
		addToArray(tris, 2, 0, toplineBottom, image);
		addToArray(tris, 3, 1, 0, image);
		addToArray(tris, 4, 1, toplineBottom, image);
		addToArray(tris, 5, 0, toplineBottom, image);
		
		for (int i = 0; i < tiles; i++) {
			addToArray(tris, 6 + i*3, 1.0f / tiles * i, toplineBottom, image);
			addToArray(tris, 7 + i*3, 1.0f / tiles * (i + 1), toplineBottom, image);
			addToArray(tris, 8 + i*3, 1.0f / tiles * (i + .5f), toplineTop, image);
		}

		GLDrawContext gl = context.getGl();
		gl.drawTrianglesWithTexture(image.getTextureIndex(gl), tris);
	}

	private void addToArray(float[] array, int pointindex,
	        float u, float v, Image image) {
		int left = image.getOffsetX();
		int top = -image.getOffsetY();
		int bottom = top - image.getHeight();
		
		int x = left + (int) (image.getWidth() * u);
		int y = bottom + (int) (image.getHeight() * v);
		
		int offset = pointindex * 5;
		array[offset] = x;
		array[offset + 1] = y;
		array[offset + 2] = 0;
		array[offset + 3] = u * image.getTextureScaleX();
		array[offset + 4] = v * image.getTextureScaleY();
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
