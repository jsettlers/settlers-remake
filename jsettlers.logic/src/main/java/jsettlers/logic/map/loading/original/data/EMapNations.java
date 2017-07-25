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

/**
 * The civilization the player is of
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EMapNations {
	ROMANS(0),
	EGYPTIANS(1),
	ASIANS(2),
	AMAZONS(3),
	FREE_CHOICE(255),
	NOT_DEFINED(256);

	private static final EMapNations[] VALUES = EMapNations.values();
	public final int value;

	EMapNations(int value) {
		this.value = value;
	}

	public static EMapNations fromMapValue(int mapValue) {
		for (int i = 0; i < EMapNations.VALUES.length; i++) {
			if (EMapNations.VALUES[i].value == mapValue)
				return EMapNations.VALUES[i];
		}

		System.err.println("wrong value for 'EMapNations' " + Integer.toString(mapValue) + "!");

		return EMapNations.ROMANS;
	}
}