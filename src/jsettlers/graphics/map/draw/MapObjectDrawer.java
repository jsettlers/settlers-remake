package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;

import java.util.ConcurrentModificationException;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuilding.IOccupyed;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.images.AnimationSequence;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.sound.ISoundable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class handles drawing of objects on the map.
 *
 * @author michael
 */
public class MapObjectDrawer {

	private static final OriginalImageLink INSIDE_BUILDING_RIGHT =
	        new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 1);
	private static final OriginalImageLink INSIDE_BUILDING_LEFT =
	        new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 0);

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
	private static final int FISH_SEQ = 7;
	private static final AnimationSequence TREE_TEST_SEQUENCE =
	        new AnimationSequence("tree_test", 0, 5);;

	int animationStep = 0;

	private final ImageProvider imageProvider = ImageProvider.getInstance();
	private final SoundManager sound;

	private final DrawBuffer buffer;

	private final MapDrawContext context;

	private final SettlerImageMap imageMap = SettlerImageMap.getInstance();
	private float betweenTilesY;

	public MapObjectDrawer(MapDrawContext context, SoundManager sound,
	        DrawBuffer buffer) {
		this.context = context;
		this.sound = sound;
		this.buffer = buffer;
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
	public void drawMapObject(IGraphicsGrid map, int x, int y, IMapObject object) {
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
			switch (type) {

				case TREE_ADULT:
					if (context.ENABLE_ORIGINAL) {
						drawTree(x, y, color);
					} else {
						drawTreeTest(x, y, color);
					}
					break;

				case TREE_DEAD:
					// TODO: falling tree sound.
					playSound(object, 4);
					drawFallingTree(x, y, progress, color);
					break;

				case TREE_GROWING:
					drawGrowingTree(x, y, progress, color);
					break;

				case CORN_GROWING:
					drawGrowingCorn(x, y, object, color);
					break;

				case CORN_ADULT:
					drawCorn(x, y, color);
					break;

				case CORN_DEAD:
					drawDeadCorn(x, y, color);
					break;

				case WAVES:
					drawWaves(x, y, color);
					break;

				case STONE:
					drawStones(x, y, object, color);
					break;

				case GHOST:
					drawPlayerableByProgress(x, y, 12, 27, object, color);
					playSound(object, 35);
					break;

				case BUILDING_DECONSTRUCTION_SMOKE:
					drawByProgress(x, y, 13, 38, object.getStateProgress(),
					        color);
					// <TODO this is not the right sound.
					playSound(object, 35);
					break;

				case FOUND_COAL:
					drawByProgress(x, y, FILE, 94, object.getStateProgress(),
					        color);
					break;

				case FOUND_GEMSTONE:
					drawByProgress(x, y, FILE, 95, object.getStateProgress(),
					        color);
					break;

				case FOUND_GOLD:
					drawByProgress(x, y, FILE, 96, object.getStateProgress(),
					        color);
					break;

				case FOUND_IRON:
					drawByProgress(x, y, FILE, 97, object.getStateProgress(),
					        color);
					break;

				case FOUND_BRIMSTONE:
					drawByProgress(x, y, FILE, 98, object.getStateProgress(),
					        color);
					break;

				case FOUND_NOTHING:
					drawByProgress(x, y, FILE, 99, object.getStateProgress(),
					        color);
					break;

				case BUILDINGSITE_SIGN:
					drawByProgress(x, y, FILE, 93, object.getStateProgress(),
					        color);
					break;

				case BUILDINGSITE_POST:
					drawByProgress(x, y, FILE, 92, object.getStateProgress(),
					        color);
					break;

				case WORKAREA_MARK:
					drawByProgress(x, y, FILE, 91, object.getStateProgress(),
					        color);
					break;

				case FLAG_DOOR:
					drawPlayerableWaving(x, y, 13, 63, object, color);
					break;

				case CONSTRUCTION_MARK:
					drawByProgress(x, y, 4, 6, object.getStateProgress(), color);
					break;

				case FLAG_ROOF:
					float z = buffer.getZ();
					buffer.setZ(.89f);
					drawPlayerableWaving(x, y, 13, 64, object, color);
					buffer.setZ(z);
					break;

				case BUILDING:
					drawBuilding(x, y, (IBuilding) object, color);
					break;

				case STACK_OBJECT:
					drawStack(x, y, (IStackMapObject) object, color);
					break;

				case SMOKE:
					drawByProgress(x, y, 13, 42, progress, color);
					break;

				case WINE:
					drawByProgress(x, y, 1, 25, progress, color);
					break;

				case PLANT_DECORATION: {
					int step = (x * 13 + y * 233) % 8;

					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(1, 27);

					draw(seq.getImageSafe(step), x, y, color);
				}
					break;

				case DESERT_DECORATION: {
					int step = (x * 13 + y * 233) % 5 + 10;

					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(1, 27);

					draw(seq.getImageSafe(step), x, y, color);
				}
					break;

				case PIG: {
					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(ANIMALS_FILE,
					                PIG_SEQ);

					if (seq.length() > 0) {
						int i = getAnimationStep(x, y) / 2;
						int step = i % seq.length();
						draw(seq.getImageSafe(step), x, y, color);
					}
				}
					break;

				case FISH_DECORATION: {
					int step = getAnimationStep(x, y);
					Sequence<? extends Image> seq =
					        this.imageProvider.getSettlerSequence(ANIMALS_FILE,
					                FISH_SEQ);
					int substep = step % 1024;
					if (substep < 15) {
						int subseq = (step / 1024) % 4;
						draw(seq.getImageSafe(subseq * 15 + substep), x, y,
						        color);
					}
				}
					break;

				case ATTACKABLE_TOWER: {
					IMovable movable = ((IAttackableTowerMapObject) object).getMovable();
					if (movable != null) {
						Image image = this.imageMap.getImageForSettler(movable);
						draw(image, x, y, color);
					}
				}
					break;

				default:
					break;
			}
		}
		if (object.getNextObject() != null) {
			drawMapObject(map, x, y, object.getNextObject());
		}
	}

	/**
	 * Draws a movable
	 *
	 * @param movable
	 *            The movable.
	 */
	public void draw(IMovable movable) {
		Image image = this.imageMap.getImageForSettler(movable);
		drawImage(movable, image);

		if (!movable.isSoundPlayed()) {
			EAction action = movable.getAction();
			if (action == EAction.ACTION1) {
				playSoundAction1(movable.getMovableType());
				movable.setSoundPlayed();
			} else if (action == EAction.ACTION2) {
				playSoundAction2(movable.getMovableType());
				movable.setSoundPlayed();
			}
		}
	}

	private void playSoundAction1(EMovableType type) {
		switch (type) {
			case LUMBERJACK:
				sound.playSound(1, 1, 1);
				break;
			case STONECUTTER:
				sound.playSound(3, 1, 1);
				break;
			case DIGGER:
				sound.playSound(2, 1, 1);
				break;
			case SAWMILLER:
				sound.playSound(5, 1, 1);
				break;
			case SMITH:
				sound.playSound(6, 1, 1);
				break;
			case FARMER:
				sound.playSound(12, 1, 1);
				break;
			case SWORDSMAN_L1:
			case SWORDSMAN_L2:
			case SWORDSMAN_L3:
				sound.playSound(30, 1, 1);
				break;
			case BOWMAN_L1:
			case BOWMAN_L2:
			case BOWMAN_L3:
				sound.playSound(33, 1, 1);
				break;
		}
	}

	private void playSoundAction2(EMovableType type) {
		// currently there is nobody who needs this.
	}

	private void drawImage(IMovable movable, Image image) {
		ShortPoint2D pos = movable.getPos();
		short x = pos.getX();
		short y = pos.getY();

		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus == 0) {
			return; // break
		}

		Color color = context.getPlayerColor(movable.getPlayer());
		float shade = MapObjectDrawer.getColor(fogstatus);

		float viewX;
		float viewY;
		if (movable.getAction() == EAction.WALKING) {
			int originx = x - movable.getDirection().getGridDeltaX();
			int originy = y - movable.getDirection().getGridDeltaY();
			viewX =
			        betweenTilesX(originx, originy, x, y,
			                movable.getMoveProgress());
			viewY = betweenTilesY;
		} else {
			int height = context.getHeight(x, y);
			viewX = context.getConverter().getViewX(x, y, height);
			viewY = context.getConverter().getViewY(x, y, height);
		}
		image.drawAt(context.getGl(), buffer, viewX, viewY, color, shade);

		if (movable.isSelected()) {
			drawSelectionMark(viewX, viewY, movable.getHealth());
		}
	}

	private float betweenTilesX(int startx, int starty, int destinationx,
	        int destinationy, float progress) {
		float theight = context.getHeight(startx, starty);
		float dheight = context.getHeight(destinationx, destinationy);
		MapCoordinateConverter converter = context.getConverter();
		float x =
		        (1 - progress)
		                * converter.getViewX(startx, starty, theight)
		                + progress
		                * converter.getViewX(destinationx, destinationy,
		                        dheight);
		betweenTilesY =
		        (1 - progress)
		                * converter.getViewY(startx, starty, theight)
		                + progress
		                * converter.getViewY(destinationx, destinationy,
		                        dheight);
		return x;
	}

	private void drawSelectionMark(float viewX, float viewY, float health) {
		float z = buffer.getZ();
		buffer.setZ(.9f);

		Image image =
		        ImageProvider.getInstance().getSettlerSequence(4, 7)
		                .getImageSafe(0);
		image.drawAt(context.getGl(), buffer, viewX, viewY + 20, -1);

		Sequence<? extends Image> sequence =
		        ImageProvider.getInstance().getSettlerSequence(4, 6);
		int healthId =
		        Math.min((int) ((1 - health) * sequence.length()),
		                sequence.length() - 1);
		Image healthImage = sequence.getImageSafe(healthId);
		healthImage.drawAt(context.getGl(), buffer, viewX, viewY + 38, -1);

		buffer.setZ(z);
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

		float x = betweenTilesX(object.getSourceX(),
		        object.getSourceY(), object.getTargetX(), object.getTargetY(),
		        progress);

		int iColor = Color.getABGR(color, color, color, 1);

		boolean onGround = progress >= 1;
		float z = 0;
		if (onGround) {
			z = buffer.getZ();
			buffer.setZ(-.1f);
			iColor &= 0x7fffffff;
		}
		Image image = this.imageProvider.getSettlerSequence(FILE, sequence)
		        .getImageSafe(index);
		image.drawAt(context.getGl(), buffer, x, betweenTilesY + 20 * progress * (1 - progress) + 20, iColor);
		if (onGround) {
			buffer.setZ(z);
		}
	}

	private void drawStones(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, STONE);
		int stones = (int) (seq.length() - object.getStateProgress() - 1);
		draw(seq.getImageSafe(stones), x, y, color);
	}

	private void drawWaves(int x, int y, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, WAVES);
		int len = seq.length();
		int step = (animationStep / 2 + x / 2 + y / 2) % len;
		if (step < len) {
			draw(seq.getImageSafe(step), x, y, color);
		}
	}

	private void drawDeadCorn(int x, int y, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		draw(seq.getImageSafe(CORN_DEAD_STEP), x, y, color);
	}

	private void drawGrowingCorn(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = (int) (object.getStateProgress() * CORN_GROW_STEPS);
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawCorn(int x, int y, float color) {
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, CORN);
		int step = CORN_GROW_STEPS;
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawGrowingTree(int x, int y, float progress, float color) {
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
		draw(image, x, y, color);
	}

	private void drawFallingTree(int x, int y, float progress, float color) {
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
		draw(seq.getImageSafe(imageStep), x, y, color);
	}

	private void drawTree(int x, int y, float color) {
		int treeType = getTreeType(x, y);
		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE,
		                TREE_SEQUENCES[treeType]);

		int step = getAnimationStep(x, y) % seq.length();
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawTreeTest(int x, int y, float color) {
		int step = getAnimationStep(x, y) % TREE_TEST_SEQUENCE.getLength();
		draw(imageProvider.getImage(TREE_TEST_SEQUENCE.getImage(step)), x, y,
		        color);
	}

	/**
	 * gets a 0 or a 1.
	 *
	 * @param pos
	 * @return
	 */
	// private static int get01(int x, int y) {
	// return (x * 677 + y) % 2;
	// }

	/**
	 * Draws a player border at a given position.
	 *
	 * @param player
	 *            The player.
	 */
	public void drawPlayerBorderObject(int x, int y, byte player) {
		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus == 0) {
			return; // break
		}
		float base = getColor(fogstatus);
		Color color = context.getPlayerColor(player);

		draw(imageProvider.getSettlerSequence(FILE_BORDERPOST, 65)
		        .getImageSafe(0), x, y, color, base);
	}

	private static int getTreeType(int x, int y) {
		return (x + x / 5 + y / 3 + y + y / 7) % TREE_TYPES;
	}

	private int getAnimationStep(int x, int y) {
		return 0xfffffff & (this.animationStep + x * 167 + y * 1223);
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
	public void drawStack(int x, int y, IStackMapObject object, float color) {
		byte elements = object.getSize();
		if (elements > 0) {
			drawStackAtScreen(x, y, object.getMaterialType(), elements, color);
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
	private void drawStackAtScreen(int x, int y, EMaterialType material,
	        int count, float color) {
		int stackIndex = material.getStackIndex();

		Sequence<? extends Image> seq =
		        this.imageProvider.getSettlerSequence(FILE, stackIndex);
		draw(seq.getImageSafe(count - 1), x, y, color);
	}

	/**
	 * Gets the gray color for a given fog.
	 *
	 * @param fogstatus
	 * @return
	 */
	public static float getColor(int fogstatus) {
		return (float) fogstatus / CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	/**
	 * Draws a given buildng to the context.
	 *
	 * @param context
	 * @param building
	 * @param color
	 *            Gray color shade
	 */
	private void drawBuilding(int x, int y, IBuilding building, float color) {
		EBuildingType type = building.getBuildingType();

		float state = building.getStateProgress();
		float maskState;
		if (state < 0.5f) {
			maskState = state * 2;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(x, y, maskState, image, color);
			}

		} else if (state < 0.99) {
			maskState = state * 2 - 1;
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				draw(image, x, y, color);
			}

			for (ImageLink link : type.getImages()) {
				Image image = imageProvider.getImage(link);
				drawWithConstructionMask(x, y, maskState, image, color);
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
					draw(seq.getImageSafe(step), x, y, color);
				}
				playSound(building, 42);

			} else {
				ImageLink[] images = type.getImages();
				if (images.length > 0) {
					Image image = imageProvider.getImage(images[0]);
					draw(image, x, y, color);
				}

				if (building instanceof IBuilding.IOccupyed) {
					drawOccupyers(x, y, (IBuilding.IOccupyed) building, color);
				}

				for (int i = 1; i < images.length; i++) {
					Image image = imageProvider.getImage(images[i]);
					draw(image, x, y, color);
				}
			}
		}

		if (building.isSelected()) {
			drawBuildingSelectMarker(x, y);
		}
	}

	private void drawOccupyers(int x, int y, IOccupyed building, float basecolor) {
		// this can cause a ConcurrentModificationException when
		// a soldier enters the tower!
		try {
			int height = context.getHeight(x, y);
			float viewX = context.getConverter().getViewX(x, y, height);
			float viewY = context.getConverter().getViewY(x, y, height);
			GLDrawContext gl = context.getGl();

			for (IBuildingOccupyer occupyer : building.getOccupyers()) {
				OccupyerPlace place = occupyer.getPlace();

				Color color =
				        context.getPlayerColor(occupyer.getMovable()
				                .getPlayer());

				Image image;
				switch (place.getType()) {
					case INFANTRY:
						OriginalImageLink imageLink =
						        place.looksRight() ? INSIDE_BUILDING_RIGHT
						                : INSIDE_BUILDING_LEFT;
						image = imageProvider.getImage(imageLink);
						break;
					case BOWMAN:
					default:
						image =
						        this.imageMap.getImageForSettler(occupyer
						                .getMovable());
				}
				image.drawAt(gl, buffer, viewX + place.getOffsetX(), viewY
				        + place.getOffsetY(), color, basecolor);

			}
		} catch (ConcurrentModificationException e) {
			// happens sometime, just ignore it.
		}
	}

	private void drawBuildingSelectMarker(int x, int y) {
		float z = buffer.getZ();
		buffer.setZ(.9f);

		Image image =
		        imageProvider.getSettlerSequence(SELECTMARK_FILE,
		                SELECTMARK_SEQUENCE).getImageSafe(0);
		draw(image, x, y, -1);

		buffer.setZ(z);
	}

	private void drawWithConstructionMask(int x, int y, float maskState,
	        Image unsafeimage, float color) {
		if (!(unsafeimage instanceof SingleImage)) {
			return; // should not happen
		}
		context.beginTileContext(x, y);

		SingleImage image = (SingleImage) unsafeimage;
		// number of tiles in x direction, can be adjusted for performance
		int tiles = 6;

		float toplineBottom =
		        (int) (maskState * image.getHeight())
		                / (float) image.getHeight();
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

		context.endTileContext();
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
		array[offset + 3] = u * image.getTextureScaleX();// +
		                                                 // .5f/image.getWidth();
		array[offset + 4] =
		        image.getTextureScaleY() - v * image.getTextureScaleY() + .5f
		                / image.getHeight();
	}

	private void drawPlayerableByProgress(int x, int y, int file,
	        int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (object.getStateProgress() * sequence.length()),
		                sequence.length() - 1);
		Color color = getColor(object);
		draw(sequence.getImage(index), x, y, color, basecolor);
	}

	private Color getColor(IMapObject object) {
		Color color = null;
		if (object instanceof IPlayerable) {
			color = context.getPlayerColor(((IPlayerable) object).getPlayer());
		}
		return color;
	}

	private void drawPlayerableWaving(int x, int y, int file,
	        int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index = animationStep % sequence.length();
		Color color = getColor(object);
		draw(sequence.getImageSafe(index), x, y, color, basecolor);
	}

	private void drawByProgress(int x, int y, int file, int sequenceIndex,
	        float progress, float color) {

		Sequence<? extends Image> sequence =
		        this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
		        Math.min((int) (progress * sequence.length()),
		                sequence.length() - 1);
		draw(sequence.getImageSafe(index), x, y, color);
	}

	private void draw(Image image, int x, int y, Color color, float basecolor) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height);
		float viewY = context.getConverter().getViewY(x, y, height);

		image.drawAt(context.getGl(), buffer, viewX, viewY, color, basecolor);
	}

	private void draw(Image image, int x, int y, float color) {
		int iColor = Color.getABGR(color, color, color, 1);
		draw(image, x, y, iColor);
	}

	private void draw(Image image, int x, int y, int color) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height);
		float viewY = context.getConverter().getViewY(x, y, height);

		image.drawAt(context.getGl(), buffer, viewX, viewY, color);
	}
}
