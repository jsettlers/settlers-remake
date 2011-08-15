package go.area;

import javax.media.opengl.GL2;

public interface GLAreaDrawPane {

	void resizeArea(GL2 gl2, int x, int y, int width, int height);

	void draw(GL2 gl2);

}
