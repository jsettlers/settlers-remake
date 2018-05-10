package jsettlers.logic.movable.components;

import jsettlers.common.selectable.ESelectionType;

/**
 * @author homoroselaps
 */

public class SelectableComponent extends Component {
	private static final long serialVersionUID = 665477836143096339L;

	private final ESelectionType selectionType;
	private       boolean        selected;

	SelectableComponent(ESelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public ESelectionType getSelectionType() {
		return selectionType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	protected void onDisable() {
		setSelected(false);
	}
}
