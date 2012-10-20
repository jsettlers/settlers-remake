package jsettlers.graphics.androidui.menu;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.MobileMenu;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class AndroidMobileMenu implements MobileMenu, Hideable {
	private AndroidMenuPutable putable;
	private boolean backgroundAlreadyClicked = false;
	private int menuResource;

	public AndroidMobileMenu(AndroidMenuPutable puttable, int menuResource) {
		this.putable = puttable;
		this.menuResource = menuResource;

	}


	protected Context getContext() {
	    return putable.getParentView().getContext();
    }
	
	@Override
	public void setPosition(FloatRectangle floatRectangle) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
	}

	@Override
	public Action getActionFor(UIPoint position) {
		return getHideAction();
	}

	private Action getHideAction() {
	    /*
		 * A click on the background should bring us back to the game.
		 */
		if (!backgroundAlreadyClicked) {
			backgroundAlreadyClicked = true;
			return new Action(EActionType.BACK);
		} else {
			return null;
		}
    }

	@Override
	public void show() {
		View menu = putable.getLayoutInflater().inflate(menuResource, null);
		fillLayout(menu);
		putable.getParentView().addView(menu);
		putable.getParentView().setVisibility(View.VISIBLE);
		backgroundAlreadyClicked = false;
	}

	protected abstract void fillLayout(View menu);

	@Override
	public void hide() {
		putable.getParentView().setVisibility(View.INVISIBLE);
		putable.getParentView().removeAllViews();
	}
	
	/* (non-Javadoc)
	 * @see jsettlers.graphics.androidui.menu.Hideable#requestHide()
	 */
	@Override
    public void requestHide() {
		Action hideAction = getHideAction();
		if (hideAction != null) {
			putable.fireAction(hideAction);
		}
	}

	protected OnClickListener generateActionListener(Action action, boolean hideOnClick) {
	    return new ActionClickListener(putable, action, hideOnClick ? this : null);
    };

}
