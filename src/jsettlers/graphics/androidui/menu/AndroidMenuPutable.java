package jsettlers.graphics.androidui.menu;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * This class holds the data needed to display custom Android menus on the UI.
 * 
 * @author michael
 */
public class AndroidMenuPutable implements ActionFireable {
	private final LayoutInflater layoutInflater;

	private final FrameLayout parentView;

	private ActionFireable actionFireable;

	/**
	 * Creates a new {@link AndroidMenuPutable}.
	 * 
	 * @param parentView
	 *            The parent view. This is an empty {@link FrameLayout} to which
	 *            we add our menu.
	 */
	public AndroidMenuPutable(FrameLayout parentView) {
		super();
		this.layoutInflater =
		        (LayoutInflater) parentView.getContext().getSystemService(
		                Context.LAYOUT_INFLATER_SERVICE);
		this.parentView = parentView;
	}

	public LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	public FrameLayout getParentView() {
		return parentView;
	}

	public void setActionFireable(ActionFireable actionFireable) {
		this.actionFireable = actionFireable;
	}

	@Override
	public void fireAction(Action action) {
		if (actionFireable != null) {
			actionFireable.fireAction(action);
		}
	}
}
