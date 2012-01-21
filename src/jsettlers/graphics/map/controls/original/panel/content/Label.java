package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.utils.UIElement;
import jsettlers.graphics.utils.UIPanel;

public class Label extends UIPanel implements UIElement {

	private final EFontSize size;
	private final String[] words;
	private double[] widths = null;
	private double spaceWidth;
	private double lineHeight;

	public Label(String message, EFontSize size) {
		this.size = size;

		words = message.split(" ");

	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		TextDrawer drawer = gl.getTextDrawer(size);

		if (widths == null) {
			widths = new double[words.length];
			for (int i = 0; i < words.length; i++) {
				widths[i] = drawer.getWidth(words[i]);
			}
			spaceWidth = drawer.getWidth(" ");
			lineHeight = drawer.getHeight("j");
		}

		double maxwidth = getPosition().getWidth();

		StringBuilder line = new StringBuilder(words[0]);
		double linewidth = widths[0];
		double y = 0;
		for (int i = 1; i < words.length; i++) {
			double newlinewidth = linewidth + spaceWidth + widths[i];
			if (newlinewidth > maxwidth) {
				drawLine(drawer, line.toString(), y);
				line = new StringBuilder(words[i]);
				y += lineHeight;
				linewidth = widths[i];
			} else {
				line.append("");
				line.append(words[i]);
				linewidth = newlinewidth;
			}
		}
		drawLine(drawer, line.toString(), y);

	}

	private void drawLine(TextDrawer drawer, String string, double y) {
		drawer.renderCentered(getPosition().getCenterX(),
		        (float) (getPosition().getMaxY() - y - lineHeight / 2), string);
	}
}
