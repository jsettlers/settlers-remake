package jsettlers.graphics.androidui.menu;

import java.util.ArrayList;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.androidui.actions.ContextAction;
import jsettlers.graphics.androidui.actions.ContextActionListener;
import android.content.Context;
import android.view.LayoutInflater;

/**
 * This class holds the data needed to display custom Android menus on the UI.
 * 
 * @author michael
 */
public class AndroidMenuPutable implements ActionFireable {
	private final LayoutInflater layoutInflater;

	private ActionFireable actionFireable;

	private ContextActionListener contextActionListener;

	private final IFragmentHandler fragmentHandler;

	private final Context context;

	private ArrayList<AndroidMenu> activeMenu = new ArrayList<AndroidMenu>();
	private final Object activeMenuMutex = new Object();

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

	/**
	 * Shows a new fragment. Only call from UI thread.
	 * 
	 * @param fragment
	 */
	public void showMenuFragment(AndroidMenu fragment) {
		synchronized (activeMenuMutex) {
			activeMenu.add(fragment);
			fragmentHandler.showMenuFragment(fragment);
		}
	}

	public void hideMenu() {
		synchronized (activeMenuMutex) {
			activeMenu.clear();
			fragmentHandler.hideMenu();
		}
	}

	public boolean goBackInMenu() {
		synchronized (activeMenuMutex) {
			int size = activeMenu.size();
			if (size >= 2) {
				activeMenu.remove(size - 1);
				fragmentHandler.showMenuFragment(activeMenu.get(size - 2));
				return true;
			} else if (size >= 1) {
				hideMenu();
				return true;
			} else {
				return false;
			}
		}
	}

	public Context getContext() {
		return context;
	}

	public AndroidMenu getActiveMenu() {
		synchronized (activeMenuMutex) {
			return activeMenu.isEmpty() ? null : activeMenu.get(activeMenu
			        .size() - 1);
		}
	}
}
