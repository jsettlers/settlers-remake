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
package jsettlers.logic.objects;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.objects.AbstractObjectsManagerObject;
import jsettlers.network.synchronic.random.RandomSingleton;

public final class RessourceSignMapObject extends AbstractObjectsManagerObject {
	private static final long serialVersionUID = -7248748388147081545L;

	private static final float MINIMUM_LIVETIME = 4 * 60;
	private static final float MAX_RANDOM_LIVETIME = 5 * 60;

	private final float amount;
	private final byte objectType;

	public RessourceSignMapObject(ShortPoint2D pos, EResourceType resourceType, float amount) {
		super(pos);
		this.amount = amount;

		switch (resourceType) {
		case COAL:
			objectType = EMapObjectType.FOUND_COAL.ordinal;
			break;
		case IRON:
			objectType = EMapObjectType.FOUND_IRON.ordinal;
			break;
		case GOLD:
			objectType = EMapObjectType.FOUND_GOLD.ordinal;
			break;
		default:
			throw new IllegalArgumentException("Can't create ressource sign for: " + resourceType);
		}
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.values[objectType];
	}

	@Override
	public float getStateProgress() {
		return amount;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		throw new UnsupportedOperationException();
	}

	public static final float getLivetime() {
		return RandomSingleton.nextF() * MAX_RANDOM_LIVETIME + MINIMUM_LIVETIME;
	}

}
