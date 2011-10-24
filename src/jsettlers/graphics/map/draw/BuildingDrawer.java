package jsettlers.graphics.map.draw;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;

/**
 * This class is used to draw buildings.
 * 
 * @author michael
 */
public class BuildingDrawer {
	private static final int SELECTMARK_SEQUENCE = 11;

	private static final int SELECTMARK_FILE = 4;

	private ImageProvider imageProvider = ImageProvider.getInstance();

	/**
	 * Draws a given buildng to the context.
	 * 
	 * @param context
	 * @param building
	 * @param color Gray color shade
	 */
	public void draw(MapDrawContext context, IBuilding building, Color color) {
		EBuildingType type = building.getBuildingType();
		float state = building.getStateProgress();
		float maskState;
		if (state < 0.5f) {
			maskState = state * 2;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(context, maskState, image, color);
			}

		} else if (state < 0.99) {
			maskState = state * 2 - 1;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				image.draw(context.getGl(), color);
			}

			for (ImageLink link : type.getImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(context, maskState, image, color);
			}
		} else {
			for (ImageLink link : type.getImages()) {
				Image image = imageProvider.getImage(link);
				image.draw(context.getGl(), color);
			}
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
	        float maskState, Image image, Color color) {
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
			addToArray(tris, 6 + i * 3, 1.0f / tiles * i, toplineBottom, image);
			addToArray(tris, 7 + i * 3, 1.0f / tiles * (i + 1), toplineBottom,
			        image);
			addToArray(tris, 8 + i * 3, 1.0f / tiles * (i + .5f), toplineTop,
			        image);
		}

		GLDrawContext gl = context.getGl();
		gl.color(color);
		gl.drawTrianglesWithTexture(image.getTextureIndex(gl), tris);
	}

	private void addToArray(float[] array, int pointindex, float u, float v,
	        Image image) {
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

}
