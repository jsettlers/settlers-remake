package jsettlers.graphics.map;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOEventHandlerProvoder;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.text.DecimalFormat;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionHandler;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.PanToAction;
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.OriginalControls;
import jsettlers.graphics.map.controls.small.SmallControls;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.map.draw.MovableDrawer;
import jsettlers.graphics.map.selection.ISelectionSet;

/**
 * This is the main map content class. It manages the map drawing on the screen
 * region.
 * <p>
 * <h1>The drawing process</h1> The map is drawn in three steps. At first, the
 * background is drawn. After that, it is overlayed with the images for
 * settlers, and other map objects. Then the interface is drawn above everything
 * else.
 * <p>
 * The objects and background are drawn with the map draw context.
 * <p>
 * UI structure:
 * <ul>
 * <li>left minimap decoration</li>
 * <li>right minimap decoration</li>
 * <li>main background
 * <ul>
 * <li>tabs
 * <ol>
 * <li>building tabs</li>
 * <li>goods tabs</li>
 * <li>settlers tabs</li>
 * <li>game tab</li>
 * </ol>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author michael
 */
public class MapContent implements SettlersContent, GOEventHandlerProvoder,
        IMapInterfaceListener {
	private boolean ENABLE_DEBUG = false;

	private final IGraphicsGrid map;

	private Background background = new Background();

	private MovableDrawer movableDrawer = new MovableDrawer();

	private final MapDrawContext context;

	private MapObjectDrawer objectDrawer = new MapObjectDrawer();

	/**
	 * The current connector that connects the outside world to us.
	 */
	private MapInterfaceConnector connector;

	private IntRectangle oldScreen;

	/**
	 * The controls that represent the interface.
	 */
	private final IControls controls;

	/**
	 * Creates a new map content for the given map.
	 * 
	 * @param map
	 *            The map.
	 */
	public MapContent(IGraphicsGrid map) {
		this.map = map;
		this.context = new MapDrawContext(map);

		controls = new OriginalControls(context);
		//controls = new SmallControls();

		this.connector = new MapInterfaceConnector(this);
		this.connector.addListener(this);
	}

	private void resizeTo(int newWidth, int newHeight) {
		this.context.setSize(newWidth, newHeight);
		this.controls.resizeTo(newWidth, newHeight);
	}

	@Override
	public void drawContent(GLDrawContext gl, int newWidth, int newHeight) {
		adaptScreenSize(newWidth, newHeight);
		this.objectDrawer.increaseAnimationStep();

		this.context.begin(gl);

		drawBackground();
		drawMain();
		this.context.end();

		gl.glTranslatef(0, 0, .5f);
		drawSelectionHint(gl);
		controls.drawAt(gl);

		drawFramerate(gl);
		drawTooltip(gl);
	}

	private void adaptScreenSize(int newWidth, int newHeight) {
		if (newWidth != this.context.getScreen().getWidth()
		        || newHeight != this.context.getScreen().getHeight()) {
			resizeTo(newWidth, newHeight);
		}
		IntRectangle newScreen = context.getScreen().getPosition();
		if (!newScreen.equals(oldScreen)) {
			getInterfaceConnector().fireAction(
			        new ScreenChangeAction(context.getScreenArea()));
		}
		oldScreen = newScreen;
	}

	private void drawSelectionHint(GLDrawContext gl) {
		if (this.currentSelectionAreaStart != null
		        && this.currentSelectionAreaEnd != null) {
			float x1 = (float) this.currentSelectionAreaStart.getX();
			float y1 = (float) this.currentSelectionAreaStart.getY();
			float x2 = (float) this.currentSelectionAreaEnd.getX();
			float y2 = (float) this.currentSelectionAreaEnd.getY();

			gl.color(1, 1, 1, 1);
			gl.drawLine(new float[] {
			        x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0
			}, true);
		}
	}

	private long lastFrame = 0;

	private UIPoint currentSelectionAreaStart;

	private void drawFramerate(GLDrawContext gl2) {
		long currentFrame = System.nanoTime();
		double framerate = 1000000000.0 / (currentFrame - this.lastFrame);
		String frames = new DecimalFormat("###.##").format(framerate);
		TextDrawer drawer = gl2.getTextDrawer(EFontSize.NORMAL);
		drawer.drawString(200, 5, "FPS: " + frames);

		this.lastFrame = currentFrame;
	}

	private void drawTooltip(GLDrawContext gl) {
		if (!tooltipString.isEmpty()) {
			TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);
			drawer.drawString((int) mousePosition.getX(),
			        (int) mousePosition.getY(), tooltipString);
		}
	}

	/**
	 * Draws the main content (buildings, settlers, ...), assuming the context
	 * is set up.
	 */
	private void drawMain() {
		boolean needDrawDebug = false;

		IMapArea tiles =
		        new MapShapeFilter(this.context.getConverter().getMapForScreen(
		                this.context.getScreen().getPosition().bigger(30)),
		                map.getWidth(), map.getHeight());
		for (ISPosition2D pos : tiles) {
			short x = pos.getX();
			short y = pos.getY();
			IMapObject object = map.getMapObjectsAt(x, y);
			if (object != null) {
				this.objectDrawer.drawMapObject(this.context, this.map, pos,
				        object);
			}

			IMovable movable = map.getMovableAt(x, y);
			if (movable != null) {
				if (movable.getAction() == EAction.WALKING) {
					ISPosition2D origin =
					        movable.getDirection().getInverseDirection()
					                .getNextHexPoint(pos);
					if (origin == null) {
						origin = pos;
					}
					this.context.beginBetweenTileContext(origin, pos,
					        movable.getMoveProgress());
					this.movableDrawer.draw(this.context, movable);
					this.context.endTileContext();
				} else {
					this.context.beginTileContext(pos);
					this.movableDrawer.draw(this.context, movable);
					this.context.endTileContext();
				}
			}

			if (ENABLE_DEBUG && map.getDebugColorAt(x, y) != null) {
				needDrawDebug = true;
			}

			if (map.isBorder(x, y)) {
				this.context.beginTileContext(pos);
				byte player = map.getPlayerAt(x, y);
				objectDrawer.drawPlayerBorderObject(context, player);
				this.context.endTileContext();
			}
		}

		// if (map.getConstructionPreviewBuilding() != null) {
		// Sequence<? extends Image> sequence =
		// ImageProvider.getInstance().getSettlerSequence(4, 5);
		// float imageScale = Byte.MAX_VALUE / Math.max(sequence.length(), 1);
		//
		// ISPosition2D underMouse =
		// this.context.getPositionOnScreen(mousePosition.x, mousePosition.y);
		// IHexTile tile = map.getTile(underMouse);
		// if (tile != null) {
		// context.beginTileContext(tile);
		// for (ImageLink image :
		// map.getConstructionPreviewBuilding().getImages()) {
		// ImageProvider.getInstance().getImage(image).draw(context.getGl());
		// }
		// context.endTileContext();
		// }
		// }

		if (needDrawDebug) {
			drawDebugColors();
		}
	}

	private void drawDebugColors() {
		IMapArea tiles =
		        new MapShapeFilter(context.getScreenArea(), map.getWidth(),
		                map.getHeight());
		GLDrawContext gl = this.context.getGl();

		float[] shape =
		        new float[] {
		                0,
		                4,
		                .5f,
		                0,
		                0,
		                -3,
		                2,
		                .5f,
		                0,
		                0,
		                -3,
		                -2,
		                .5f,
		                0,
		                0,
		                0,
		                -4,
		                .5f,
		                0,
		                0,
		                0,
		                -4,
		                .5f,
		                0,
		                0,
		                3,
		                -2,
		                .5f,
		                0,
		                0,
		                3,
		                2,
		                .5f,
		                0,
		                0,
		                0,
		                4,
		                .5f,
		                0,
		                0,
		        };

		for (ISPosition2D pos : tiles) {
			Color color = map.getDebugColorAt(pos.getX(), pos.getY());
			if (color != null) {
				this.context.beginTileContext(pos);
				gl.color(color);
				gl.drawQuadWithTexture(0, shape);
				context.endTileContext();
			}
		}
	}

	/**
	 * Draws the background.
	 * 
	 * @param gl
	 * @param screen2
	 */
	private void drawBackground() {
		this.background.drawMapContent(this.context);
	}

	@Override
	public void handleEvent(GOEvent event) {
		if (event instanceof GOPanEvent) {
			event.setHandler(new PanHandler(this.context.getScreen()));
		} else if (event instanceof GOCommandEvent) {
			GOCommandEvent commandEvent = (GOCommandEvent) event;
			Action action = handleCommand(commandEvent);

			if (action != null) {
				fireActionEvent(event, action);
			}
		} else if (event instanceof GOKeyEvent) {
			Action actionForKeyboard =
			        getActionForKeyboard(((GOKeyEvent) event).getKeyCode());
			if (actionForKeyboard != null) {
				fireActionEvent(event, actionForKeyboard);
			}
		} else if (event instanceof GODrawEvent) {
			GODrawEvent drawEvent = (GODrawEvent) event;
			if (!controls.handleDrawEvent(drawEvent)) {
				handleDraw(drawEvent);
			}
		} else if (event instanceof GOHoverEvent) {
			GOHoverEvent hoverEvent = (GOHoverEvent) event;
			handleHover(hoverEvent);
		}
	}

	private void fireActionEvent(GOEvent event, Action action) {
		event.setHandler(new ActionHandler(action, getInterfaceConnector()));
	}

	private Action getActionForKeyboard(String keyCode) {
		if ("F12".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.FAST_FORWARD);
		} else if ("P".equalsIgnoreCase(keyCode)
		        || "PAUSE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SPEED_TOGGLE_PAUSE);
		} else if ("+".equals(keyCode)) {
			return new Action(EActionType.SPEED_FASTER);
		} else if ("-".equals(keyCode)) {
			return new Action(EActionType.SPEED_SLOWER);
		} else if (" ".equals(keyCode)) {
			return new Action(EActionType.SHOW_SELECTION);
		} else if ("d".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.DEBUG_ACTION);
		} else if ("s".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.STOP_WORKING);
		} else if ("q".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_DEBUG);
		} else {
			return null;
		}
	}

	private GOEventHandler hoverHandler = new GOModalEventHandler() {
		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			changeMousePosition(((GOHoverEvent) event).getHoverPosition());
		}

		@Override
		public void aborted(GOEvent event) {
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			changeMousePosition(((GOHoverEvent) event).getHoverPosition());
		}
	};

	private String tooltipString = "";

	private UIPoint mousePosition = new UIPoint(0, 0);

	private void handleHover(GOHoverEvent hoverEvent) {
		hoverEvent.setHandler(hoverHandler);
	}

	protected void changeMousePosition(UIPoint position) {
		mousePosition = position;

		if (controls.containsPoint(position)) {
			tooltipString = controls.getDescriptionFor(position);
			if (tooltipString == null) {
				tooltipString = "";
			}
		} else {
			tooltipString = "";
		}
	}

	private Action handleCommand(GOCommandEvent commandEvent) {
		UIPoint position = commandEvent.getCommandPosition();
		if (controls.containsPoint(position)) {
			return controls.getActionFor(position);
		} else {
			// handle map click
			return handleCommandOnMap(commandEvent, position);
		}
	}

	private void handleDraw(GODrawEvent drawEvent) {
		// Point start = drawEvent.getMousePosition();
		// if (this.mapInterface.isOnSidebar(start)) {
		// click on interface. TODO
		// this.mapInterface.handleDraw(drawEvent);
		// } else {
		// handle map click
		handleDrawOnMap(drawEvent);
		// }
	}

	private void handleDrawOnMap(GODrawEvent drawEvent) {
		this.currentSelectionAreaStart = drawEvent.getDrawPosition();
		drawEvent.setHandler(this.drawSelectionHandler);
	}

	private GOEventHandler drawSelectionHandler = new GOModalEventHandler() {

		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			updateSelectionArea(((GODrawEvent) event).getDrawPosition(), true);
		}

		@Override
		public void aborted(GOEvent event) {
			abortSelectionArea();
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			updateSelectionArea(((GODrawEvent) event).getDrawPosition(), false);
		}

	};

	private UIPoint currentSelectionAreaEnd;

	private Action handleCommandOnMap(GOCommandEvent commandEvent,
	        UIPoint position) {
		ISPosition2D onMap =
		        this.context.getPositionOnScreen((int) position.getX(),
		                (int) position.getY());
		if (this.context.checkMapCoordinates(onMap.getX(), onMap.getY())) {
			Action action;
			if (commandEvent.isSelecting()) {
				action = new SelectAction(onMap);
			} else {
				action = new MoveToAction(onMap);
			}
			return action;
		}
		return null;
	}

	protected void abortSelectionArea() {
		this.currentSelectionAreaStart = null;
		this.currentSelectionAreaEnd = null;
	}

	private void updateSelectionArea(UIPoint mousePosition, boolean finished) {
		if (finished && currentSelectionAreaStart != null) {
			int x1 = (int) mousePosition.getX();
			int x2 = (int) this.currentSelectionAreaStart.getX();
			int y1 = (int) mousePosition.getY();
			int y2 = (int) this.currentSelectionAreaStart.getY();

			if (x1 > x2) {
				int temp = x1;
				x1 = x2;
				x2 = temp;
			}
			if (y1 > y2) {
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
			IMapArea area = this.context.getRectangleOnScreen(x1, y1, x2, y2);
			getInterfaceConnector().fireAction(new SelectAreaAction(area));

			this.currentSelectionAreaStart = null;
			this.currentSelectionAreaEnd = null;
		} else {
			this.currentSelectionAreaEnd = mousePosition;
		}
	}

	/**
	 * Gets the interface connector for the ui.
	 * 
	 * @return The connector to access the interface.
	 */
	public MapInterfaceConnector getInterfaceConnector() {
		return this.connector;
	}

	public void setSelection(ISelectionSet selection) {
	}

	public void scrollTo(ISPosition2D point, boolean mark) {
		this.context.scrollTo(point);
	}

	/**
	 * We currently do not provide redraw requests
	 */
	@Override
	public void addRedrawListener(RedrawListener l) {
	}

	@Override
	public void removeRedrawListener(RedrawListener l) {
	}

	@Override
	public void action(Action action) {
		controls.action(action);
		if (action.getActionType() == EActionType.TOGGLE_DEBUG) {
			ENABLE_DEBUG = !ENABLE_DEBUG;
		} else if (action.getActionType() == EActionType.PAN_TO) {
			PanToAction panAction = (PanToAction) action;
			scrollTo(panAction.getCenter(), false);
		} else if (action.getActionType() == EActionType.SCREEN_CHANGE) {
			ScreenChangeAction screenAction = (ScreenChangeAction) action;
			controls.setMapViewport(screenAction.getScreenArea());
		}
	}

}
