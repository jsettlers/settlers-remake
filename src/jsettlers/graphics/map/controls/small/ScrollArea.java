package jsettlers.graphics.map.controls.small;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.graphics.utils.UIPanel;

public class ScrollArea extends UIPanel {
	
	private static final int CIRCLEPOINTS = 12;

	@Override
	public void drawAt(GLDrawContext gl) {
		float centerX = getPosition().getCenterX();
		float centerY = getPosition().getCenterY();
		float radius = centerX - getPosition().getMinX();
		
		float[] points = new float[3 * CIRCLEPOINTS];
		
		for (int i = 0; i < CIRCLEPOINTS; i++) {
			float x = radius * (float) Math.sin(Math.PI * 2 / CIRCLEPOINTS * i) + centerX;
			float y = radius * (float) Math.cos(Math.PI * 2 / CIRCLEPOINTS * i) + centerY;
			points[i * 3] = x;
			points[i * 3 + 1] = y;
			points[i * 3 + 2] = 0;
		}
		gl.color(Color.WHITE);
	    gl.drawLine(points, true);
	}
}
