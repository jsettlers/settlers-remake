/*******************************************************************************
 * Copyright (c) 2015
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

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.buildings.loader.BuildingFile;
import jsettlers.common.buildings.stacks.ConstructionStack;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;

/**
 * This interface defines the main building type.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public enum EBuildingType {
	STONECUTTER(3),
	FORESTER(7),
	LUMBERJACK(0),
	SAWMILL(1),

	COALMINE(6),
	IRONMINE(4),
	GOLDMINE(5),
	GOLDMELT(8),
	IRONMELT(9),
	TOOLSMITH(10),
	WEAPONSMITH(11),

	FARM(21),
	PIG_FARM(20),
	/**
	 * Needs to implement {@link IBuilding.Mill}
	 */
	MILL(14), // 13 => rotating
	WATERWORKS(19),
	SLAUGHTERHOUSE(13),
	BAKER(23),
	FISHER(22),
	WINEGROWER(33),
	CHARCOAL_BURNER(30),
	DONKEY_FARM(35), // GUI 87

	SMALL_LIVINGHOUSE(24),
	MEDIUM_LIVINGHOUSE(25),
	BIG_LIVINGHOUSE(26),

	LOOKOUT_TOWER(2),
	TOWER(17),
	BIG_TOWER(18),
	CASTLE(16),
	HOSPITAL(29), // GUI 93
	BARRACK(34),

	DOCKYARD(45),
	HARBOR(44),
	STOCK(-1),
	// 27: katapult

	TEMPLE(31),
	BIG_TEMPLE(32),

	MARKET_PLACE(0),
	// /**
	// * Test building for own image files.
	// */
	// LAGERHAUS(0)
	;

	public static final EBuildingType[] values = EBuildingType.values();
	public static final int NUMBER_OF_BUILDINGS = values.length;
	private static final EBuildingType[] MILITARY_BUILDINGS = {TOWER, BIG_TOWER, CASTLE};

	public final int ordinal;

	private final IBuildingJob startJob;

	private final EMovableType workerType;

	private final RelativePoint doorTile;

	private final RelativePoint[] blockedTiles;

	private final int imageIndex;

	private final short workradius;

	private final ConstructionStack[] constructionStacks;
	private final RelativeStack[] requestStacks;
	private final RelativeStack[] offerStacks;

	private final RelativePoint workcenter;

	private final RelativePoint flag;

	private final RelativeBricklayer[] bricklayers;

	private final byte numberOfConstructionMaterials;

	private final ImageLink guiImage;

	private final ImageLink[] images;

	private final ImageLink[] buildImages;

	private final RelativePoint[] protectedTiles;

	private final RelativePoint[] buildmarks;

	private final ELandscapeType[] groundtypes;
	private final long groundtypesFast;

	private final short viewdistance;

	private final OccupyerPlace[] occupyerPlaces;

	private final BuildingAreaBitSet buildingAreaBitSet;

	EBuildingType(int imageIndex) {
		this.ordinal = ordinal();

		this.imageIndex = imageIndex;
		BuildingFile file = new BuildingFile(this.toString());
		startJob = file.getStartJob();
		workerType = file.getWorkerType();
		doorTile = file.getDoor();
		blockedTiles = file.getBlockedTiles();
		protectedTiles = file.getProtectedTiles();

		constructionStacks = file.getConstructionRequiredStacks();
		requestStacks = file.getRequestStacks();
		offerStacks = file.getOfferStacks();

		workradius = file.getWorkradius();
		workcenter = file.getWorkcenter();
		flag = file.getFlag();
		bricklayers = file.getBricklayers();
		occupyerPlaces = file.getOccupyerPlaces();
		guiImage = file.getGuiImage();
		ImageLink[] tempimages = file.getImages();
		if (tempimages.length == 0) {
			// TODO: this can be removed if all images are converted
			System.out.println("WARNING: Building " + this.toString() + " does not have an image definition.");
			images = new ImageLink[] { new OriginalImageLink(EImageLinkType.SETTLER, 13, imageIndex, 0) };
			buildImages = new ImageLink[] { new OriginalImageLink(EImageLinkType.SETTLER, 13, imageIndex, 1) };
		} else {
			images = tempimages;
			buildImages = file.getBuildImages();
		}

		buildmarks = file.getBuildmarks();
		groundtypes = file.getGroundtypes();
		groundtypesFast = packGroundtypes(groundtypes);
		viewdistance = file.getViewdistance();

		this.numberOfConstructionMaterials = calculateNumberOfConstructionMaterials();

		this.buildingAreaBitSet = new BuildingAreaBitSet(this.getProtectedTiles());
	}

	private long packGroundtypes(ELandscapeType[] groundtypes) {
		assert ELandscapeType.values.length <= 64;
		long res = 0;
		for (ELandscapeType g : groundtypes) {
			res |= (1 << g.ordinal);
		}
		return res;
	}

	private final byte calculateNumberOfConstructionMaterials() {
		byte sum = 0;
		for (ConstructionStack stack : getConstructionStacks()) {
			sum += stack.requiredForBuild();
		}
		return sum;
	}

	public final IBuildingJob getStartJob() {
		return startJob;
	}

	public final EMovableType getWorkerType() {
		return workerType;
	}

	public final RelativePoint getDoorTile() {
		return doorTile;
	}

	public final RelativePoint[] getBlockedTiles() {
		return blockedTiles;
	}

	@Deprecated
	public final int getImageIndex() {
		return imageIndex;
	}

	/**
	 * Gets the images needed to display this building
	 * 
	 * @return The images
	 */
	public final ImageLink[] getImages() {
		return images;
	}

	/**
	 * Gets the gui image
	 * 
	 * @return The image. It may be <code>null</code>
	 */
	public final ImageLink getGuiImage() {
		return guiImage;
	}

	/**
	 * Gets the working radius of the building. If it is 0, the building does not support a working radius.
	 * 
	 * @return
	 */
	public final short getWorkradius() {
		return workradius;
	}

	public final RelativePoint getWorkcenter() {
		return workcenter;
	}

	public final RelativePoint getFlag() {
		return flag;
	}

	public final RelativeBricklayer[] getBricklayers() {
		return bricklayers;
	}

	public final byte getNumberOfConstructionMaterials() {
		return numberOfConstructionMaterials;
	}

	/**
	 * Gets the tiles that are protected by this building. On thse tiles, no other buildings may be build.
	 * 
	 * @return The tiles as array.
	 */
	public final RelativePoint[] getProtectedTiles() {
		return protectedTiles;
	}

	public final RelativePoint[] getBuildmarks() {
		return buildmarks;
	}

	public final ImageLink[] getBuildImages() {
		return buildImages;
	}

	public final ELandscapeType[] getGroundtypes() {
		return groundtypes;
	}

	public final short getViewDistance() {
		return viewdistance;
	}

	public final OccupyerPlace[] getOccupyerPlaces() {
		return occupyerPlaces;
	}

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

	public final BuildingAreaBitSet getBuildingAreaBitSet() {
		return buildingAreaBitSet;
	}

	public ConstructionStack[] getConstructionStacks() {
		return constructionStacks;
	}

	public RelativeStack[] getRequestStacks() {
		return requestStacks;
	}

	public RelativeStack[] getOfferStacks() {
		return offerStacks;
	}

	public static EBuildingType[] getMilitaryBuildings() {
		return MILITARY_BUILDINGS;
	}

	public boolean isMine() {
		switch (this) {
		case COALMINE:
		case IRONMINE:
		case GOLDMINE:
			return true;
		default:
			return false;
		}
	}

	public boolean isMilitaryBuilding() {
		switch(this) {
		case TOWER:
		case BIG_TOWER:
		case CASTLE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * A (fast) method that allows us to check if this landscape type is allowed.
	 * 
	 * @param landscapeId
	 *            The landscape id (ordinal).
	 * @return True if we allow that landscape as ground type.
	 */
	public boolean allowsLandscapeId(int landscapeId) {
		return (groundtypesFast & (1 << landscapeId)) != 0;
	}
}
