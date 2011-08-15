package jsettlers.graphics.map;

import go.RedrawListener;
import go.event.GOEvent;
import go.event.GOEventHandler;
import go.event.GOEventHandlerProvoder;
import go.event.GOKeyEvent;
import go.event.GOModalEventHandler;
import go.event.command.GOCommandEvent;
import go.event.mouse.GODrawEvent;
import go.event.mouse.GOHoverEvent;
import go.event.mouse.GOPanEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.map.IHexMap;
import jsettlers.common.map.IHexTile;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.IStack;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionHandler;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.BuildingDrawer;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.map.draw.MovableDrawer;
import jsettlers.graphics.map.panel.MainPanel;
import jsettlers.graphics.map.panel.content.EContentType;
import jsettlers.graphics.map.selection.ISelectionSet;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.utils.EFontSize;
import jsettlers.graphics.utils.TextDrawer;
import jsettlers.graphics.utils.UIPanel;

import com.jogamp.common.nio.Buffers;

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
	private static final float UI_RATIO = (float) 768 / 333;

	private static final float UI_CENTERY = (float) 544 / 768;

	private static final float UI_CENTERX = (float) 216 / 333;

	private static final float UI_DECORATIONRIGHT = (float) 8 / 333
	        + UI_CENTERX;

	private static final int UI_BG_FILE = 4;

	private static final int UI_BG_SEQ_MAIN = 2;

	private static final int UI_BG_SEQ_MINIMAPR = 1;

	private static final int UI_BG_SEQ_MINIMAPL = 0;

	private static final int UI_BG_SEQ_RIGHT = 3;

	private boolean ENABLE_DEBUG = false;

	private final IHexMap map;

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

	private final UIPanel uiBase;

	private final MainPanel mainPanel = new MainPanel();

	/**
	 * Creates a new map content for the given map.
	 * 
	 * @param map
	 *            The map.
	 */
	public MapContent(IHexMap map) {
		this.map = map;
		this.context = new MapDrawContext(map);

		uiBase = createInterface();

		this.connector = new MapInterfaceConnector(this);
	}

	private UIPanel createInterface() {
		UIPanel panel = new UIPanel();

		UIPanel minimapbg_left = new UIPanel();
		minimapbg_left.setBackground(new ImageLink(EImageLinkType.SETTLER,
		        UI_BG_FILE, UI_BG_SEQ_MINIMAPL, 0));
		panel.addChild(minimapbg_left, 0, UI_CENTERY, UI_CENTERX, 1);

		UIPanel minimapbg_right = new UIPanel();
		minimapbg_right.setBackground(new ImageLink(EImageLinkType.SETTLER,
		        UI_BG_FILE, UI_BG_SEQ_MINIMAPR, 0));
		panel.addChild(minimapbg_right, UI_CENTERX, UI_CENTERY, 1, 1);

		mainPanel.setBackground(new ImageLink(EImageLinkType.SETTLER,
		        UI_BG_FILE, UI_BG_SEQ_MAIN, 0));
		panel.addChild(mainPanel, 0, 0, UI_CENTERX, UI_CENTERY);

		UIPanel rightDecoration = new UIPanel();
		rightDecoration.setBackground(new ImageLink(EImageLinkType.SETTLER,
		        UI_BG_FILE, UI_BG_SEQ_RIGHT, 0));
		panel.addChild(rightDecoration, UI_CENTERX, 0, UI_DECORATIONRIGHT,
		        UI_CENTERY);

		return panel;
	}

	private void resizeTo(int newWidth, int newHeight) {
		this.context.setSize(newWidth, newHeight);
		// this.mapInterface.setWindowSize(newWidth, newHeight);
		this.uiBase.setPosition(new IntRectangle(0, 0,
		        (int) (newHeight / UI_RATIO), newHeight));
	}

	@Override
	public void drawContent(GL2 gl, int newWidth, int newHeight) {
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

		gl.glDisable(GL.GL_STENCIL_BUFFER_BIT);
		gl.glDisable(GL.GL_DEPTH_TEST);
		this.context.begin(gl);

		this.context.debugTime("Context set up");

		this.objectDrawer.increaseAnimationStep();

		drawBackground();
		this.context.debugTime("Background drawn");

		gl.glAlphaFunc(GL.GL_GREATER, 0.1f);
		gl.glEnable(GL2ES1.GL_ALPHA_TEST);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_DEPTH_TEST);

		drawMain();

		this.context.end();

		gl.glDepthFunc(GL.GL_ALWAYS);
		gl.glColor3f(1, 1, 1);
		drawSelectionHint(gl);
		uiBase.drawAt(gl);
		this.context.debugTime("Interface drawn");

		drawFramerate();
		drawTooltip();
	}

	private void drawSelectionHint(GL2 gl) {
		if (this.currentSelectionAreaStart != null
		        && this.currentSelectionAreaEnd != null) {
			int x1 = this.currentSelectionAreaStart.x;
			int y1 = this.currentSelectionAreaStart.y;
			int x2 = this.currentSelectionAreaEnd.x;
			int y2 = this.currentSelectionAreaEnd.y;

			gl.glColor3f(1, 1, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2i(x1, y1);
			gl.glVertex2i(x2, y1);
			gl.glVertex2i(x2, y2);
			gl.glVertex2i(x1, y2);
			gl.glEnd();
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
			IHexTile tile = map.getTile(pos);

			IBuilding building = tile.getBuilding();
			if (building != null) {
				this.context.beginTileContext(tile);
				this.buildingDrawer.draw(this.context, building);
				this.context.endTileContext();
			}

			IMapObject object = tile.getHeadMapObject();
			if (object != null) {
				this.objectDrawer.drawMapObject(this.context, this.map, tile,
				        object);
			}

			IStack stack = tile.getStack();
			if (stack != null) {
				this.context.beginTileContext(tile);
				this.objectDrawer.drawStack(this.context, stack);
				this.context.endTileContext();
			}

			IMovable movable = tile.getMovable();
			if (movable != null) {
				if (movable.getAction() == EAction.WALKING) {
					// TODO: catch nullpointer!
					IHexTile origin =
					        context.getTileInDirection(tile, movable
					                .getDirection().getInverseDirection());
					if (origin == null) {
						origin = tile;
					}
					this.context.beginBetweenTileContext(origin, tile,
					        movable.getMoveProgress());
					this.movableDrawer.draw(this.context, movable);
					this.context.endTileContext();
				} else {
					this.context.beginTileContext(tile);
					this.movableDrawer.draw(this.context, movable);
					this.context.endTileContext();
				}
			}

			if (ENABLE_DEBUG && tile.getDebugColor() != null) {
				needDrawDebug = true;
			}

			drawPlayerBorderIfNeeded(tile);
		}

		if (map.getConstructionPreviewBuilding() != null) {
			Sequence<? extends Image> sequence =
			        ImageProvider.getInstance().getSettlerSequence(4, 5);
			float imageScale = Byte.MAX_VALUE / Math.max(sequence.length(), 1);

			for (ISPosition2D pos : tiles) {
				IHexTile tile = map.getTile(pos);
				byte constructionMark = tile.getConstructionMark();

				if (constructionMark >= 0) {
					this.context.beginTileContext(tile);
					int index = (int) (constructionMark * imageScale);
					Image image = sequence.getImageSafe(index);
					image.draw(context.getGl());
					this.context.endTileContext();
				}
			}
			
			ISPosition2D underMouse = this.context.getPositionOnScreen(mousePosition.x, mousePosition.y);
			IHexTile tile = map.getTile(underMouse);
			if (tile != null) {
				context.beginTileContext(tile);
				for (ImageLink image : map.getConstructionPreviewBuilding().getImages()) {
					ImageProvider.getInstance().getImage(image).draw(context.getGl());
				}
				context.endTileContext();
			}
		}

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
		GL2 gl = this.context.getGl();
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		ByteBuffer buffer =
		        ByteBuffer.allocateDirect(6 * 2 * Buffers.SIZEOF_INT);
		buffer.order(ByteOrder.nativeOrder());
		IntBuffer intBuffer = buffer.asIntBuffer();
		intBuffer.put(new int[] {
		        0, 4, -3, 2, -3, -2, 0, -4, 3, -2, 3, 2
		});
		intBuffer.rewind();
		gl.glVertexPointer(2, GL2.GL_INT, 0, intBuffer);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		for (ISPosition2D pos : tiles) {
			IHexTile tile = map.getTile(pos);
			if (tile.getDebugColor() != null) {
				this.context.beginTileContext(tile);
				drawDebugTile(tile.getDebugColor());
				this.context.endTileContext();
			}
		}
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	}

	private void drawPlayerBorderIfNeeded(IHexTile tile) {
		byte player = tile.getPlayer();
		if (player != -1 && tile.getX() > 0
		        && tile.getX() < this.map.getWidth() - 1 && tile.getY() > 0
		        && tile.getY() < this.map.getHeight() - 1
		        && isBorderOfPlayer(tile, player)) {
			this.context.beginTileContext(tile);
			this.objectDrawer.drawPlayerBorderObject(this.context, player);
			this.context.endTileContext();
		}
	}

	private boolean isBorderOfPlayer(IHexTile tile, byte player) {
		return (this.context.getTileInDirection(tile, EDirection.NORTH_EAST)
		        .getPlayer() != player
		        || this.context.getTileInDirection(tile, EDirection.EAST)
		                .getPlayer() != player
		        || this.context.getTileInDirection(tile, EDirection.SOUTH_EAST)
		                .getPlayer() != player
		        || this.context.getTileInDirection(tile, EDirection.SOUTH_WEST)
		                .getPlayer() != player
		        || this.context.getTileInDirection(tile, EDirection.WEST)
		                .getPlayer() != player || this.context
		        .getTileInDirection(tile, EDirection.NORTH_WEST).getPlayer() != player);
	}

	/**
	 * draws a debug tile around the current (0,0)
	 * 
	 * @param debugColor
	 */
	private void drawDebugTile(Color debugColor) {
		GL2 gl = this.context.getGl();
		gl.glColor4f(debugColor.getRed() / 255.0f,
		        debugColor.getGreen() / 255.0f, debugColor.getBlue() / 255.0f,
		        debugColor.getAlpha() / 255.0f);
		gl.glDrawArrays(GL2.GL_POLYGON, 0, 6);
		gl.glColor4f(1, 1, 1, .5f);
		gl.glDrawArrays(GL2.GL_LINE_LOOP, 0, 6);
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
		if (action.getActionType() == EActionType.CHANGE_PANEL) {
			// TODO. can we fire the action and catch it later?
			mainPanel.setContent(((ChangePanelAction) action).getContent());
		} else {
			event.setHandler(new ActionHandler(action, getInterfaceConnector()));
		}
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
			changeMousePosition(((GOHoverEvent) event).getMousePosition());
		}

		@Override
		public void aborted(GOEvent event) {
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			changeMousePosition(((GOHoverEvent) event).getMousePosition());
		}
	};

	private String tooltipString = "";

	private Point mousePosition = new Point(0, 0);

	private void handleHover(GOHoverEvent hoverEvent) {
		hoverEvent.setHandler(hoverHandler);
	}

	protected void changeMousePosition(Point position) {
		mousePosition = position;

		double relativeUIX =
		        position.getX() / this.uiBase.getPosition().getWidth();
		double relativeUIY =
		        position.getY() / this.uiBase.getPosition().getHeight();
		if (relativeUIX < UI_CENTERX && relativeUIY < UI_CENTERY) {
			tooltipString =
			        mainPanel.getDescription((float) relativeUIX / UI_CENTERX,
			                (float) relativeUIY / UI_CENTERY);
			if (tooltipString == null) {
				tooltipString = "";
			}
		} else {
			tooltipString = "";
		}
	}

	private Action handleCommand(GOCommandEvent commandEvent) {
		Point position = commandEvent.getCommandPosition();
		double relativeUIX =
		        position.getX() / this.uiBase.getPosition().getWidth();
		double relativeUIY =
		        position.getY() / this.uiBase.getPosition().getHeight();
		if (relativeUIX < UI_CENTERX && relativeUIY < UI_CENTERY) {
			// we are on the sidebar
			return mainPanel.getAction((float) relativeUIX / UI_CENTERX,
			        (float) relativeUIY / UI_CENTERY);
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
		this.currentSelectionAreaStart = drawEvent.getMousePosition();
		drawEvent.setHandler(this.drawSelectionHandler);
	}

	private GOEventHandler drawSelectionHandler = new GOModalEventHandler() {

		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			updateSelectionArea(((GODrawEvent) event).getMousePosition(), true);
		}

		@Override
		public void aborted(GOEvent event) {
			abortSelectionArea();
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			updateSelectionArea(((GODrawEvent) event).getMousePosition(), false);
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

	public void displayUIContent(EContentType content) {
		this.mainPanel.setContent(content);
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
