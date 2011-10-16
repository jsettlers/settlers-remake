package jsettlers.graphics.map;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOEventHandlerProvoder;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
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
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.OriginalControls;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.BuildingDrawer;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.map.draw.MovableDrawer;
import jsettlers.graphics.map.selection.ISelectionSet;
import jsettlers.graphics.utils.EFontSize;
import jsettlers.graphics.utils.TextDrawer;

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
public class MapContent implements SettlersContent, GOEventHandlerProvoder {
	private boolean ENABLE_DEBUG = false;

	private final IGraphicsGrid map;

	private Background background = new Background();

	private MovableDrawer movableDrawer = new MovableDrawer();

	private final MapDrawContext context;

	private BuildingDrawer buildingDrawer = new BuildingDrawer();

	private MapObjectDrawer objectDrawer = new MapObjectDrawer();

	/**
	 * The current connector that connects the outside world to us.
	 */
	private MapInterfaceConnector connector;

	private IntRectangle oldScreen;

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

		controls = new OriginalControls(map);

		this.connector = new MapInterfaceConnector(this);
	}

	private void resizeTo(int newWidth, int newHeight) {
		this.context.setSize(newWidth, newHeight);
		this.controls.resizeTo(newWidth, newHeight);
		// this.mapInterface.setWindowSize(newWidth, newHeight);
	}

	@Override
    public void drawContent(GLDrawContext gl, int newWidth, int newHeight) {
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

		this.context.begin(gl);

		this.context.debugTime("Context set up");

		this.objectDrawer.increaseAnimationStep();

		drawBackground();
		this.context.debugTime("Background drawn");

		drawMain();

		this.context.end();

		gl.glTranslatef(0, 0, .5f);
		drawSelectionHint(gl);
		controls.drawAt(gl);
		this.context.debugTime("Interface drawn");

		drawFramerate();
		drawTooltip();
	}

	private void drawSelectionHint(GLDrawContext gl) {
		if (this.currentSelectionAreaStart != null
		        && this.currentSelectionAreaEnd != null) {
			int x1 = this.currentSelectionAreaStart.x;
			int y1 = this.currentSelectionAreaStart.y;
			int x2 = this.currentSelectionAreaEnd.x;
			int y2 = this.currentSelectionAreaEnd.y;


			gl.color(1, 1, 1, 1);
			gl.drawLine(new float[] {
					x1, y1,0,x2, y1,0,x2, y2,0,x1, y2,0
			}, true);
		}
	}

	private long lastFrame = 0;

	private Point currentSelectionAreaStart;

	private void drawFramerate() {
		long currentFrame = System.nanoTime();
		double framerate = 1000000000.0 / (currentFrame - this.lastFrame);
		String frames = new DecimalFormat("###.##").format(framerate);
		TextDrawer drawer = TextDrawer.getTextDrawer(EFontSize.NORMAL);
		drawer.drawString(200, 5, "FPS: " + frames);

		this.lastFrame = currentFrame;
	}

	private void drawTooltip() {
		if (!tooltipString.isEmpty()) {
			TextDrawer drawer = TextDrawer.getTextDrawer(EFontSize.NORMAL);
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
					// TODO: catch nullpointer!
					ISPosition2D origin =movable
			                .getDirection().getInverseDirection().getNextHexPoint(pos);
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
			//drawPlayerBorderIfNeeded(tile);
		}

//		if (map.getConstructionPreviewBuilding() != null) {
//			Sequence<? extends Image> sequence =
//			        ImageProvider.getInstance().getSettlerSequence(4, 5);
//			float imageScale = Byte.MAX_VALUE / Math.max(sequence.length(), 1);
//
//			for (ISPosition2D pos : tiles) {
//				IHexTile tile = map.getTile(pos);
//				byte constructionMark = tile.getConstructionMark();
//
//				if (constructionMark >= 0) {
//					this.context.beginTileContext(tile);
//					int index = (int) (constructionMark * imageScale);
//					Image image = sequence.getImageSafe(index);
//					image.draw(context.getGl());
//					this.context.endTileContext();
//				}
//			}
//			
//			ISPosition2D underMouse = this.context.getPositionOnScreen(mousePosition.x, mousePosition.y);
//			IHexTile tile = map.getTile(underMouse);
//			if (tile != null) {
//				context.beginTileContext(tile);
//				for (ImageLink image : map.getConstructionPreviewBuilding().getImages()) {
//					ImageProvider.getInstance().getImage(image).draw(context.getGl());
//				}
//				context.endTileContext();
//			}
//		}

		this.context.debugTime("Tiles drawn");

		if (needDrawDebug) {
			drawDebugColors();
		}
		this.context.debugTime("Debug tiles drawn");
	}

	private void drawDebugColors() {
		IMapArea tiles =
		        new MapShapeFilter(context.getScreenArea(), map.getWidth(),
		                map.getHeight());
		GLDrawContext gl = this.context.getGl();
		
		float[] shape = new float[] {
				0, 4, .5f, 0, 0,
				-3, 2, .5f,  0, 0,
				-3, -2, .5f,  0, 0,
				0, -4, .5f,  0, 0,
				0, -4, .5f,  0, 0,
				3, -2, .5f,  0, 0,
				3, 2, .5f, 0, 0,
				0, 4, .5f,  0, 0,
		};

		float[] colorArray = new float[4];
		for (ISPosition2D pos : tiles) {
			Color color = map.getDebugColorAt(pos.getX(), pos.getY());
			if (color != null) {
				this.context.beginTileContext(pos);
				gl.color(color.getComponents(colorArray));
				gl.drawQuadsWithTexture(0, shape);
				context.endTileContext();
			}
		}
	}


	/**
	 * draws a debug tile around the current (0,0)
	 * 
	 * @param debugColor
	 */
	private void drawDebugTile(Color debugColor) {
		GLDrawContext gl = this.context.getGl();
		gl.color(debugColor.getRed() / 255.0f,
		        debugColor.getGreen() / 255.0f, debugColor.getBlue() / 255.0f,
		        debugColor.getAlpha() / 255.0f);
		//gl.glDrawArrays(GL2.GL_POLYGON, 0, 6);
		gl.color(1, 1, 1, .5f);
		//gl.glDrawArrays(GL2.GL_LINE_LOOP, 0, 6);
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
				fireAction(event, action);
			}
		} else if (event instanceof GOKeyEvent) {
			Action actionForKeyboard =
			        getActionForKeyboard(((GOKeyEvent) event).getKeyCode());
			if (actionForKeyboard != null) {
				fireAction(event, actionForKeyboard);
			}
		} else if (event instanceof GODrawEvent) {
			GODrawEvent drawEvent = (GODrawEvent) event;
			handleDraw(drawEvent);
		} else if (event instanceof GOHoverEvent) {
			GOHoverEvent hoverEvent = (GOHoverEvent) event;
			handleHover(hoverEvent);
		}
	}

	private void fireAction(GOEvent event, Action action) {
			event.setHandler(new ActionHandler(action, getInterfaceConnector()));
	}

	private Action getActionForKeyboard(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_F12:
				return new Action(EActionType.FAST_FORWARD);
			case KeyEvent.VK_PAUSE:
			case KeyEvent.VK_P:
				return new Action(EActionType.SPEED_TOGGLE_PAUSE);
			case KeyEvent.VK_PLUS:
				return new Action(EActionType.SPEED_FASTER);
			case KeyEvent.VK_MINUS:
				return new Action(EActionType.SPEED_SLOWER);
			case KeyEvent.VK_SPACE:
				return new Action(EActionType.SHOW_SELECTION);
			case KeyEvent.VK_D:
				return new Action(EActionType.DEBUG_ACTION);
			case KeyEvent.VK_S:
				return new Action(EActionType.STOP_WORKING);
			case KeyEvent.VK_Q:
				ENABLE_DEBUG = !ENABLE_DEBUG;
				break;

		}
		return null;
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

	private Point mousePosition = new Point(0, 0);

	private void handleHover(GOHoverEvent hoverEvent) {
		hoverEvent.setHandler(hoverHandler);
	}

	protected void changeMousePosition(Point position) {
		mousePosition = position;

		//TODO: make it work again
//		double relativeUIX =
//		        position.getX() / this.uiBase.getPosition().getWidth();
//		double relativeUIY =
//		        position.getY() / this.uiBase.getPosition().getHeight();
//		if (relativeUIX < UI_CENTERX && relativeUIY < UI_CENTERY) {
//			tooltipString =
//			        mainPanel.getDescription((float) relativeUIX / UI_CENTERX,
//			                (float) relativeUIY / UI_CENTERY);
//			if (tooltipString == null) {
//				tooltipString = "";
//			}
//		} else {
//			tooltipString = "";
//		}
	}

	private Action handleCommand(GOCommandEvent commandEvent) {
		Point position = commandEvent.getCommandPosition();
//		double relativeUIX =
//		        position.getX() / this.uiBase.getPosition().getWidth();
//		double relativeUIY =
//		        position.getY() / this.uiBase.getPosition().getHeight();
//		if (relativeUIX < UI_CENTERX && relativeUIY < UI_CENTERY) {
//			// we are on the sidebar
//			return mainPanel.getAction((float) relativeUIX / UI_CENTERX,
//			        (float) relativeUIY / UI_CENTERY);
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

	private Point currentSelectionAreaEnd;

	private Action handleCommandOnMap(GOCommandEvent commandEvent,
	        Point position) {
		ISPosition2D onMap =
		        this.context.getPositionOnScreen(position.x, position.y);
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

	private void updateSelectionArea(Point mousePosition, boolean finished) {
		if (finished && currentSelectionAreaStart != null) {
			int x1 = mousePosition.x;
			int x2 = this.currentSelectionAreaStart.x;
			int y1 = mousePosition.y;
			int y2 = this.currentSelectionAreaStart.y;

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


}
