package jsettlers.lookandfeel.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel with background texture and Border
 * 
 * @author Andreas Butti
 */
public class BackgroundPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * ID to apply a custom Look and Feel
	 */
	private static final String uiClassID = "BackgroundPanelUI";

	/**
	 * Constructor
	 */
	public BackgroundPanel() {
		setLayout(null);
	}

	@Override
	public void doLayout() {
		if (getComponentCount() >= 1) {
			getComponent(0).setBounds(50, 70, getWidth() - 100, getHeight() - 140);
		}
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		// do nothing, this method is called from constructor
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

}
