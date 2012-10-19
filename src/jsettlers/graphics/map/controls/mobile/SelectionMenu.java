package jsettlers.graphics.map.controls.mobile;

import go.graphics.GLDrawContext;
import jsettlers.common.selectable.ISelectionSet;

public class SelectionMenu extends NormalMobileMenu {

	private ISelectionSet selection;
	private boolean contentIsDirty = true;

	public SelectionMenu() {
		super("");
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		synchronized (this) {
			if (contentIsDirty) {
				rebuildContent();
				contentIsDirty = false;
			}
		}
		super.drawAt(gl);
	}

	public synchronized void setSelection(ISelectionSet selection) {
		this.selection = selection;
		contentIsDirty = true;
	}

	private void rebuildContent() {
		removeAll();
	}

}
