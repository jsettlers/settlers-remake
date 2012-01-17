package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuilding.IOccupyed;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.sound.ISoundable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class handles drawing of objects on the map.
 * 
 * @author michael
 */
public class MapObjectDrawer {

	private static final ImageLink INSIDE_BUILDING_RIGHT = new ImageLink(
	        EImageLinkType.SETTLER, 12, 28, 1);
	private static final ImageLink INSIDE_BUILDING_LEFT = new ImageLink(
	        EImageLinkType.SETTLER, 12, 28, 0);

	private static final int FILE = 1;

	private static final int TREE_TYPES = 7;

	private static final int[] TREE_SEQUENCES = new int[] {
	        1, 2, 4, 7, 8, 16, 17,
	};
	private static final int[] TREE_CHANGING_SEQUENCES = new int[] {
	        3, 3, 6, 9, 9, 18, 18,
	};

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
	private final MovableDrawer movableDrawer;
	private final SoundManager sound;

	public MapObjectDrawer(SoundManager sound) {
		this.sound = sound;
		movableDrawer = new MovableDrawer(sound);
	}

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
	public void drawMapObject(MapDrawContext context, IGraphicsGrid map, int x,
	        int y, IMapObject object) {
		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus == 0) {
			return; // break
		}
		float color = getColor(fogstatus);

		EMapObjectType type = object.getObjectType();

		float progress = object.getStateProgress();

		if (type == EMapObjectType.ARROW) {
			drawArrow(context, (IArrowMapObject) object, color);
		} else {
			context.beginTileContext(x, y);
			switch (type) {

				case TREE_ADULT:
					drawTree(context, x, y, color);
					break;

				case TREE_DEAD:
					// TODO: falling tree sound.
					drawFallingTree(context, x, y, progress, color);
					break;

				case TREE_GROWING:
					drawGrowingTree(context, x, y, progress, color);
					break;

				case CORN_GROWING:
					drawGrowingCorn(context, object, color);
					break;

				case CORN_ADULT:
					drawCorn(context, color);
					break;

				case CORN_DEAD:
					drawDeadCorn(context, color);
					break;

				case WAVES:
					drawWaves(context, x, y, color);
					break;

				case STONE:
					drawStones(context, object, color);
					break;

				case GHOST:
					drawPlayerableByProgress(context, 12, 27, object, color);
					playSound(object, 35);
					break;

				case BUILDING_DECONSTRUCTION_SMOKE:
					drawByProgress(context, 13, 38, object.getStateProgress(),
					        color);
					// <TODO this is not the right sound.
					playSound(object, 35);
					break;

				case FOUND_COAL:
					drawByProgress(context, FILE, 94,
					        object.getStateProgress(), color);
					break;

				case FOUND_GEMSTONE:
					drawByProgress(context, FILE, 95,
					        object.getStateProgress(), color);
					break;

				case FOUND_GOLD:
					drawByProgress(context, FILE, 96,
					        object.getStateProgress(), color);
					break;

				case FOUND_IRON:
					drawByProgress(context, FILE, 97,
					        object.getStateProgress(), color);
					break;

				case FOUND_BRIMSTONE:
					drawByProgress(context, FILE, 98,
					        object.getStateProgress(), color);
					break;

				case FOUND_NOTHING:
					drawByProgress(context, FILE, 99,
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
					drawBuilding(context, x, y, (IBuilding) object, color);
					break;

				case STACK_OBJECT:
					drawStack(context, (IStackMapObject) object, color);
					break;

				case SMOKE:
					drawByProgress(context, 13, 42, progress, color);
					break;

				case WINE:
					drawByProgress(context, 1, 25, progress, color);
					break;

				case PLANT_DECORATION: {
					int step = getAnimationStep(x, y) % 8;

					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(1, 27);

					seq.getImageSafe(step).draw(context.getGl(), null, color);
				}
					break;

				case DESERT_DECORATION: {
					int step = getAnimationStep(x, y) % 5 + 10;

					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(1, 27);

					seq.getImageSafe(step).draw(context.getGl(), null, color);
				}
					break;

				case PIG: {
					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(ANIMALS_FILE,
					                PIG_SEQ);

					if (seq.length() > 0) {
						int i = getAnimationStep(x, y) / 2;
						int step = i % seq.length();
						seq.getImageSafe(step).draw(context.getGl(), null,
						        color);
					}
				}
					break;

				default:
					break;
			}
			context.endTileContext();
		}
		if (object.getNextObject() != null) {
			drawMapObject(context, map, x, y, object.getNextObject());
		}
	}

	private void playSound(IMapObject object, int soundid) {
		if (object instanceof ISoundable) {
			ISoundable soundable = (ISoundable) object;
			if (!soundable.isSoundPlayed()) {
				sound.playSound(soundid, 1, 1);
				soundable.setSoundPlayed();
			}
		}
	}

	private void drawPlayerableByProgress(MapDrawContext context, int file,
	        int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (object.getStateProgress() * sequence.length()),
		                sequence.length() - 1);
		Color color = getColor(context, object);
		sequence.getImage(index).draw(context.getGl(), color, basecolor);
	}

	private static Color getColor(MapDrawContext context, IMapObject object) {
		Color color = null;
		if (object instanceof IPlayerable) {
			color = context.getPlayerColor(((IPlayerable) object).getPlayer());
		}
		return color;
	}

	private void drawPlayerableWaving(MapDrawContext context, int file,
	        int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index = animationStep % sequence.length();
		Color color = getColor(context, object);
		sequence.getImageSafe(index).draw(context.getGl(), color, basecolor);
	}

	private void drawByProgress(MapDrawContext context, int file,
	        int sequenceIndex, float progress, float color) {

		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (progress * sequence.length()),
		                sequence.length() - 1);
		sequence.getImageSafe(index).draw(context.getGl(), null, color);
	}

	private void drawArrow(MapDrawContext context, IArrowMapObject object,
	        float color) {
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
		context.beginBetweenTileContext(start.getX(), start.getY(), end.getX(),
		        end.getY(), progress);
		context.getGl()
		        .glTranslatef(0, -20 * progress * (progress - 1) + 10, 0);
		if (progress >= 1) {
			context.getGl().glTranslatef(0, 0, -.2f);
		}

		this.imageProvider.getSettlerSequence(FILE, sequence)
		        .getImageSafe(index).draw(context.getGl(), null, color);
		context.endTileContext();
	}

	private void drawStones(MapDrawContext context, IMapObject object,
	        float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, STONE);
		int stones = (int) (seq.length() - object.getStateProgress() - 1);
		seq.getImageSafe(stones).draw(context.getGl(), null, color);
	}

	private void drawWaves(MapDrawContext context, int x, int y, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, WAVES);
		int len = seq.length();
		int step = (animationStep/2 + x/2 + y/2) % len;
		if (step < len) {
			seq.getImageSafe(step).draw(context.getGl(), null, color);
		}
	}

	private void drawDeadCorn(MapDrawContext context, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		seq.getImageSafe(CORN_DEAD_STEP).draw(context.getGl(), null, color);
	}

	private void drawGrowingCorn(MapDrawContext context, IMapObject object,
	        float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = (int) (object.getStateProgress() * CORN_GROW_STEPS);
		seq.getImageSafe(step).draw(context.getGl(), null, color);
	}

	private void drawCorn(MapDrawContext context, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = CORN_GROW_STEPS;
		seq.getImageSafe(step).draw(context.getGl(), null, color);
	}

	private void drawGrowingTree(MapDrawContext context, int x, int y,
	        float progress, float color) {
		Image image;
		if (progress < 0.33) {
			Sequence<? extends Image> seq =
			        this.imageProvider.getSettlerSequence(FILE,
			                SMALL_GROWING_TREE);
			image = seq.getImageSafe(0);
		} else {
			int treeType = getTreeType(x, y);
			Sequence<? extends Image> seq =
			        this.imageProvider.getSettlerSequence(FILE,
			                TREE_CHANGING_SEQUENCES[treeType]);
			if (progress < 0.66) {
				image = seq.getImageSafe(TREE_SMALL);
			} else {
				image = seq.getImageSafe(TREE_MEDIUM);
			}
		}
		image.draw(context.getGl(), null, color);
	}

	private void drawFallingTree(MapDrawContext context, int x, int y,
	        float progress, float color) {
		int treeType = getTreeType(x, y);
		int imageStep = 0;

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
		                TREE_CHANGING_SEQUENCES[treeType]);
		seq.getImageSafe(imageStep).draw(context.getGl(), null, color);
	}

	private void drawTree(MapDrawContext context, int x, int y, float color) {
		int treeType = getTreeType(x, y);
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE,
		                TREE_SEQUENCES[treeType]);

		int step = getAnimationStep(x, y) % seq.length();
		seq.getImageSafe(step).draw(context.getGl(), null, color);
	}

	/**
	 * gets a 0 or a 1.
	 * 
	 * @param pos
	 * @return
	 */
//	private static int get01(int x, int y) {
//		return (x * 677 + y) % 2;
//	}

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

	private static int getTreeType(int x, int y) {
		return (x * 251 + y * 233) % TREE_TYPES;
	}

	private int getAnimationStep(int x, int y) {
		return this.animationStep + x * 167 + y * 41;
	}

	/**
	 * Increases the animation step for trees and other stuff.
	 */
	public void increaseAnimationStep() {
		this.animationStep =
		        ((int) System.currentTimeMillis() / 100) & 0x7fffffff;
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
	        float color) {
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
	        EMaterialType material, int count, float color) {
		int stackIndex = material.getStackIndex();

		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, stackIndex);
		seq.getImageSafe(count - 1).draw(glDrawContext, null, color);
	}

	/**
	 * Gets the gray color for a given fog.
	 * 
	 * @param fogstatus
	 * @return
	 */
	public static float getColor(int fogstatus) {
		float color = (float) fogstatus / CommonConstants.FOG_OF_WAR_VISIBLE;
		return color;
	}

	/**
	 * Draws a given buildng to the context.
	 * 
	 * @param context
	 * @param building
	 * @param color
	 *            Gray color shade
	 */
	private void drawBuilding(MapDrawContext context, int x, int y,
	        IBuilding building, float color) {
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
				image.draw(context.getGl(), null, color);
			}

			for (ImageLink link : type.getImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(context, maskState, image, color);
			}
		} else {
			if (type == EBuildingType.MILL
			        && ((IBuilding.IMill) building).isWorking()) {
				Sequence<? extends Image> seq =
				        this.imageProvider.getSettlerSequence(MILL_FILE,
				                MILL_SEQ);

				if (seq.length() > 0) {
					int i = getAnimationStep(x, y);
					int step = i % seq.length();
					seq.getImageSafe(step).draw(context.getGl(), null, color);
				}
				playSound(building, 42);

			} else {
				ImageLink[] images = type.getImages();
				if (images.length > 0) {
					Image image = imageProvider.getImage(images[0]);
					image.draw(context.getGl(), null, color);
				}

				if (building instanceof IBuilding.IOccupyed) {
					drawOccupyers(context, (IBuilding.IOccupyed) building);
				}

				for (int i = 1; i < images.length; i++) {
					Image image = imageProvider.getImage(images[i]);
					image.draw(context.getGl(), null, color);
				}
			}
		}

		if (building.isSelected()) {
			drawBuildingSelectMarker(context);
		}
	}

	private void drawOccupyers(MapDrawContext context, IOccupyed building) {
		for (IBuildingOccupyer occupyer : building.getOccupyers()) {
			OccupyerPlace place = occupyer.getPlace();
			GLDrawContext gl = context.getGl();

			gl.glPushMatrix();
			gl.glTranslatef(place.getOffsetX(), place.getOffsetY(), 0);

			if (place.getType() == ESoldierType.INFANTARY) {
				ImageLink image =
				        place.looksRight() ? INSIDE_BUILDING_RIGHT
				                : INSIDE_BUILDING_LEFT;
				Color color =
				        context.getPlayerColor(occupyer.getMovable()
				                .getPlayer());
				imageProvider.getImage(image).draw(gl, color);
			} else {
				movableDrawer.draw(context, occupyer.getMovable());
			}
			gl.glPopMatrix();
		}
	}

	private void drawBuildingSelectMarker(MapDrawContext context) {
		context.getGl().glTranslatef(0, 20, .2f);
		Image image =
		        imageProvider.getSettlerSequence(SELECTMARK_FILE,
		                SELECTMARK_SEQUENCE).getImageSafe(0);
		image.draw(context.getGl(), null);
	}

	private static void drawWithConstructionMask(MapDrawContext context,
	        float maskState, Image unsafeimage, float color) {
		if (!(unsafeimage instanceof SingleImage)) {
			return; // should not happen
		}
		SingleImage image = (SingleImage) unsafeimage;
		// number of tiles in x direction, can be adjustet for performance
		int tiles = 6;
		
		float toplineBottom = (int) (maskState * image.getHeight()) / (float) image.getHeight();
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
		gl.color(color, color, color, 1);
		gl.drawTrianglesWithTexture(image.getTextureIndex(gl), tris);
	}

	private static void addPointToArray(float[] array, int pointindex, float u,
	        float v, SingleImage image) {
		int left = image.getOffsetX();
		int top = -image.getOffsetY();
		int bottom = top - image.getHeight();

		int x = left + (int) (image.getWidth() * u);
		int y = bottom + (int) (image.getHeight() * v);

		int offset = pointindex * 5;
		array[offset] = x;
		array[offset + 1] = y;
		array[offset + 2] = 0;
		// .5px offset because it works ...
		array[offset + 3] = u * image.getTextureScaleX();// + .5f/image.getWidth();
		array[offset + 4] =
		        image.getTextureScaleY() - v * image.getTextureScaleY() + .5f/image.getHeight();
	}

}
