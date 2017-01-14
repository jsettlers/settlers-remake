package jsettlers.main.android.controls;

import java.util.LinkedList;

import go.graphics.android.AndroidSoundPlayer;

import jsettlers.common.menu.action.IAction;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.menus.SettlersSoldiersMenu;

import android.content.Context;

/**
 * Created by tompr on 14/01/2017.
 */

public class ControlsAdapter implements ActionControls, DrawControls, SelectionControls, TaskControls, MenuFactory {
    private final Context context;
    private final AndroidSoundPlayer soundPlayer;
    private final IInGamePlayer player;
    private final AndroidControls androidControls;
    private final GameMenu gameMenu;

    private final LinkedList<SelectionListener> selectionListeners = new LinkedList<>();
    private final LinkedList<ActionListener> actionListeners = new LinkedList<>();
    private final LinkedList<DrawListener> drawListeners = new LinkedList<>();

    private ISelectionSet selection;

    public ControlsAdapter(Context context, AndroidSoundPlayer soundPlayer, IInGamePlayer player) {
        this.context = context;
        this.soundPlayer = soundPlayer;
        this.player = player;

        androidControls = new AndroidControls(this);
        gameMenu = new GameMenu(context, androidControls, soundPlayer);
    }

        public IControls getControls() {
            return androidControls;
        }

    public void onAction(IAction action) {
        synchronized (actionListeners) {
            for (ActionListener listener : actionListeners) {
                listener.actionFired(action);
            }
        }
    }

    public void onSelection(ISelectionSet selection) {
        if (selection != null && selection.getSize() > 0) {
            this.selection = selection;
        } else {
            this.selection = null;
        }

        synchronized (selectionListeners) {
            for (SelectionListener listener : selectionListeners) {
                listener.selectionChanged(this.selection);
            }
        }
    }

    public void onDraw() {
        synchronized (drawListeners) {
            for (DrawListener listener : drawListeners) {
                listener.draw();
            }
        }
    }

    /**
     * ActionControls implementation
     */
    @Override
    public void fireAction(IAction action) {
        androidControls.fireAction(action);
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.add(actionListener);
        }
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.remove(actionListener);
        }
    }

    /**
     * DrawControls implementation
     */
    @Override
    public void addDrawListener(DrawListener drawListener) {
        synchronized (drawListeners) {
            drawListeners.add(drawListener);
        }
    }

    @Override
    public void removeDrawListener(DrawListener drawListener) {
        synchronized (drawListeners) {
            drawListeners.remove(drawListener);
        }
    }

    /**
     * SelectionControls implementation
     */
    @Override
    public ISelectionSet getCurrentSelection() {
        return selection;
    }

    @Override
    public void addSelectionListener(SelectionListener selectionListener) {
        synchronized (selectionListeners) {
            selectionListeners.add(selectionListener);
        }
    }

    @Override
    public void removeSelectionListener(SelectionListener selectionListener) {
        synchronized (selectionListeners) {
            selectionListeners.remove(selectionListener);
        }
    }

    /**
     * TaskControls implementation
     * @return
     */
    @Override
    public boolean isTaskActive() {
        return androidControls.isTaskActive();
    }

    @Override
    public void endTask() {
        androidControls.endTask();
    }


    /**
     * MenuFactory implementation
     */
    @Override
    public GameMenu getGameMenu() {
        return gameMenu;
    }

    @Override
    public BuildingsMenu getBuildingsMenu() {
        return new BuildingsMenu(this);
    }

    @Override
    public SettlersSoldiersMenu getSettlersSoldiersMenu() {
        return new SettlersSoldiersMenu(this);
    }
}
