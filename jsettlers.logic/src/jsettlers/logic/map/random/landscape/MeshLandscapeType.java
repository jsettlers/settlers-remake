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
package jsettlers.logic.map.random.landscape;

/**
 * This are the types of the landscapes on the landscape mesh.
 * 
 * @author michael
 *
 */
public enum MeshLandscapeType {
	UNSPECIFIED,

	GRASS,

	SEA,

	SAND,

	MOUNTAIN,
	DESERT;

	public static MeshLandscapeType parse(String string) {
		return valueOf(string.toUpperCase());
	}

	/**
	 * Does not throw an exception.
	 * 
	 * @param parameter
	 *            The parameter
	 * @return The result or the default value
	 */
	public static MeshLandscapeType parse(String parameter, MeshLandscapeType defaultValue) {
		try {
			return parse(parameter);
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

}
