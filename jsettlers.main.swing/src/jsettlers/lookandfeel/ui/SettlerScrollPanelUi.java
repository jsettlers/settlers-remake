package jsettlers.lookandfeel.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.metal.MetalScrollPaneUI;

/**
 * Scroll panel UI
 * 
 * @author Andreas Butti
 */
public class SettlerScrollPanelUi extends MetalScrollPaneUI {

	/**
	 * Constructor
	 */
	public SettlerScrollPanelUi() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		((JScrollPane) c).getViewport().setOpaque(false);
		((JScrollPane) c).getViewport().setBorder(null);
		c.setOpaque(false);
		c.setBorder(null);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.setColor(UIDefaults.HALFTRANSPARENT_BLACK);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}
}