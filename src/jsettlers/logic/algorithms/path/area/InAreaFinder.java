package jsettlers.logic.algorithms.path.area;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import random.RandomSingleton;

/**
 * 
 * 
 * NOTE: This class uses random.Random! Therefore it needs to run synchronous to avoid inconsistencies between multiple players!
 * 
 * @author Andreas Eberle
 * 
 */
public final class InAreaFinder {
	private final IInAreaFinderMap map;
	private final short width;
	private final short height;

	public InAreaFinder(IInAreaFinderMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;
	}

	/**
	 * 
	 * @param requester
	 * @param centerX
	 * @param centerY
	 * @param searchRadius
	 * @param searched
	 * @return an SPoint2D object if the searched thing has been found<br>
	 *         null if it hasn't been found.
	 */
	public final ISPosition2D find(IPathCalculateable requester, short centerX, short centerY, short searchRadius, ESearchType searched) {

		for (int i = 0; i < 100; i++) {
			double angle = RandomSingleton.nextD() * 2 * Math.PI; // get an angle in the interval [0, 2PI]
			double radius = Math.pow(RandomSingleton.nextD(), 3.9) * searchRadius; // get a radius in the interval [0, pixelRadius]

			short tileX = (short) (Math.cos(angle) * radius + centerX);
			short tileY = (short) (Math.sin(angle) * radius + centerY);

			if (isInBounds(tileX, tileY) && !map.isBlocked(requester, tileX, tileY) && map.fitsSearchType(tileX, tileY, searched, requester)) {
				return new ShortPoint2D(tileX, tileY);
			}
		}
		return null;
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}
}
