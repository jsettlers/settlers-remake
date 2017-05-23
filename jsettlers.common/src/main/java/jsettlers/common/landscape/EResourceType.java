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
package jsettlers.common.landscape;

import jsettlers.common.mapobject.EMapObjectType;

/**
 * These are the types of available resources.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EResourceType {
	COAL(EMapObjectType.FOUND_COAL),
	GOLDORE(EMapObjectType.FOUND_GOLD),
	IRONORE(EMapObjectType.FOUND_IRON),
	FISH(null),
	GEMSTONE(EMapObjectType.FOUND_GEMSTONE),
	BRIMSTONE(EMapObjectType.FOUND_BRIMSTONE),
	NOTHING(EMapObjectType.FOUND_NOTHING);

	public static final EResourceType[] VALUES = EResourceType.values();

	public final byte ordinal;
	public final EMapObjectType mapObjectType;

	EResourceType(EMapObjectType mapObjectType) {
		this.mapObjectType = mapObjectType;
		this.ordinal = (byte) super.ordinal();
	}
}
