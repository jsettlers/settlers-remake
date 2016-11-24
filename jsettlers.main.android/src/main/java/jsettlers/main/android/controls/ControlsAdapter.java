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

    //
    // So
    /**
     * A task is something that requires multiple steps. E.g. show constructions markers, then choose build location.
     * If an action is part of a task then store it so we know how to react when the next action comes in.
     */
    private IAction taskAction;

    /**
     * The action is already happening in the game, here we just store it if it's part of a task. Finish the current task if it's run to completion.
     * Or cancel the current task if the user has started some other task. (finishing and cancelling both currently use endTask())
     */
    @Override
    public void action(IAction action) {
        switch (action.getActionType()) {
            case SHOW_CONSTRUCTION_MARK:
                ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) action;
                if (showConstructionMarksAction.getBuildingType() != null) { // null means dismissing the construction markers, so is not awaiting further actions
                    startTask(action);
                }
                break;
            case MOVE_TO:
                // MOVE_TO will already be active in this case so don't need to do anything.
                break;
            case SET_WORK_AREA:
            case SELECT_POINT:
            case SELECT_AREA:
            case BUILD:
            case ABORT:
                endTask();
                break;
        }
    }

    @Override
    public IAction replaceAction(IAction action) {
        if(taskAction != null) {
            if (action.getActionType() == EActionType.SELECT_POINT ) {
                PointAction pointAction = (PointAction) action;

                switch (taskAction.getActionType()) {
                    case SHOW_CONSTRUCTION_MARK:
                        ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) taskAction;
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
            startTask(new Action(EActionType.MOVE_TO));
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

    public boolean isTaskActive() {
        return taskAction != null;
    }

    private void startTask(IAction action) {
        endTask();
        taskAction = action;
    }

    public void endTask() {
        if(taskAction != null) {
            switch (taskAction.getActionType()) {
                case SHOW_CONSTRUCTION_MARK:
                    actionFireable.fireAction(new ShowConstructionMarksAction(null));
            }

            taskAction = null;
        }
    }
}
