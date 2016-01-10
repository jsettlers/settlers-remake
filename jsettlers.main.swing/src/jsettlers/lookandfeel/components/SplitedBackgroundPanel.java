package jsettlers.lookandfeel.components;

import jsettlers.lookandfeel.LFStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel with background texture and Border
 * 
 * Do not set layout, this class has an integrated layout. You can add two components, first the left, second the right
 * 
 * @author Andreas Butti
 */
public class SplitedBackgroundPanel extends BackgroundPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Title
	 */
	protected JLabel titleLabel = new JLabel("<Title>", JLabel.CENTER);

	/**
	 * Split position
	 */
	private int splitPosition = 300;

	/**
	 * Constructor
	 */
	public SplitedBackgroundPanel() {
		titleLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
		add(titleLabel);
	}

	/**
	 * @return Split position
	 */
	public int getSplitPosition() {
		return splitPosition;
	}

	/**
	 * @param splitPosition
	 *            Split position
	 */
	public void setSplitPosition(int splitPosition) {
		int oldSplitPosition = this.splitPosition;
		firePropertyChange("splitPosition", oldSplitPosition, splitPosition);
		if (splitPosition != oldSplitPosition) {
			repaint();
		}
	}

	@Override
	public void doLayout() {
		if (getComponentCount() >= 2) {
			getComponent(1).setBounds(50, 70, splitPosition - 80, getHeight() - 140);
		}
		int w = getWidth() - splitPosition - 80;
		if (getComponentCount() >= 3) {
			getComponent(2).setBounds(splitPosition + 30, 70, w, getHeight() - 140);
		}

		Dimension pf = titleLabel.getPreferredSize();
		int x = 320 + (w - pf.width) / 2;
		titleLabel.setBounds(x, 40, pf.width, pf.height);
	};

	/**
	 * @return The title label on the right side
	 */
	public JLabel getTitleLabel() {
		return titleLabel;
	}
}
