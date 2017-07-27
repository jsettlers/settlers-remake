/*
 * Copyright (c) 2017
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
 */

package jsettlers.main.android.core.controls;

import static java8.util.stream.StreamSupport.stream;

import java.util.LinkedList;

import go.graphics.android.AndroidSoundPlayer;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.ETextDrawPosition;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.controls.IControls;

import android.content.Context;

/**
 * Created by tompr on 14/01/2017.
 */
public class ControlsAdapter implements ActionControls, DrawControls, SelectionControls, TaskControls, PositionControls {
	private static final int SOUND_THREADS = 6;

	private final IInGamePlayer player;
	private final AndroidControls androidControls;
	private final MapContent mapContent;
	private final GameMenu gameMenu;
	private final IGraphicsGrid graphicsGrid;

	private final LinkedList<SelectionListener> selectionListeners = new LinkedList<>();
	private final LinkedList<ActionListener> actionListeners = new LinkedList<>();
	private final LinkedList<DrawListener> drawListeners = new LinkedList<>();
	private final LinkedList<DrawListener> infrequentDrawListeners = new LinkedList<>();
	private final LinkedList<PositionChangedListener> positionChangedListeners = new LinkedList<>();

	private final int fireDrawListenerFrequency = 15;
	private int fireDrawListenerCounter = -1;

	private ISelectionSet selection;
	private ShortPoint2D displayCenter;

	public ControlsAdapter(Context context, IStartedGame game) {
		this.player = game.getInGamePlayer();

		AndroidSoundPlayer soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);
		androidControls = new AndroidControls(this);
		mapContent = new MapContent(game, soundPlayer, ETextDrawPosition.TOP_LEFT, androidControls);
		gameMenu = new GameMenu(context, soundPlayer, this);
		graphicsGrid = game.getMap();
	}

	public IControls getControls() {
		return androidControls;
	}

	public MapContent getMapContent() {
		return mapContent;
	}

	public GameMenu getGameMenu() {
		return gameMenu;
	}

	public IInGamePlayer getInGamePlayer() {
		return player;
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
			stream(drawListeners).forEach(DrawListener::draw);
		}

		fireDrawListenerCounter = (fireDrawListenerCounter + 1) % fireDrawListenerFrequency;

		if (fireDrawListenerCounter == 0) {
			synchronized (infrequentDrawListeners) {
				stream(infrequentDrawListeners).forEach(DrawListener::draw);
			}
		}
	}

	public void onPositionChanged(MapRectangle screenArea, ShortPoint2D displayCenter) {
		this.displayCenter = displayCenter;

		synchronized (positionChangedListeners) {
			stream(positionChangedListeners).forEach(PositionChangedListener::positionChanged);
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
	public void fireAction(EActionType actionType) {
		fireAction(new Action(actionType));
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

	@Override
	public void addInfrequentDrawListener(DrawListener drawListener) {
		synchronized (infrequentDrawListeners) {
			infrequentDrawListeners.add(drawListener);
		}
	}

	@Override
	public void removeInfrequentDrawListener(DrawListener drawListener) {
		synchronized (infrequentDrawListeners) {
			infrequentDrawListeners.remove(drawListener);
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
	public void deselect() {
		if (selection != null) {
			fireAction(new Action(EActionType.DESELECT));
		}
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
	 *
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
	 * ParitionControls inplementation
	 */
	@Override
	public boolean isInPlayerPartition() {
		if (displayCenter == null) {
			return false;
		}

		return graphicsGrid.getPlayerIdAt(displayCenter.x, displayCenter.y) == player.getPlayerId();
	}

	@Override
	public IPartitionData getCurrentPartitionData() {
		return graphicsGrid.getPartitionData(displayCenter.x, displayCenter.y);
	}

	@Override
	public ShortPoint2D getCurrentPosition() {
		return displayCenter;
	}

	@Override
	public void addPositionChangedListener(PositionChangedListener positionChangedListener) {
		synchronized (positionChangedListeners) {
			positionChangedListeners.add(positionChangedListener);
		}
	}

	@Override
	public void removePositionChangedListener(PositionChangedListener positionChangedListener) {
		synchronized (positionChangedListeners) {
			positionChangedListeners.remove(positionChangedListener);
		}
	}
}
