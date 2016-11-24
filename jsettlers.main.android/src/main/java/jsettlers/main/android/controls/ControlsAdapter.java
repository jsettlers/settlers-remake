package jsettlers.main.android.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

/**
 * Created by tompr on 21/11/2016.
 */

public class ControlsAdapter implements IControls {
    private ActionFireable actionFireable;

    private IAction activeAction;

    @Override
    public void action(IAction action) {
        switch (action.getActionType()) {
            case SHOW_CONSTRUCTION_MARK:
                ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) action;
                if (showConstructionMarksAction.getBuildingType() != null) { // null means dismissing the construction markers, so is not awaiting further actions
                    activeAction = action;
                }
                break;
            case MOVE_TO:
                activeAction = action;
                break;
            case SET_WORK_AREA:
                activeAction = null;
                break;
            case SELECT_POINT:
                activeAction = null;
                break;
            case SELECT_AREA:
                activeAction = null;
                break;
            case BUILD:
                actionFireable.fireAction(new ShowConstructionMarksAction(null));
                activeAction = null;
                break;
            case ABORT:
                activeAction = null;
                break;
        }
    }

    @Override
    public IAction replaceAction(IAction action) {
        if(activeAction != null) {
            if (action.getActionType() == EActionType.SELECT_POINT ) {
                PointAction pointAction = (PointAction) action;

                switch (activeAction.getActionType()) {
                    case SHOW_CONSTRUCTION_MARK:
                        ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) activeAction;
                        return new BuildAction(showConstructionMarksAction.getBuildingType(), pointAction.getPosition());
                    case MOVE_TO:
                        return new PointAction(EActionType.MOVE_TO, pointAction.getPosition());
                }
            }
        }

        return action;
    }

    @Override
    public void displaySelection(ISelectionSet selection) {
        //TODO tell the UI what type of selection this is so we can update the Settlers menu
        if (selection != null && (selection.getSelectionType() == ESelectionType.SOLDIERS || selection.getSelectionType() == ESelectionType.SPECIALISTS)) {
            activeAction = new Action(EActionType.MOVE_TO);
        }
    }

    @Override
    public void drawAt(GLDrawContext gl) {
    }

    @Override
    public void resizeTo(float newWidth, float newHeight) {
    }

    @Override
    public boolean containsPoint(UIPoint position) {
        return false;
    }

    @Override
    public String getDescriptionFor(UIPoint position) {
        return null;
    }

    @Override
    public void setMapViewport(MapRectangle screenArea) {
    }

    @Override
    public Action getActionFor(UIPoint position, boolean selecting) {
        return null;
    }

    @Override
    public boolean handleDrawEvent(GODrawEvent event) {
        return false;
    }

    @Override
    public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {
        this.actionFireable = actionFireable;
    }

    @Override
    public String getMapTooltip(ShortPoint2D point) {
        return null;
    }

    @Override
    public void stop() {
    }

    public boolean isActionPending() {
        return activeAction != null;
    }

    public void cancelPendingAction() {
        if(activeAction != null) {
            switch (activeAction.getActionType()) {
                case SHOW_CONSTRUCTION_MARK:
                    actionFireable.fireAction(new ShowConstructionMarksAction(null));
            }

            activeAction = null;
        }
    }
}
