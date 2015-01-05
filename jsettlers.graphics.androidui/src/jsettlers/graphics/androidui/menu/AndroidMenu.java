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
