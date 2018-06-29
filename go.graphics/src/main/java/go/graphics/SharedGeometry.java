package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;

public class SharedGeometry {

	private static final int CAPACITY = 1000;
	private static final int QUAD_SIZE = 4*4*4;

	private int size = 0;
	private GeometryHandle geometry;
	private static final ByteBuffer generate_buffer = ByteBuffer.allocateDirect(QUAD_SIZE).order(ByteOrder.nativeOrder());

	private static final ArrayList<SharedGeometry> geometries = new ArrayList<>();

	public static SharedGeometryHandle addGeometry(GLDrawContext dc, float[] data) throws IllegalBufferException {
		int sgeometryIndex = 0;

		while(true) {
			// create an instance if needed
			if(geometries.size() == sgeometryIndex) geometries.add(new SharedGeometry(dc));

			SharedGeometry geometry = geometries.get(sgeometryIndex);
			// generate it
			geometry.validate(dc);

			// skip if we wont fit
			if (geometry.size < CAPACITY) {
				// add it to our vbo
				generate_buffer.asFloatBuffer().put(data);
				dc.updateGeometryAt(geometry.geometry, QUAD_SIZE*geometry.size, generate_buffer);
				generate_buffer.rewind();

				geometry.size++;
				return new SharedGeometryHandle(geometry);
			}

			sgeometryIndex++;
		}
	}

	public static class SharedGeometryHandle {
		public final GeometryHandle geometry;
		public final int index;
		private final int iteration = SharedGeometry.iteration;

		private SharedGeometryHandle(SharedGeometry shared) {
			geometry = shared.geometry;
			index = shared.size-1;
		}
	}

	private static int iteration = 0;
	private static GLDrawContext staticdc = null;

	public static boolean isValid(GLDrawContext dc, SharedGeometryHandle handle) {
		if(dc != staticdc) {
			staticdc = dc;
			iteration++;
		}
		return handle.iteration!=iteration;
	}

	private void validate(GLDrawContext dc) {
		if(!geometry.isValid()) {
			geometry = dc.generateGeometry(CAPACITY*QUAD_SIZE);
			size = 0;
		}
	}

	public static GeometryHandle getGeometry(int sgeometryIndex) {
		return geometries.get(sgeometryIndex).geometry;
	}

	private SharedGeometry(GLDrawContext dc) {
		geometry = dc.generateGeometry(CAPACITY*QUAD_SIZE);
	}


	public static float[] createQuadGeometry(float lx, float ly, float hx, float hy, float lu, float lv, float hu, float hv) {
		return new float[] {
			// bottom right
			hx, ly, hu, lv,
			// top right
			hx, hy, hu, hv,
			// top left
			lx, hy, lu, hv,
			// bottom left
			lx, ly, lu, lv,
		};
	}
}
