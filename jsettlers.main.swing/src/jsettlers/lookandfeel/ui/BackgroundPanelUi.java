package jsettlers.lookandfeel.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

import jsettlers.lookandfeel.DrawHelper;
import jsettlers.lookandfeel.components.SplitedBackgroundPanel;
import jsettlers.lookandfeel.ui.img.UiImageLoader;

/**
 * Background Panel UI
 * 
 * @author Andreas Butti
 */
public class BackgroundPanelUi extends PanelUI {

	// private BufferedImage backgroundTextture;
	//

	/**
	 * Current background cache
	 */
	private BufferedImage tmpBg = null;

	/**
	 * Background color image
	 */
	private BufferedImage backgroundColor = UiImageLoader.get("stone-background-colors.jpg");

	/**
	 * Border texture for the border line
	 */
	private BufferedImage borderTexture = UiImageLoader.get("texture-border.png");

	/**
	 * Leaf image at the right corner
	 */
	private BufferedImage leafes1 = UiImageLoader.get("leafes1b.png");

	/**
	 * Constructor
	 */
	public BackgroundPanelUi() {
		// backgroundTextture = toBufferedImage(new ImageIcon(BackgroundComponent.class.getResource("nahtlohs" + TEXTURE_ID + ".jpg")).getImage());
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		super.paint(g1, c);

		Graphics2D g = DrawHelper.antialiasingOn(g1);
		if (tmpBg == null || tmpBg.getWidth() != c.getWidth() || tmpBg.getHeight() != c.getHeight()) {
			recreateBgImage(c);
		}

		g.drawImage(tmpBg, 0, 0, c);
	}

	/**
	 * Recreate the cached background image, if needed
	 * 
	 * @param c
	 *            The component
	 */
	private void recreateBgImage(JComponent c) {
		tmpBg = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics2D g = tmpBg.createGraphics();

		// scale the background color image
		g.drawImage(backgroundColor, 0, 0, c.getWidth(), c.getHeight(), c);
		//
		// for (int x = 0; x < getWidth(); x += backgroundTextture.getWidth()) {
		// for (int y = 0; y < getHeight(); y += backgroundTextture.getHeight()) {
		// multiplyImage(tmpBg, backgroundTextture, x, y);
		// }
		// }

		BorderDrawer border = new BorderDrawer(g, 3, 0, 0, c.getWidth(), c.getHeight());
		BufferedImage scaledTexture = DrawHelper
				.toBufferedImage(borderTexture.getScaledInstance(c.getWidth(), c.getHeight(), BufferedImage.SCALE_FAST));
		TexturePaint tp = new TexturePaint(scaledTexture,
				new Rectangle2D.Float(0, 0, c.getWidth(), c.getHeight()));
		border.setPaint(tp);
		border.drawRect();

		if (c instanceof SplitedBackgroundPanel) {
			border.drawVertical(((SplitedBackgroundPanel) c).getSplitPosition(), true);
		}

		float factor = 0.2f;
		g.drawImage(leafes1, c.getWidth() - 120, -30, (int) (leafes1.getWidth() * factor), (int) (leafes1.getHeight() * factor), c);

		g.dispose();
	}

	// private void multiplyImage(BufferedImage tmpBg, BufferedImage textture, int targetX, int targetY) {
	// for (int x = 0; x < textture.getWidth(); x++) {
	// for (int y = 0; y < textture.getHeight(); y++) {
	// if (x + targetX >= tmpBg.getWidth() || y + targetY >= tmpBg.getHeight()) {
	// continue;
	// }
	//
	// int rgb1 = tmpBg.getRGB(x + targetX, y + targetY);
	// Color c1 = new Color(rgb1);
	// float r1 = c1.getRed() / 255f;
	// float g1 = c1.getGreen() / 255f;
	// float b1 = c1.getBlue() / 255f;
	//
	// int rgb2 = textture.getRGB(x, y);
	// Color c2 = new Color(rgb2);
	// float addTexture = 0.15f;
	// float r2 = c2.getRed() / 255f + addTexture;
	// float g2 = c2.getGreen() / 255f + addTexture;
	// float b2 = c2.getBlue() / 255f + addTexture;
	//
	// int r = (int) (Math.min(1.0f, r1 * r2) * 255f);
	// int g = (int) (Math.min(1.0f, g1 * g2) * 255f);
	// int b = (int) (Math.min(1.0f, b1 * b2) * 255f);
	//
	// tmpBg.setRGB(x + targetX, y + targetY, r << 16 | g << 8 | b);
	// }
	// }
	//
	// // g.drawImage(backgroundTextture, x, y, this);
	//

}
