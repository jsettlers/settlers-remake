/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
