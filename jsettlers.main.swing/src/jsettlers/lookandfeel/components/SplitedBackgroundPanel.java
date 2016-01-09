package jsettlers.lookandfeel.components;

import java.awt.Dimension;

import javax.swing.JLabel;

import jsettlers.lookandfeel.LFStyle;

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
	 * Constructor
	 */
	public SplitedBackgroundPanel() {
		titleLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
	}

	@Override
	public void doLayout() {
		if (getComponentCount() >= 1) {
			getComponent(0).setBounds(50, 70, 220, getHeight() - 140);
		}
		int w = getWidth() - 360;
		if (getComponentCount() >= 2) {
			getComponent(1).setBounds(320, 70, w, getHeight() - 140);
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
