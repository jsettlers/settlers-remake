package jsettlers.graphics.androidui.menu;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.androidui.actions.ContextAction;
import jsettlers.graphics.androidui.actions.ContextActionListener;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;

/**
 * This class holds the data needed to display custom Android menus on the UI.
 * 
 * @author michael
 */
public class AndroidMenuPutable implements ActionFireable, IFragmentHandler {
	private final LayoutInflater layoutInflater;

	private ActionFireable actionFireable;

	private ContextActionListener contextActionListener;

	private final IFragmentHandler fragmentHandler;

	private final Context context;

	/**
	 * Creates a new {@link AndroidMenuPutable}.
	 */
	public AndroidMenuPutable(Context context, IFragmentHandler fragmentHandler) {
		super();
		this.context = context;
		this.fragmentHandler = fragmentHandler;
		this.layoutInflater =
		        (LayoutInflater) context
		                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public LayoutInflater getLayoutInflater() {
		return layoutInflater;
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

	protected void setActiveAction(ContextAction action) {
		contextActionListener.contextActionChanged(action);
	}

	public void setContextActionListener(
	        ContextActionListener contextActionListener) {
		this.contextActionListener = contextActionListener;
	}

	@Override
	public void showMenuFragment(Fragment fragment) {
		// TODO: Handle own back stack here.
		fragmentHandler.showMenuFragment(fragment);
	}

	@Override
	public void hideMenu() {
		fragmentHandler.hideMenu();
	}

	public Context getContext() {
		return context;
	}
}
