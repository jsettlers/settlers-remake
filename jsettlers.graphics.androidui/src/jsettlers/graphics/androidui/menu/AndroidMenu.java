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

import go.graphics.UIPoint;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.androidui.actions.ContextAction;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.view.View.OnClickListener;

public abstract class AndroidMenu extends Fragment implements Hideable {
	private AndroidMenuPutable putable;
	private boolean backgroundAlreadyClicked;

	public AndroidMenu(AndroidMenuPutable puttable) {
		this.putable = puttable;
	}

	protected Context getContext() {
		return putable.getContext();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private Action getHideAction() {
		/*
		 * A click on the background should bring us back to the game.
		 */
		return new ExecutableAction() {
			@Override
			public void execute() {
				requestHide();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.graphics.androidui.menu.Hideable#requestHide()
	 */
	@Override
	public void requestHide() {
		new Handler(getContext().getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				putable.hideMenu();
			}
		});
	}

	protected OnClickListener generateActionListener(Action action,
			boolean hideOnClick) {
		return new ActionClickListener(putable, action, hideOnClick ? this
				: null);
	};

	protected void setActiveAction(ContextAction action) {
		putable.setActiveAction(action);
	}

	public Action getActionFor(UIPoint mapPosition) {
		return getHideAction();
	}

	public ActionFireable getActionFireable() {
		return putable;
	}

	/**
	 * Called periodically while menu is in front and game is active.
	 */
	public void poll() {
	}

	/**
	 * Called when the back button is pressed while we are active.
	 * 
	 * @return <code>true</code> if we handled the press.
	 */
	public boolean onBackButtonPressed() {
		return false;
	}

	public AndroidMenuPutable getPutable() {
		return putable;
	}

}
