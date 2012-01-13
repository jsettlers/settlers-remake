package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.map.selection.SettlerSelection;
import jsettlers.graphics.utils.UIPanel;

/**
 * Displays a selection of specialists.
 * 
 * @author michael
 */
public class SpecialistSelection implements IContentProvider {

	private static final EMovableType[] specialists = new EMovableType[] {
	        EMovableType.PIONEER, EMovableType.THIEF, EMovableType.GEOLOGIST,
	};

	/**
	 * Rows of selectables
	 */
	public static int ROWS = 10;

	private final UIPanel panel;

	public SpecialistSelection(SettlerSelection selection) {
		panel = new UIPanel();

		SoilderSelection.addRowsToPanel(panel, selection, specialists);

		UIPanel stop =
		        new UILabeledButton("stop",
		                new Action(EActionType.STOP_WORKING));
		UIPanel work =
		        new UILabeledButton("work", new Action(
		                EActionType.START_WORKING));

		panel.addChild(stop, .1f, .1f, .5f, .2f);
		panel.addChild(work, .5f, .1f, .9f, .2f);

		if (selection.getMovableCount(EMovableType.PIONEER) > 0) {
			UIPanel convert =
			        new UILabeledButton("zu Siedlern umwandeln",
			                new ConvertAction(EMovableType.BEARER,
			                        Short.MAX_VALUE));
			panel.addChild(convert, .1f, .2f, .9f, .3f);
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

	/**
	 * Checks if the selection contains soilders.
	 * 
	 * @param settlerSelection
	 * @return
	 */
	public static boolean isFor(SettlerSelection settlerSelection) {
		for (EMovableType t : specialists) {
			if (settlerSelection.getMovableCount(t) > 0) {
				return true;
			}
		}
		return false;
	}
}
