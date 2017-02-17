package jsettlers.graphics.map.controls.original.panel.button;

import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.Action;

/**
 * This is a material button for the trading GUI.
 *
 * @author Michael Zangl
 */
public class SelectionManagedMaterialButton extends MaterialButton {
	private SelectionManager selectionManager;

	/**
	 * Creates a new {@link SelectionManagedMaterialButton}.
	 *
	 * @param material
	 *            The material this button is for.
	 */
	public SelectionManagedMaterialButton(EMaterialType material) {
		super(null, material);
	}

	@Override
	public Action getAction() {
		if (selectionManager != null) {
			return selectionManager.getSelectAction(getMaterial());
		} else {
			return null;
		}
	}

	/**
	 * Binds this button to a selection manager.
	 *
	 * @param selectionManager
	 *            The manager to use.
	 */
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
}