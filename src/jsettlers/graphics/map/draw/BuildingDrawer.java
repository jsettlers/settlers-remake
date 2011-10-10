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
		GLDrawContext gl = context.getGl();
		/*gl.glEnable(GL.GL_TEXTURE_2D);
		image.bind(gl);

		int left = image.getOffsetX();
		int top = -image.getOffsetY();
		int bottom = top - image.getHeight();

		// number of tiles in x direction
		int tiles = 3;
		int steps = tiles * 2 + 1;

		float toplineBottom = (1 - maskState);
		float toplineTop = toplineBottom + 3.0f / image.getHeight();

		// Our buffer: 2 floats for x,y, 2 floats for u,v, always: top, botom,
		// top, ...
		ByteBuffer buffer =
		        ByteBuffer.allocateDirect((4 * Buffers.SIZEOF_FLOAT) * 2
		                * steps);
		buffer.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = buffer.asFloatBuffer();

		for (int i = 0; i < steps; i++) {
			// relative to top left, y downwards
			float x = (float) i / (tiles * 2);
			float topy = i % 2 == 0 ? toplineBottom : toplineTop;

			//top
			floatBuffer.put(left + x * image.getWidth());
			floatBuffer.put(top - topy * image.getHeight());
			floatBuffer.put(x * image.getTextureScaleX());
			floatBuffer.put(topy * image.getTextureScaleY());
			
			//bottom
			floatBuffer.put(left + x * image.getWidth());
			floatBuffer.put(bottom);
			floatBuffer.put(x * image.getTextureScaleX());
			floatBuffer.put(image.getTextureScaleY());

		}

		floatBuffer.rewind();
		float[] debug = new float[4 * 2 * steps];
		floatBuffer.get(debug);
		System.out.println(Arrays.toString(debug));

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		floatBuffer.position(0);
		gl.glVertexPointer(2, GL2.GL_FLOAT, 4 * Buffers.SIZEOF_FLOAT,
		        floatBuffer);
		floatBuffer.position(2 * Buffers.SIZEOF_FLOAT);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 4 * Buffers.SIZEOF_FLOAT,
		        floatBuffer);

		gl.glColor3f(1, 1, 1);
		gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 2 * steps);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 2 * steps);

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL.GL_TEXTURE_2D); */
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
