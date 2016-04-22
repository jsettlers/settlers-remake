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
package jsettlers.mapcreator.mapview;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;

/**
 * Helper class to display resources on the Map
 * 
 * @author Andreas Butti
 *
 */
public class ResourceMapObject implements IMapObject {

	/**
	 * Amount of the resources
	 */
	private final byte resourceAmount;

	/**
	 * Type of the resource
	 */
	private final EResourceType resourceType;

	/**
	 * Constructor
	 * 
	 * @param resourceType
	 *            Type of the resource
	 * @param resourceAmount
	 *            Amount of the resources
	 */
	public ResourceMapObject(EResourceType resourceType, byte resourceAmount) {
		this.resourceType = resourceType;
		this.resourceAmount = resourceAmount;

	}

	/**
	 * Gets the resource object
	 * 
	 * @param resourceType
	 *            Type of the resource
	 * @param resourceAmount
	 *            Amount of the resources
	 * @return IMapObject
	 */
	public static IMapObject get(EResourceType resourceType, byte resourceAmount) {
		return new ResourceMapObject(resourceType, resourceAmount);
	}

	@Override
	public EMapObjectType getObjectType() {
		switch (resourceType) {
		case COAL:
			return EMapObjectType.FOUND_COAL;

		case GOLDORE:
			return EMapObjectType.FOUND_GOLD;

		case IRONORE:
			return EMapObjectType.FOUND_IRON;

		case FISH:
			return EMapObjectType.FISH_DECORATION;

		case NOTHING:
			return EMapObjectType.FOUND_NOTHING;

		case GEMSTONE:
			return EMapObjectType.FOUND_GEMSTONE;

		case BRIMSTONE:
			return EMapObjectType.FOUND_BRIMSTONE;

		default:
			return EMapObjectType.FOUND_NOTHING;
		}

	}

	@Override
	public float getStateProgress() {
		return (float) resourceAmount / Byte.MAX_VALUE;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}
}
