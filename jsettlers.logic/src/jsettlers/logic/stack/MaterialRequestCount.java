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
package jsettlers.logic.stack;

import java.util.ArrayList;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.MaterialSet;

/**
 * This is a table of material -> requested count mappings.
 * 
 * @author Michael Zangl
 */
public class MaterialRequestCount {

	public interface IMaterialRequestCountObserver {
		public void materialCountChanged(EMaterialType material, short oldValue, short newValue);
	}

	/**
	 * How many materials were requested by the user. Short#MAX_VALUE for infinity.
	 */
	private final short[] requestedMaterials = new short[EMaterialType.NUMBER_OF_MATERIALS];
	/**
	 * Materials that are in delivery or of which the delivery was already done.
	 */
	private final short[] deliveredMaterials = new short[EMaterialType.NUMBER_OF_MATERIALS];

	private final ArrayList<IMaterialRequestCountObserver> observers = new ArrayList<>();

	public MaterialRequestCount() {
	}

	public short getRequestedFor(EMaterialType material) {
		return requestedMaterials[material.ordinal];
	}

	/**
	 * Gets the number of missing items for this material.
	 * 
	 * @param material
	 *            The material
	 * @return The number of missing items.
	 */
	public short getMissingFor(EMaterialType material) {
		short requested = requestedMaterials[material.ordinal];
		return requested == Short.MAX_VALUE ? requested : (short) Math.max(0, deliveredMaterials[material.ordinal] - requested);
	}

	public void changeRequestedMaterial(EMaterialType material, int amount, boolean relative) {
		long newValue = amount;
		byte materialId = material.ordinal;
		if (relative) {
			int old = requestedMaterials[materialId];
			if (old == Short.MAX_VALUE) {
				// infinity stays infinity.
				return;
			}
			newValue += old;
		}

		short realNewValue = (short) Math.max(0, Math.min(Short.MAX_VALUE, newValue));
		setForMaterial(materialId, realNewValue);
	}

	private void setForMaterial(int materialId, short realNewValue) {
		short oldValue = requestedMaterials[materialId];
		if (oldValue != realNewValue) {
			requestedMaterials[materialId] = realNewValue;
			for (IMaterialRequestCountObserver l : observers) {
				l.materialCountChanged(EMaterialType.values[materialId], oldValue, realNewValue);
			}
		}
	}

	/**
	 * Loads this request count from a material set, setting all material counts to infinity for those contained in the set, 0 for the rest.
	 * 
	 * @param set
	 */
	public void loadFrom(MaterialSet set) {
		for (int i = 0; i < requestedMaterials.length; i++) {
			setForMaterial(i, set.contains(i) ? Short.MAX_VALUE : 0);
		}
	}

	public void changeDelivered(EMaterialType material, int delta) {
		deliveredMaterials[material.ordinal] += delta;
	}

	public boolean isTooMuch(EMaterialType material) {
		return requestedMaterials[material.ordinal] < deliveredMaterials[material.ordinal];
	}

	public void addMaterialCountObserver(IMaterialRequestCountObserver o) {
		observers.add(o);
	}

	public void removeMaterialCountObserver(IMaterialRequestCountObserver o) {
		observers.remove(o);
	}

}
