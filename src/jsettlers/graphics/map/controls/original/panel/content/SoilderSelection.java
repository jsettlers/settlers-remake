package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.utils.UIPanel;

public class SoilderSelection implements IContentProvider {

	private static final EMovableType[] soildertypes = new EMovableType[] {
	        EMovableType.SWORDSMAN_L1,
	        EMovableType.SWORDSMAN_L2,
	        EMovableType.SWORDSMAN_L3,
	        EMovableType.PIKEMAN_L1,
	        EMovableType.PIKEMAN_L2,
	        EMovableType.PIKEMAN_L3,
	        EMovableType.BOWMAN_L1,
	        EMovableType.BOWMAN_L2,
	        EMovableType.BOWMAN_L3,
	};

	/**
	 * Rows of selectables
	 */
	public static int ROWS = 10;

	private final UIPanel panel;

	public SoilderSelection(ISelectionSet selection) {
		panel = new UIPanel();

		addRowsToPanel(panel, selection, soildertypes);

		UIPanel kill = new UILabeledButton(Labels.getString("kill"), new Action(EActionType.DESTROY));
		UIPanel stop = new UILabeledButton(Labels.getString("stop"), new Action(EActionType.STOP_WORKING));

		panel.addChild(kill, .1f, .1f, .5f, .2f);
		panel.addChild(stop, .5f, .1f, .9f, .2f);
	}

	public static void addRowsToPanel(UIPanel panel, ISelectionSet selection,
	        EMovableType[] types) {
		float rowHeight = 1f / ROWS;

		int rowi = ROWS - 1; // from bottom
		for (int i = 0; i < types.length; i++) {
			EMovableType type = types[i];
			int count = selection.getMovableCount(type);

			if (count > 0) {
				SelectionRow row = new SelectionRow(type, count);
				panel.addChild(row, 0.1f, rowHeight * (rowi - 1), .9f,
				        rowHeight * (rowi));
				rowi--;
			}
		}

	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public IContextListener getContextListener() {
		return null;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return null;
	}

}
