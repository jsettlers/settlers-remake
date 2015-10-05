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
package jsettlers.common.material;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A (final) set of materials. We could use an enum set here. But they use a slow contains method.
 * 
 * @author Michael Zangl
 *
 */
public final class MaterialSet implements Serializable {
	private static final long serialVersionUID = 3406490542591944273L;
	private final long materials;

	private MaterialSet(long materials) {
		this.materials = materials;
	}

	public MaterialSet(EMaterialType... materials) {
		this(toLong(materials));
	}

	private static long toLong(EMaterialType[] materials) {
		long value = 0;
		for (EMaterialType m : materials) {
			value |= (1l << m.ordinal);
		}
		return value;
	}

	/**
	 * This can be used to iterate over this list.
	 * 
	 * <pre>
	 * for (int i = getNext(0); i >= 0; i = getNext(i)) { ... }
	 * </pre>
	 * 
	 * @param lastIndex
	 *            The last index returned by this method, 0 for the first call.
	 * @return A material id or -1 if there are no more in this set.
	 */
	public int getNext(int lastIndex) {
		long remaining = materials & (-1l << lastIndex);
		return remaining == 0 ? -1 : Long.numberOfTrailingZeros(remaining);
	}

	public boolean contains(EMaterialType material) {
		return (materials & (1l << material.ordinal)) != 0;
	}

	public MaterialSet set(EMaterialType material, boolean set) {
		long mask = (1l << material.ordinal);
		long newMaterials = set ? materials | mask : materials & ~mask;
		if (newMaterials != materials) {
			return new MaterialSet(newMaterials);
		} else {
			return this;
		}
	}

	public int size() {
		return Long.bitCount(materials);
	}

	public EMaterialType[] toArray() {
		EMaterialType[] materials = new EMaterialType[size()];
		for (int i = getNext(0), j = 0; i >= 0; i = getNext(i + 1), j++) {
			materials[j] = EMaterialType.values[i];
		}
		return materials;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (materials ^ (materials >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialSet other = (MaterialSet) obj;
		if (materials != other.materials)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaterialSet [" + (toArray() != null ? Arrays.asList(toArray()) : null) + "]";
	}

}
