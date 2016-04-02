/*******************************************************************************
 * Copyright (c) 2015, 2016
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.menu.joingame;

import java.util.Random;

import jsettlers.common.player.ECivilisation;
import jsettlers.graphics.localization.Labels;

/**
 * @author codingberlin
 */
public class CivilisationUiWrapper {

	private final ECivilisation civilisation;
	private final boolean isRandom;

	public CivilisationUiWrapper() {
		this(determineRandomCivilisation(), true);
	}

	public CivilisationUiWrapper(ECivilisation civilisation) {
		this(civilisation, false);
	}

	private CivilisationUiWrapper(ECivilisation civilisation, boolean isRandom) {
		this.civilisation = civilisation;
		this.isRandom = isRandom;
	}

	private static ECivilisation determineRandomCivilisation() {
		return ECivilisation.values()[new Random().nextInt(ECivilisation.values().length)];
	}

	public ECivilisation getCivilisation() {
		return civilisation;
	}

	@Override
	public String toString() {
		if (isRandom) {
			return Labels.getString("civilisation-random");
		}
		return Labels.getString("civilisation-" + civilisation.name());
	}
}
