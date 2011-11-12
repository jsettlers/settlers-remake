package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.sequence.Sequence;

/**
 * This class handles drawing of objects on the map.
 * 
 * @author michael
 */
public class MapObjectDrawer {

	private static final int FILE = 1;

	private static final int TREE_TYPES = 7;

	/**
	 * First images in tree cutting sequence
	 */
	private static final int TREE_FALL_IMAGES = 4;

	/**
	 * Tree falling speed. bigger => faster.
	 */
	private static final float TREE_FALLING_SPEED = 10f;
	/**
	 * First images in tree cutting sequence
	 */
	private static final int TREE_ROT_IMAGES = 5;

	/**
	 * First images in tree cutting sequence
	 */
	private static final int TREE_SMALL = 12;

	/**
	 * First images in tree cutting sequence
	 */
	private static final int TREE_MEDIUM = 11;

	/**
	 * Index of first tree in the set.
	 */
	private static final int ALIVE_TREE_OFFSET = 1;

	private static final int CHANGING_TREE_OFFSET = 3;

	private static final int SMALL_GROWING_TREE = 22;

	private static final int CORN = 23;

	private static final int CORN_GROW_STEPS = 7;

	private static final int CORN_DEAD_STEP = 8;

	private static final int WAVES = 26;

	private static final int FILE_BORDERPOST = 13;

	private static final int STONE = 31;

	private static final int SELECTMARK_SEQUENCE = 11;

	private static final int SELECTMARK_FILE = 4;

	private static final int MILL_FILE = 13;

	private static final int MILL_SEQ = 15;

	private static final int PIG_SEQ = 0;

	private static final int ANIMALS_FILE = 6;

	int animationStep = 0;

	private final ImageProvider imageProvider = ImageProvider.getInstance();

	/**
	 * Draws a map object at a given position.
	 * 
	 * @param context
	 *            The context.
	 * @param map
	 *            For these damned arrwos
	 * @param pos
	 *            THe position to draw the object.
	 * @param object
	 *            The object (tree, ...) to draw.
	 */
	public void drawMapObject(MapDrawContext context, IGraphicsGrid map,
	        ISPosition2D pos, IMapObject object) {
		EMapObjectType type = object.getObjectType();

		float progress = object.getStateProgress();

		byte fogstatus =
		        context.getFogOfWar().getVisibleStatus(pos.getX(), pos.getY());
		if (fogstatus == 0) {
			return; // break
		}
		Color color = getColor(context, fogstatus);

		if (type == EMapObjectType.ARROW) {
			drawArrow(context, map, (IArrowMapObject) object, color);
		} else {
			context.beginTileContext(pos);
			switch (type) {
				case TREE_ADULT:
					drawTree(context, pos, color);
					break;

				case TREE_DEAD:
					drawFallingTree(context, pos, progress, color);
					break;

				case TREE_GROWING:
					drawGrowingTree(context, pos, progress, color);
					break;

				case CORN_GROWING:
					drawGrowingCorn(context, object, color);
					break;

				case CORN_ADULT:
					drawCorn(context, object, color);
					break;

				case CORN_DEAD:
					drawDeadCorn(context, color);
					break;

				case WAVES:
					drawWaves(context, color);
					break;

				case STONE:
					drawStones(context, object, color);
					break;

				case GHOST:
					drawPlayerableByProgress(context, 12, 27, object, color);
					break;

				case FOUND_COAL:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_GEMSTONE:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_GOLD:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_IRON:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_BRIMSTONE:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_NOTHING:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case BUILDINGSITE_SIGN:
					drawByProgress(context, FILE, 93,
					        object.getStateProgress(), color);
					break;

				case BUILDINGSITE_POST:
					drawByProgress(context, FILE, 92,
					        object.getStateProgress(), color);
					break;

				case WORKAREA_MARK:
					drawByProgress(context, FILE, 91,
					        object.getStateProgress(), color);
					break;

				case FLAG_DOOR:
					drawPlayerableWaving(context, 13, 63, object, color);
					break;

				case CONSTRUCTION_MARK:
					drawByProgress(context, 4, 6, object.getStateProgress(),
					        color);
					break;

				case FLAG_ROOF:
					context.getGl().glTranslatef(0, 0, 0.2f);
					drawPlayerableWaving(context, 13, 64, object, color);
					break;

				case BUILDING:
					draw(context, (IBuilding) object, color);
					break;

				case STACK_OBJECT:
					drawStack(context, (IStackMapObject) object, color);
					break;

				case SMOKE:
					drawByProgress(context, 13, 42, progress, color);
					break;

				case PIG:

					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(ANIMALS_FILE,
					                PIG_SEQ);

					if (seq.length() > 0) {
						int i = getAnimationStep(pos);
						int step = i % seq.length();
						seq.getImageSafe(step).draw(context.getGl(), color);
					}
					break;

				default:
					break;
			}
			context.endTileContext();
		}
		if (object.getNextObject() != null) {
			drawMapObject(context, map, pos, object.getNextObject());
		}
	}

	private void drawPlayerableByProgress(MapDrawContext context, int file,
	        int sequenceIndex, IMapObject object, Color basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (object.getStateProgress() * sequence.length()),
		                sequence.length() - 1);
		Color color = getColor(context, object);
		sequence.getImage(index).draw(context.getGl(),
		        color.multiply(basecolor.getAlpha()));
	}

	private Color getColor(MapDrawContext context, IMapObject object) {
		Color color = null;
		if (object instanceof IPlayerable) {
			color = context.getPlayerColor(((IPlayerable) object).getPlayer());
		}
		return color;
	}

	private void drawPlayerableWaving(MapDrawContext context, int file,
	        int sequenceIndex, IMapObject object, Color basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index = animationStep % sequence.length();
		Color color = getColor(context, object);
		sequence.getImage(index).draw(context.getGl(),
		        color.multiply(basecolor.getRed()));
	}

	private void drawByProgress(MapDrawContext context, int file,
	        int sequenceIndex, float progress, Color color) {

		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (progress * sequence.length()),
		                sequence.length() - 1);
		sequence.getImageSafe(index).draw(context.getGl(), color);
	}

	private void drawArrow(MapDrawContext context, IGraphicsGrid map,
	        IArrowMapObject object, Color color) {
		int sequence = 0;
		switch (object.getDirection()) {
			case SOUTH_WEST:
				sequence = 100;
				break;

			case WEST:
				sequence = 101;
				break;

			case NORTH_WEST:
				sequence = 102;
				break;

			case NORTH_EAST:
				sequence = 103;
				break;

			case EAST:
				sequence = 104;
				break;

			case SOUTH_EAST:
				sequence = 104;
				break;
		}

		float progress = object.getStateProgress();
		int index = Math.round(progress * 2);

		ISPosition2D start = object.getSource();
		ISPosition2D end = object.getTarget();
		context.beginBetweenTileContext(start, end, progress);
		context.getGl()
		        .glTranslatef(0, -20 * progress * (progress - 1) + 10, 0);
		if (progress >= 1) {
			context.getGl().glTranslatef(0, 0, -.2f);
		}

		this.imageProvider.getSettlerSequence(FILE, sequence)
		        .getImageSafe(index).draw(context.getGl(), color);
		context.endTileContext();
	}

	private void drawStones(MapDrawContext context, IMapObject object,
	        Color color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, STONE);
		int stones = (int) (seq.length() - object.getStateProgress() - 1);
		seq.getImageSafe(stones).draw(context.getGl(), color);
	}

	private void drawWaves(MapDrawContext context, Color color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, WAVES);
		seq.getImageSafe(15).draw(context.getGl(), color);
	}

	private void drawDeadCorn(MapDrawContext context, Color color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		seq.getImageSafe(CORN_DEAD_STEP).draw(context.getGl(), color);
	}

	private void drawGrowingCorn(MapDrawContext context, IMapObject object,
	        Color color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = (int) (object.getStateProgress() * CORN_GROW_STEPS);
		seq.getImageSafe(step).draw(context.getGl(), color);
	}

	private void drawCorn(MapDrawContext context, IMapObject object, Color color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = CORN_GROW_STEPS;
		seq.getImageSafe(step).draw(context.getGl(), color);
	}

	private void drawGrowingTree(MapDrawContext context, ISPosition2D pos,
	        float progress, Color color) {
		Image image;
		if (progress < 0.33) {
			Sequence<? extends Image> seq =
			        this.imageProvider.getSettlerSequence(FILE,
			                SMALL_GROWING_TREE);
			image = seq.getImageSafe(0);
		} else {
			int treeType = getTreeType(pos);
			Sequence<? extends Image> seq =
			        this.imageProvider.getSettlerSequence(FILE,
			                sequenceIndexForChangingTree(treeType));
			if (progress < 0.66) {
				image = seq.getImageSafe(TREE_SMALL);
			} else {
				image = seq.getImageSafe(TREE_MEDIUM);
			}
		}
		image.draw(context.getGl(), color);
	}

	private void drawFallingTree(MapDrawContext context, ISPosition2D pos,
	        float progress, Color color) {
		int treeType = getTreeType(pos);
		int imageStep = 0;

		// TODO
		if (progress < IMapObject.TREE_CUT_1) {
			imageStep = (int) (progress * TREE_FALLING_SPEED);
			if (imageStep >= TREE_FALL_IMAGES) {
				imageStep = TREE_FALL_IMAGES - 1;
			}
		} else if (progress < IMapObject.TREE_CUT_2) {
			imageStep = TREE_FALL_IMAGES;
		} else if (progress < IMapObject.TREE_CUT_2) {
			imageStep = TREE_FALL_IMAGES + 1;
		} else if (progress < IMapObject.TREE_TAKEN) {
			imageStep = TREE_FALL_IMAGES + 2;
		} else {
			int relativeStep =
			        (int) ((progress - IMapObject.TREE_TAKEN)
			                / (1 - IMapObject.TREE_TAKEN) * TREE_ROT_IMAGES);

			imageStep = relativeStep + TREE_FALL_IMAGES + 3;
		}

		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE,
		                sequenceIndexForChangingTree(treeType));
		seq.getImageSafe(imageStep).draw(context.getGl(), color);
	}

	private void drawTree(MapDrawContext context, ISPosition2D pos, Color color) {
		int treeType = getTreeType(pos);
		int treeSecondary = get01(pos);
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, treeType * 3
		                + treeSecondary + ALIVE_TREE_OFFSET);

		int step = getAnimationStep(pos) % seq.length();
		seq.getImageSafe(step).draw(context.getGl(), color);
	}

	/**
	 * gets a 0 or a 1.
	 * 
	 * @param pos
	 * @return
	 */
	private int get01(ISPosition2D pos) {
		return (pos.getX() * 677 + pos.getY()) % 2;
	}

	private int sequenceIndexForChangingTree(int treeType) {
		return treeType * 3 + CHANGING_TREE_OFFSET;
	}

	/**
	 * Draws a player border at a given position.
	 * 
	 * @param context
	 *            The context
	 * @param player
	 *            The player.
	 */
	public void drawPlayerBorderObject(MapDrawContext context, byte player) {
		GLDrawContext gl = context.getGl();
		Color color = context.getPlayerColor(player);

		this.imageProvider.getSettlerSequence(FILE_BORDERPOST, 65)
		        .getImageSafe(0).draw(gl, color);
	}

	private int getTreeType(ISPosition2D pos) {
		return (pos.getX() * 251 + pos.getY() * 233) % TREE_TYPES;
	}

	private int getAnimationStep(ISPosition2D pos) {
		return this.animationStep + pos.getX() * 167 + pos.getY() * 41;
	}

	/**
	 * Increases the animation step for trees and other stuff.
	 */
	public void increaseAnimationStep() {
		this.animationStep++;
	}

	/**
	 * Draws a stack
	 * 
	 * @param context
	 *            The context to draw with
	 * @param object
	 *            The stack to draw.
	 */
	public void drawStack(MapDrawContext context, IStackMapObject object,
	        Color color) {
		byte elements = object.getSize();
		if (elements > 0) {
			drawStackAtScreen(context.getGl(), object.getMaterialType(),
			        elements, color);
		}
	}

	/**
	 * Draws the stack directly to the screen.
	 * 
	 * @param glDrawContext
	 *            The gl context to draw at.
	 * @param material
	 *            The material the stack should have.
	 * @param count
	 *            The number of elements on the stack
	 */
	private void drawStackAtScreen(GLDrawContext glDrawContext,
	        EMaterialType material, int count, Color color) {
		int stackIndex = material.getStackIndex();

		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, stackIndex);
		seq.getImageSafe(count - 1).draw(glDrawContext, color);
	}

	/**
	 * Gets the gray color for a given fog.
	 * 
	 * @param context
	 * @param fogstatus
	 * @return
	 */
	private Color getColor(MapDrawContext context, int fogstatus) {
		float color = (float) fogstatus / FogOfWar.VISIBLE;
		return new Color(color, color, color, 1);
	}

	/**
	 * Draws a given buildng to the context.
	 * 
	 * @param context
	 * @param building
	 * @param color
	 *            Gray color shade
	 */
	private void draw(MapDrawContext context, IBuilding building, Color color) {
		EBuildingType type = building.getBuildingType();

		float state = building.getStateProgress();
		float maskState;
		if (state < 0.5f) {
			maskState = state * 2;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(context, maskState, image, color);
			}

		} else if (state < 0.99) {
			maskState = state * 2 - 1;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				image.draw(context.getGl(), color);
			}

			for (ImageLink link : type.getImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(context, maskState, image, color);
			}
		} else {

			if (type == EBuildingType.MILL
			        && ((IBuilding.Mill) building).isWorking()) {
				Sequence<? extends Image> seq =
				        this.imageProvider.getSettlerSequence(MILL_FILE,
				                MILL_SEQ);

				if (seq.length() > 0) {
					int i = getAnimationStep(building.getPos());
					int step = i % seq.length();
					seq.getImageSafe(step).draw(context.getGl(), color);
				}
			} else {
				for (ImageLink link : type.getImages()) {
					Image image = imageProvider.getImage(link);
					image.draw(context.getGl(), color);
				}
			}
		}

		if (building.isSelected()) {
			drawBuildingSelectMarker(context);
		}
	}

	private void drawBuildingSelectMarker(MapDrawContext context) {
		context.getGl().glTranslatef(0, 20, .2f);
		Image image =
		        imageProvider.getSettlerSequence(SELECTMARK_FILE,
		                SELECTMARK_SEQUENCE).getImageSafe(0);
		image.draw(context.getGl());
	}

	private void drawWithConstructionMask(MapDrawContext context,
	        float maskState, Image image, Color color) {
		// number of tiles in x direction, can be adjustet for performance
		int tiles = 6;

		float toplineBottom = maskState;
		float toplineTop = Math.min(1, toplineBottom + .1f);

		float[] tris = new float[(tiles + 2) * 3 * 5];

		addPointToArray(tris, 0, 0, 0, image);
		addPointToArray(tris, 1, 1, 0, image);
		addPointToArray(tris, 2, 0, toplineBottom, image);
		addPointToArray(tris, 3, 1, 0, image);
		addPointToArray(tris, 4, 1, toplineBottom, image);
		addPointToArray(tris, 5, 0, toplineBottom, image);

		for (int i = 0; i < tiles; i++) {
			addPointToArray(tris, 6 + i * 3, 1.0f / tiles * i, toplineBottom,
			        image);
			addPointToArray(tris, 7 + i * 3, 1.0f / tiles * (i + 1),
			        toplineBottom, image);
			addPointToArray(tris, 8 + i * 3, 1.0f / tiles * (i + .5f),
			        toplineTop, image);
		}

		GLDrawContext gl = context.getGl();
		gl.color(color);
		gl.drawTrianglesWithTexture(image.getTextureIndex(gl), tris);
	}

	private void addPointToArray(float[] array, int pointindex, float u,
	        float v, Image image) {
		int left = image.getOffsetX();
		int top = -image.getOffsetY();
		int bottom = top - image.getHeight();

		int x = left + (int) (image.getWidth() * u);
		int y = bottom + (int) (image.getHeight() * v);

		int offset = pointindex * 5;
		array[offset] = x;
		array[offset + 1] = y;
		array[offset + 2] = 0;
		array[offset + 3] = u * image.getTextureScaleX();
		array[offset + 4] = v * image.getTextureScaleY();
	}

}
