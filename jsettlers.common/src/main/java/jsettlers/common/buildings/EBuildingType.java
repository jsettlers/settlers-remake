/*******************************************************************************
 * Copyright (c) 2015, 2016
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
 *******************************************************************************/
package jsettlers.common.buildings;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.buildings.loader.BuildingFile;
import jsettlers.common.buildings.stacks.ConstructionStack;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.images.ImageLink;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;

/**
 * This interface defines the main building type.
 * 
 * @author Michael Zangl
 * @author Andreas Eberle
 */
public enum EBuildingType {
	STONECUTTER,
	FORESTER,
	LUMBERJACK,
	SAWMILL,

	COALMINE,
	IRONMINE,
	GOLDMINE,
	GOLDMELT,
	IRONMELT,
	TOOLSMITH,
	WEAPONSMITH,

	FARM,
	PIG_FARM,
	/**
	 * Needs to implement {@link IBuilding.IMill}
	 */
	MILL,
	WATERWORKS,
	SLAUGHTERHOUSE,
	BAKER,
	FISHER,
	WINEGROWER,
	CHARCOAL_BURNER,
	DONKEY_FARM,

	SMALL_LIVINGHOUSE,
	MEDIUM_LIVINGHOUSE,
	BIG_LIVINGHOUSE,

	LOOKOUT_TOWER,
	TOWER,
	BIG_TOWER,
	CASTLE,
	HOSPITAL,
	BARRACK,

	DOCKYARD,
	HARBOR,
	STOCK,

	TEMPLE,
	BIG_TEMPLE,

	MARKET_PLACE;

	/**
	 * A copy of {@link #values()}. Do not modify this array. This is intended for quicker access to this value.
	 */
	public static final EBuildingType[] VALUES = EBuildingType.values();

	/**
	 * The number of buildings in the {@link #VALUES} array.
	 */
	public static final int NUMBER_OF_BUILDINGS = VALUES.length;
	private static final EnumSet<EBuildingType> MILITARY_BUILDINGS = EnumSet.of(TOWER, BIG_TOWER, CASTLE);
	private static final EnumSet<EBuildingType> MINE_BUILDINGS = EnumSet.of(GOLDMINE, IRONMINE, COALMINE);

	/**
	 * The ordinal of this type. Yields more performance than using {@link #ordinal()}
	 */
	public final int ordinal;

	private final IBuildingJob startJob;

	private final EMovableType workerType;

	private final RelativePoint doorTile;

	private final RelativePoint[] blockedTiles;

	private final short workRadius;

	private final boolean mine;

	private final ConstructionStack[] constructionStacks;
	private final RelativeStack[] requestStacks;
	private final RelativeStack[] offerStacks;

	private final RelativePoint workCenter;

	private final RelativePoint flag;

	private final RelativeBricklayer[] bricklayers;

	private final byte numberOfConstructionMaterials;

	private final ImageLink guiImage;

	private final ImageLink[] images;

	private final ImageLink[] buildImages;

	private final RelativePoint[] protectedTiles;

	private final RelativePoint[] buildMarks;

	private final EnumSet<ELandscapeType> groundTypes;

	private final short viewDistance;

	private final OccupierPlace[] occupierPlaces;

	private final BuildingAreaBitSet buildingAreaBitSet;

	/**
	 * Constructs an enum object.
	 */
	EBuildingType() {
		this.ordinal = ordinal();

		BuildingFile file = new BuildingFile(this.toString());
		startJob = file.getStartJob();
		workerType = file.getWorkerType();
		doorTile = file.getDoor();
		blockedTiles = file.getBlockedTiles();
		protectedTiles = file.getProtectedTiles();

		constructionStacks = file.getConstructionRequiredStacks();
		requestStacks = file.getRequestStacks();
		offerStacks = file.getOfferStacks();

		workRadius = file.getWorkradius();
		workCenter = file.getWorkcenter();
		mine = file.isMine();
		flag = file.getFlag();
		bricklayers = file.getBricklayers();
		occupierPlaces = file.getOccupyerPlaces();
		guiImage = file.getGuiImage();

		images = file.getImages();
		buildImages = file.getBuildImages();

		buildMarks = file.getBuildmarks();
		groundTypes = EnumSet.copyOf(file.getGroundtypes());
		viewDistance = file.getViewdistance();

		this.numberOfConstructionMaterials = calculateNumberOfConstructionMaterials();

		this.buildingAreaBitSet = new BuildingAreaBitSet(getBuildingArea());

		if (mine) {
			this.buildingAreaBitSet.setCenter((short) 1, (short) 1);
		}
	}

	private byte calculateNumberOfConstructionMaterials() {
		byte sum = 0;
		for (ConstructionStack stack : getConstructionStacks()) {
			sum += stack.requiredForBuild();
		}
		return sum;
	}

	public RelativePoint[] getBuildingArea() {
		return protectedTiles;
	}

	/**
	 * Gets the job a worker for this building should start with.
	 * 
	 * @return That {@link IBuildingJob}
	 */
	public final IBuildingJob getStartJob() {
		return startJob;
	}

	/**
	 * Gets the type of worker required for the building.
	 * 
	 * @return The worker or <code>null</code> if no worker is required.
	 */
	public final EMovableType getWorkerType() {
		return workerType;
	}

	/**
	 * Gets the position of the door for this building.
	 * 
	 * @return The door.
	 */
	public final RelativePoint getDoorTile() {
		return doorTile;
	}

	/**
	 * Gets a list of blocked positions.
	 * 
	 * @return The list of blocked positions.
	 */
	public final RelativePoint[] getBlockedTiles() {
		return blockedTiles;
	}

	/**
	 * Gets the tiles that are protected by this building. On thse tiles, no other buildings may be build.
	 * 
	 * @return The tiles as array.
	 */
	public final RelativePoint[] getProtectedTiles() {
		return protectedTiles;
	}

	/**
	 * Gets the images needed to display this building. They are rendered in the order provided.
	 * 
	 * @return The images
	 */
	public final ImageLink[] getImages() {
		return images;
	}

	/**
	 * Gets the images needed to display this building while it si build. They are rendered in the order provided.
	 * 
	 * @return The images
	 */
	public final ImageLink[] getBuildImages() {
		return buildImages;
	}

	/**
	 * Gets the gui image that is displayed in the building selection dialog.
	 * 
	 * @return The image. It may be <code>null</code>
	 */
	public final ImageLink getGuiImage() {
		return guiImage;
	}

	/**
	 * Gets the working radius of the building. If it is 0, the building does not support a working radius.
	 * 
	 * @return The radius.
	 */
	public final short getWorkRadius() {
		return workRadius;
	}

	/**
	 * Gets the default work center for the building type.
	 * 
	 * @return The default work center position.
	 */
	public final RelativePoint getDefaultWorkcenter() {
		return workCenter;
	}

	/**
	 * Gets the position of the flag for this building. The flag type is determined by the building itself.
	 * 
	 * @return The flag position.
	 */
	public final RelativePoint getFlag() {
		return flag;
	}

	/**
	 * Gets the positions where the bricklayers should stand to build the house.
	 * 
	 * @return The positions.
	 * @see RelativeBricklayer
	 */
	public final RelativeBricklayer[] getBricklayers() {
		return bricklayers;
	}

	/**
	 * Gets the positions of the build marks (sticks) for this building.
	 * 
	 * @return The positions of the marks.
	 */
	public final RelativePoint[] getBuildMarks() {
		return buildMarks;
	}

	/**
	 * Gets the ground types this building can be placed on.
	 * 
	 * @return The ground types.
	 */
	public final Set<ELandscapeType> getGroundTypes() {
		return groundTypes;
	}

	/**
	 * Gets the distance the FOW should be set to visible around this building.
	 * 
	 * @return The view distance.
	 */
	public final short getViewDistance() {
		return viewDistance;
	}

	/**
	 * Gets the places where occupiers can be in this building.
	 * 
	 * @return The places.
	 * @see OccupierPlace
	 */
	public final OccupierPlace[] getOccupierPlaces() {
		return occupierPlaces;
	}

	/**
	 * Queries a building job with the given name that needs to be accessible from the start job.
	 * 
	 * @param jobname
	 *            The name of the job.
	 * @return The job if found.
	 * @throws IllegalArgumentException
	 *             If the name was not found.
	 */
	public final IBuildingJob getJobByName(String jobname) {
		HashSet<String> visited = new HashSet<String>();

		ConcurrentLinkedQueue<IBuildingJob> queue = new ConcurrentLinkedQueue<IBuildingJob>();
		queue.add(startJob);

		while (!queue.isEmpty()) {
			IBuildingJob job = queue.poll();
			if (visited.contains(job.getName())) {
				continue;
			}
			if (job.getName().equals(jobname)) {
				return job;
			}
			visited.add(job.getName());

			queue.add(job.getNextFailJob());
			queue.add(job.getNextSucessJob());
		}
		throw new IllegalArgumentException("This building has no job with name " + jobname);
	}

	/**
	 * Gets the area for this building.
	 * 
	 * @return The building area.
	 */
	public final BuildingAreaBitSet getBuildingAreaBitSet() {
		return buildingAreaBitSet;
	}

	/**
	 * Gets the materials required to build this building and where to place them.
	 * 
	 * @return The array of material stacks.
	 */
	public ConstructionStack[] getConstructionStacks() {
		return constructionStacks;
	}

	/**
	 * Get the amount of material required to build this house. Usually the number of stone + planks.
	 * 
	 * @return The number of materials required to construct the building.
	 */
	public final byte getNumberOfConstructionMaterials() {
		return numberOfConstructionMaterials;
	}

	/**
	 * Gets the request stacks required to operate this building.
	 * 
	 * @return The request stacks.
	 */
	public RelativeStack[] getRequestStacks() {
		return requestStacks;
	}

	/**
	 * Gets the positions where the building should offer materials.
	 * 
	 * @return The offer positions.
	 */
	public RelativeStack[] getOfferStacks() {
		return offerStacks;
	}

	/**
	 * Checks if this building is a mine.
	 * 
	 * @return <code>true</code> iff this building is a mine.
	 */
	public boolean isMine() {
		return mine;
	}

	public boolean needsFlattenedGround() {
		return !mine;
	}

	/**
	 * Checks if this building is a military building.
	 * 
	 * @return <code>true</code> iff this is a military building.
	 */
	public boolean isMilitaryBuilding() {
		return MILITARY_BUILDINGS.contains(this);
	}

	/**
	 * Gets an collection of all military buildings.
	 * 
	 * @return The buildings.
	 */
	public static EnumSet<EBuildingType> getMilitaryBuildings() {
		return MILITARY_BUILDINGS;
	}

	public Set<ELandscapeType> getRequiredGroundTypeAt(int relativeX, int relativeY) {
		if (relativeX == 0 && relativeY == 0 && mine) { // if it is a mine and we are in the center
			return ELandscapeType.MOUNTAIN_TYPES;
		} else {
			return groundTypes;
		}
	}
}
