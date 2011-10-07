package go.graphics.opengl;

import go.graphics.area.Area;

import javax.swing.JFrame;

/**
 * This is a window that consists of exactly one area.
 * @author michael
 *
 */
public class AreaWindow {
	private final Area area;
	private final JFrame frame;

	public AreaWindow(Area area) {
		this.area = area;
		this.frame = new JFrame();
		frame.add(new AreaContainer(area));
		frame.setSize(500, 500);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	public Area getArea() {
		return area;
	}
}
