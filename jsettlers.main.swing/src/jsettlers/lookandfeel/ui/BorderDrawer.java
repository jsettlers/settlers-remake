package jsettlers.lookandfeel.ui;

import java.awt.*;

/**
 * Draws the yellow border on the stone texture
 * 
 * @author Andreas Butti
 */
public class BorderDrawer {

	/**
	 * Graphics to draw with
	 */
	private Graphics2D g;

	/**
	 * Line with to draw
	 */
	private int lineWidth;

	/**
	 * Paint to use (Texture)
	 */
	private Paint paint;

	/**
	 * Original texture to back up
	 */
	private Stroke originalStroke;

	/**
	 * Use an even number!
	 */
	private int cornerLength1 = 26;

	/**
	 * Padding to the outer border
	 */
	private int padding = 20;

	/**
	 * Start x position
	 */
	private int x1;

	/**
	 * Start y position
	 */
	private int y1;

	/**
	 * End x position
	 */
	private int x2;

	/**
	 * End y position
	 */
	private int y2;

	/**
	 * Shadow offset x
	 */
	private int shadowX = 2;

	/**
	 * Shadow offset y
	 */
	private int shadowY = 3;

	/**
	 * Constructor
	 * 
	 * @param g
	 *            Graphics to draw with
	 * @param lineWidth
	 *            Line with to draw
	 * @param x1
	 *            Start x position
	 * @param y1
	 *            Start y position
	 * @param x2
	 *            End x position
	 * @param y2
	 *            End y position
	 */
	public BorderDrawer(Graphics2D g, int lineWidth, int x1, int y1, int x2, int y2) {
		this.g = g;
		this.lineWidth = lineWidth;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;

	}

	/**
	 * @param paint
	 *            Paint to use (Texture)
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	/**
	 * @return Paint to use (Texture)
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * Draw a horizontal line
	 * 
	 * @param y
	 *            Y position
	 */
	protected void drawHorizontal(int y) {
		int cl2 = cornerLength1 / 2;
		g.drawLine(x1 + cl2 + padding, y, x2 - cl2 - padding, y);
	}

	/**
	 * Draw a vertical line
	 * 
	 * @param x
	 *            x position
	 * @param right
	 *            Draw the end to the right or the left side
	 */
	protected void drawVertical(int x, boolean right) {
		initGraphics();

		// draw shadow
		g.setColor(new Color(0, 0, 0, 150));
		y1 += shadowY;
		y2 += shadowY;
		drawVertical0(x + shadowX, right);

		y1 -= shadowY;
		y2 -= shadowY;

		g.setPaint(paint);
		drawVertical0(x, right);

		resetGraphics();
	}

	/**
	 * Draw a vertical line, without initialize graphics
	 * 
	 * @param x
	 *            X Position
	 * @param right
	 *            Draw the end to the right or the left side
	 */
	private void drawVertical0(int x, boolean right) {
		int cl2 = cornerLength1 / 2;
		int yA = y1 + cl2 + padding;
		int yB = y2 - cl2 - padding;
		g.drawLine(x, yA, x, yB);

		// == corner ==

		if (right) {
			x -= cornerLength1;
		}

		// UPPER - down
		g.drawLine(x + cl2, y1 + padding, x + cl2, y1 + padding + cornerLength1);
		// LOWER - up
		g.drawLine(x + cl2, yB - cl2, x + cl2, yB + cl2);

		// UPPER - horizontal
		g.drawLine(x, yA, x + cornerLength1, yA);
		// LOWER - horizontal
		g.drawLine(x, yB, x + cornerLength1, yB);

		// UPPER - up
		if (right) {
			x -= cornerLength1;
		}
		g.drawLine(x + cornerLength1, y1 + padding - cl2, x + cornerLength1, y1 + padding + cl2);
		// LOWER - down
		g.drawLine(x + cornerLength1, yB, x + cornerLength1, yB + cornerLength1);

	}

	/**
	 * Internal draw rect, without initialize graphics
	 */
	private void drawRect0() {
		// top
		drawHorizontal(y1 + padding);
		// bottom
		drawHorizontal(y2 - padding);

		// left
		drawVertical0(x1 + padding, false);
		// right
		drawVertical0(x2 - padding, true);
	}

	/**
	 * Draw the rect
	 */
	public void drawRect() {
		initGraphics();

		// draw shadow
		g.setColor(new Color(0, 0, 0, 150));
		x1 += shadowX;
		x2 += shadowX;
		y1 += shadowY;
		y2 += shadowY;
		drawRect0();

		x1 -= shadowX;
		x2 -= shadowX;
		y1 -= shadowY;
		y2 -= shadowY;

		g.setPaint(paint);
		drawRect0();

		resetGraphics();
	}

	/**
	 * Restore the original stroke
	 */
	private void resetGraphics() {
		g.setStroke(originalStroke);
	}

	/**
	 * Initialize the graphics with the stroke
	 */
	private void initGraphics() {
		this.originalStroke = g.getStroke();

		g.setStroke(new BasicStroke(lineWidth));

	}

}
