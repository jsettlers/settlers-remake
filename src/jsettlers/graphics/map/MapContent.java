package jsettlers.graphics.map;

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
import go.graphics.event.mouse.GOZoomEvent;
import go.graphics.sound.SoundPlayer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.text.DecimalFormat;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionHandler;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.PanToAction;
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.OriginalControls;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.map.draw.MovableDrawer;
import jsettlers.graphics.map.selection.ISelectionSet;
import jsettlers.graphics.messages.Message;
import jsettlers.graphics.messages.Messenger;
import jsettlers.graphics.sound.BackgroundSound;
import jsettlers.graphics.sound.SoundManager;

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
public final class MapContent implements SettlersContent,
        GOEventHandlerProvoder, IMapInterfaceListener, ActionFireable {
	private boolean ENABLE_DEBUG = false;
	private static final int SCREEN_PADDING = 50;
	private static final float OVERDRAW_BOTTOM_PX = 50;
	private static final int MAX_MESSAGES = 10;
	private static final float MESSAGE_OFFSET_X = 200;
	private static final int MESSAGE_OFFSET_Y = 30;
	private static final int MESSAGE_LINEHIEGHT = 18;
	private static final long GOTO_MARK_TIME = 1500;

	private final IGraphicsGrid map;

	private final Background background = new Background();

	private final MovableDrawer movableDrawer;

	private final MapDrawContext context;

	private final MapObjectDrawer objectDrawer;

	/**
	 * The current connector that connects the outside world to us.
	 */
	private MapInterfaceConnector connector;

	private FloatRectangle oldScreen;

	/**
	 * The controls that represent the interface.
	 */
	private final IControls controls;

	/**
	 * zoom factor. The smaller the smaller the settlers get.
	 */
	private float zoom = 1;

	private int windowWidth = 1;

	private int windowHeight = 1;

	private final Messenger messenger = new Messenger();
	private final SoundManager soundmanager;
	private final BackgroundSound bgsound;

	private ISPosition2D gotoMarker;
	private long gotoMarkerTime;

	/**
	 * Creates a new map content for the given map.
	 * 
	 * @param map
	 *            The map.
	 */
	public MapContent(IGraphicsGrid map, SoundPlayer player) {
		this(map, player, null);
	}

	public MapContent(IGraphicsGrid map, SoundPlayer player, IControls controls) {
		this.map = map;
		this.context = new MapDrawContext(map);
		this.soundmanager = new SoundManager(player);
		movableDrawer = new MovableDrawer(soundmanager);
		objectDrawer = new MapObjectDrawer(soundmanager);
		bgsound = new BackgroundSound(context, soundmanager);

		if (controls == null) {
			this.controls = new OriginalControls();
		} else {
			this.controls = controls;
		}
		this.controls.setDrawContext(context);
		// controls = new SmallControls();

		this.connector = new MapInterfaceConnector(this);
		this.connector.addListener(this);

		map.setBackgroundListener(background);

		// sound testing code
		// new Timer().schedule(new TimerTask() {
		// private int soundid = 0;
		// @Override
		// public void run() {
		// soundmanager.playSound(soundid++);
		// }
		// }, 1000, 5000);
	}

	private void resizeTo(int newWindowWidth, int newWindowHeight) {
		windowWidth = newWindowWidth;
		windowHeight = newWindowHeight;
		this.controls.resizeTo(windowWidth, windowHeight);
		reapplyContentSizes();
	}

	private void reapplyContentSizes() {
		this.context.setSize(windowWidth, windowHeight, zoom);
	}

	@Override
	public void drawContent(GLDrawContext gl, int newWidth, int newHeight) {
		if (newWidth != windowWidth || newHeight != windowHeight) {
			resizeTo(newWidth, newHeight);
		}

		adaptScreenSize();
		this.objectDrawer.increaseAnimationStep();

		this.context.begin(gl);
		long start = System.currentTimeMillis();

		FloatRectangle screen =
		        this.context.getScreen().getPosition().bigger(SCREEN_PADDING);
		drawBackground(screen);
		long bgtime = System.currentTimeMillis() - start;

		start = System.currentTimeMillis();
		drawMain(screen);

		if (gotoMarker != null) {
			drawGotoMarker();
		}

		this.context.end();
		long foregroundtime = System.currentTimeMillis() - start;

		start = System.currentTimeMillis();
		gl.glTranslatef(0, 0, .5f);
		drawSelectionHint(gl);
		controls.drawAt(gl);
		drawMessages(gl);

		drawFramerate(gl);
		drawTooltip(gl);
		long uitime = System.currentTimeMillis() - start;

		if (CommonConstants.ENABLE_GRAPHICS_TIMES_DEBUG_OUTPUT) {
			System.out.println("Background: " + bgtime + "ms, Foreground: "
			        + foregroundtime + "ms, UI: " + uitime + "ms");
		}
	}

	private void drawGotoMarker() {
		long timediff = System.currentTimeMillis() - gotoMarkerTime;
		if (timediff > GOTO_MARK_TIME) {
			gotoMarker = null;
		} else {
			context.beginTileContext(gotoMarker.getX(), gotoMarker.getY());
			ImageProvider.getInstance().getSettlerSequence(3, 1)
			        .getImageSafe(timediff < GOTO_MARK_TIME / 2 ? 0 : 1)
			        .draw(context.getGl(), null, 1);
			context.endTileContext();
		}
	}

	private void drawMessages(GLDrawContext gl) {
		TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);
		// TODO: don't let logic wait until we rendered.
		synchronized (messenger) {
			int messageIndex = 0;
			for (Message m : messenger.getMessages()) {
				float x = MESSAGE_OFFSET_X;
				int y = MESSAGE_OFFSET_Y + messageIndex * MESSAGE_LINEHIEGHT;
				if (m.getSender() >= 0) {
					String name = getPlayername(m.getSender()) + ":";
					jsettlers.common.Color color =
					        context.getPlayerColor(m.getSender());
					float width = (float) drawer.getWidth(name);
					float bright =
					        color.getRed() + color.getGreen() + color.getBlue();
					if (bright < .9f) {
						// black
						gl.color(1, 1, 1, .5f);
						gl.fillQuad(x, y, x + width, y + MESSAGE_LINEHIEGHT);
					} else if (bright < 2f) {
						// bad visibility
						gl.color(0, 0, 0, .5f);
						gl.fillQuad(x, y, x + width, y + MESSAGE_LINEHIEGHT);
					}
					drawer.setColor(color.getRed(), color.getGreen(),
					        color.getBlue(), 1);
					drawer.drawString(x, y, name);
					x += width + 10;
				}

				drawer.setColor(1, 1, 1, 1);
				drawer.drawString(x, y, m.getMessage());

				messageIndex++;
				if (messageIndex >= MAX_MESSAGES) {
					break;
				}
			}
		}
	}

	private String getPlayername(byte sender) {
		// TODO: Player names
		return "player " + sender;
	}

	private void adaptScreenSize() {
		FloatRectangle newScreen = context.getScreen().getPosition();
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
	private void drawMain(FloatRectangle screen) {
		boolean needDrawDebug = false;
		short height = map.getHeight();
		short width = map.getWidth();
		MapRectangle area = this.context.getConverter().getMapForScreen(screen);

		double bottomdrawy = screen.getMinY() - OVERDRAW_BOTTOM_PX;

		boolean linePartuallyVisible = true;
		for (int line = 0; line < area.getLines() + 50 && linePartuallyVisible; line++) {
			int y = area.getLineY(line);
			if (y < 0) {
				continue;
			}
			if (y >= height) {
				break;
			}
			linePartuallyVisible = false;

			int endX = Math.min(area.getLineEndX(line), width - 1);
			int startX = Math.max(area.getLineStartX(line), 0);
			for (int x = startX; x <= endX; x++) {
				needDrawDebug |= drawTile(x, y);
				if (!linePartuallyVisible) {
					double drawspacey =
					        this.context.getConverter().getViewY(x, y,
					                this.context.getHeight(x, y));
					if (drawspacey > bottomdrawy) {
						linePartuallyVisible = true;
					}
				}
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

	private boolean drawTile(int x, int y) {
		IMapObject object = map.getMapObjectsAt(x, y);
		if (object != null) {
			this.objectDrawer.drawMapObject(this.context, this.map, x, y,
			        object);
		}

		IMovable movable = map.getMovableAt(x, y);
		if (movable != null) {
			if (movable.getAction() == EAction.WALKING) {
				int originx = x - movable.getDirection().getGridDeltaX();
				int originy = y - movable.getDirection().getGridDeltaY();
				this.context.beginBetweenTileContext(originx, originy, x, y,
				        movable.getMoveProgress());
				this.movableDrawer.draw(this.context, movable);
				this.context.endTileContext();
			} else {
				this.context.beginTileContext(x, y);
				this.movableDrawer.draw(this.context, movable);
				this.context.endTileContext();
			}
		}

		if (map.isBorder(x, y)) {
			this.context.beginTileContext(x, y);
			byte player = map.getPlayerAt(x, y);
			objectDrawer.drawPlayerBorderObject(context, player);
			this.context.endTileContext();
		}
		return ENABLE_DEBUG && map.getDebugColorAt(x, y) != null;
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
			short x = pos.getX();
			short y = pos.getY();
			Color color = map.getDebugColorAt(x, y);
			if (color != null) {
				this.context.beginTileContext(x, y);
				gl.color(color.red, color.green, color.blue, color.alpha);
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
	private void drawBackground(FloatRectangle screen) {
		this.background.drawMapContent(this.context, screen);
	}

	@Override
	public void handleEvent(GOEvent event) {
		if (event instanceof GOPanEvent) {
			event.setHandler(new PanHandler(this.context.getScreen()));
		} else if (event instanceof GOCommandEvent) {
			GOCommandEvent commandEvent = (GOCommandEvent) event;
			Action action = getActionForCommand(commandEvent);
			// also set when action was null, to abort drawing.
			fireActionEvent(event, action);
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
		} else if (event instanceof GOZoomEvent) {
			GOZoomEvent zoomEvent = (GOZoomEvent) event;
			handleZoom(zoomEvent);
		}
	}

	private void handleZoom(GOZoomEvent zoomEvent) {
		zoomEvent.setHandler(new GOModalEventHandler() {
			float startzoom = zoom;

			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				eventDataChanged(((GOZoomEvent) event).getZoomFactor());
			}

			private void eventDataChanged(float zoomFactor) {
				System.out.println("Zooming by " + zoomFactor);
				float newZoom = startzoom * zoomFactor;
				setZoom(newZoom);
			}

			@Override
			public void aborted(GOEvent event) {
				eventDataChanged(1);
			}

			@Override
			public void eventDataChanged(GOEvent event) {
				eventDataChanged(((GOZoomEvent) event).getZoomFactor());
			}
		});
	}

	private void fireActionEvent(GOEvent event, Action action) {
		event.setHandler(new ActionHandler(action, this));
	}

	/**
	 * Gets a action for a keyboard key
	 * 
	 * @param keyCode
	 *            The key
	 * @return The action that corresponds to the key
	 */
	private static Action getActionForKeyboard(String keyCode) {
		if ("F12".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.FAST_FORWARD);
		} else if ("P".equalsIgnoreCase(keyCode)
		        || "PAUSE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SPEED_TOGGLE_PAUSE);
		} else if ("+".equals(keyCode)) {
			return new Action(EActionType.SPEED_FASTER);
		} else if ("-".equals(keyCode)) {
			return new Action(EActionType.SPEED_SLOWER);
		} else if (" ".equals(keyCode) || "space".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SHOW_SELECTION);
		} else if ("d".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.DEBUG_ACTION);
		} else if ("s".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.STOP_WORKING);
		} else if ("e".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_DEBUG);
		} else if ("q".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.EXIT);
		} else if ("w".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_FOG_OF_WAR);
		} else if ("F5".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.ZOOM_IN);
		} else if ("F6".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.ZOOM_OUT);
		} else if ("F2".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SAVE);
		} else if ("DELETE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.DESTROY);
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

	private Action getActionForCommand(GOCommandEvent commandEvent) {
		UIPoint position = commandEvent.getCommandPosition();
		if (controls.containsPoint(position)) {
			return controls.getActionFor(position);
		} else {
			// handle map click
			return handleCommandOnMap(commandEvent, position);
		}
	}

	private void handleDraw(GODrawEvent drawEvent) {
		handleDrawOnMap(drawEvent);
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

		float x = (float) position.getX();
		float y = (float) position.getY();
		ISPosition2D onMap = this.context.getPositionOnScreen(x, y);
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
		controls.displaySelection(selection);
	}

	public void scrollTo(ISPosition2D point, boolean mark) {
		this.context.scrollTo(point);
		if (mark) {
			gotoMarker = point;
			gotoMarkerTime = System.currentTimeMillis();
		}
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
		} else if (action.getActionType() == EActionType.ZOOM_IN) {
			if (zoom < 1.1) {
				setZoom(zoom * 2);
			}
		} else if (action.getActionType() == EActionType.ZOOM_OUT) {
			if (zoom > 0.6) {
				setZoom(zoom / 2);
			}
		}
	}

	private void setZoom(float newzoom) {
		if (newzoom < .3f) {
			this.zoom = .3f;
		} else if (newzoom > 3f) {
			this.zoom = 3f;
		} else {
			this.zoom = newzoom;
		}
		reapplyContentSizes();
	}

	public void setPreviewBuildingType(EBuildingType buildingType) {
		controls.displayBuildingBuild(buildingType);
	}

	public void addMessage(Message message) {
		synchronized (messenger) {
			messenger.addMessage(message);
		}
		switch (message.getType()) {
			case ATTACKED:
				soundmanager.playSound(SoundManager.NOTIFY_ATTACKED, 1, 1);
				break;

			default:
				break;
		}
	}

	public void loadUIState(UIState uiState) {
		scrollTo(uiState.getScreenCenter(), false);
		// TODO: player number
	}

	@Override
	public void fireAction(Action action) {
		Action fire = controls.replaceAction(action);
		if (fire != null) {
			getInterfaceConnector().fireAction(fire);
		}
	}

}
