/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.loading.original.data;

import jsettlers.common.landscape.EResourceType;

/**
 * The map resources and their mapping to {@link EResourceType}
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EMapResources {

	FISH(EResourceType.FISH),
	COAL(EResourceType.COAL),
	IRONORE(EResourceType.IRONORE),
	GOLDORE(EResourceType.GOLDORE),
	GEMS(EResourceType.GEMSTONE),
	SULFUR(EResourceType.BRIMSTONE),
	NOT_A_RESOURCE_TYPE(EResourceType.NOTHING);

	public final EResourceType value;

	// - length of THIS enum (without NOT_A_TYPE)
	private static final int LENGTH = EMapResources.values().length - 1;

	EMapResources(EResourceType value) {
		this.value = value;
	}

	public static EMapResources getTypeByInt(int type) {
		if (type < 0 || type >= EMapResources.LENGTH) {
			return NOT_A_RESOURCE_TYPE;
		} else {
			return EMapResources.values()[type];
		}
	}
}