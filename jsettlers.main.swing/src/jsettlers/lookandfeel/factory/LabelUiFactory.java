package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.SettlerLabelDynamicUi;
import jsettlers.lookandfeel.ui.SettlerLabelUi;
import jsettlers.lookandfeel.ui.UIDefaults;

/**
 * Label UI factory
 * 
 * @author Andreas Butti
 */
public class LabelUiFactory {

	/**
	 * Forward calls
	 */
	public static final ForwardFactory FORWARD = new ForwardFactory();

	/**
	 * Header Label
	 */
	private static final SettlerLabelUi headerLabel = new SettlerLabelUi(UIDefaults.HEADER_TEXT_COLOR, 311, 30, 210, 27);

	/**
	 * Label short
	 */
	private static final SettlerLabelUi labelShort = new SettlerLabelUi(UIDefaults.LABEL_TEXT_COLOR, 19, 324, 122, 26);

	/**
	 * Label long
	 */
	private static final SettlerLabelUi labelLong = new SettlerLabelUi(UIDefaults.LABEL_TEXT_COLOR, 311, 30, 210, 27);

	/**
	 * Label long
	 */
	private static final SettlerLabelDynamicUi labelDynamic = new SettlerLabelDynamicUi(UIDefaults.LABEL_TEXT_COLOR, 311, 30, 210, 27);

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(LFStyle.KEY);
		if (LFStyle.LABEL_HEADER == style) {
			return headerLabel;
		}
		if (LFStyle.LABEL_LONG == style) {
			return labelLong;
		}
		if (LFStyle.LABEL_SHORT == style) {
			return labelShort;
		}
		if (LFStyle.LABEL_DYNAMIC == style) {
			return labelDynamic;
		}

		return FORWARD.create(c);
	}
}