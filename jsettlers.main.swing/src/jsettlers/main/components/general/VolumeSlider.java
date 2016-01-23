package jsettlers.main.components.general;

import jsettlers.lookandfeel.LFStyle;

/**
 * Slider to select volume in settings
 * 
 * @author Andreas Butti
 */
public class VolumeSlider extends SettlerSlider {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public VolumeSlider() {
		setStringPainted(true);

		setMinimum(0);
		setMaximum(100);
		setValue(50);

		putClientProperty(LFStyle.KEY, LFStyle.PROGRESSBAR_SLIDER);
		updateUI();
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
		setString(n + "%");
	}

}
