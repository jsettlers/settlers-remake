package jsettlers.graphics.map.controls.original.panel.button;

import java.util.Collection;

import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;

/**
 * This class manages the selection of the {@link SelectionManagedMaterialButton}s.
 *
 * @author Michael Zangl
 */
public  class SelectionManager {
	private Collection<SelectionManagedMaterialButton> buttons;
	private EMaterialType selected;

	public void setButtons(Collection<SelectionManagedMaterialButton> buttons) {
		this.buttons = buttons;
		updteSelected();
	}

	public Action getSelectAction(final EMaterialType material) {
		return new ExecutableAction() {
			@Override
			public void execute() {
				select(material);
			}
		};
	}

	protected void select(EMaterialType material) {
		this.selected = material;
		updteSelected();
	}

	private void updteSelected() {
		for (SelectionManagedMaterialButton b : buttons) {
			b.setSelected(selected == b.getMaterial());
		}
	}

	public EMaterialType getSelected() {
		return selected;
	}
}