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
package jsettlers.ai.construction;

import java.util.List;

import jsettlers.common.position.ShortPoint2D;

/**
 * The ScoredConstructionPosition helps to create lists of possible construction positions with their score for the low level KI
 * IBestConstructionPositionFinder
 *
 * @author codingberlin
 */
class ScoredConstructionPosition {
	ShortPoint2D point;
	int score;

	public ScoredConstructionPosition(ShortPoint2D point, int score) {
		this.point = point;
		this.score = score;
	}

	public static ShortPoint2D detectPositionWithLowestScore(List<ScoredConstructionPosition> scoredConstructionPositions) {
		if (scoredConstructionPositions.size() == 0) {
			return null;
		}

		ScoredConstructionPosition winnerPosition = null;
		for (ScoredConstructionPosition scoredConstructionPosition : scoredConstructionPositions) {
			if (winnerPosition == null) {
				winnerPosition = scoredConstructionPosition;
			} else if (scoredConstructionPosition.score < winnerPosition.score) {
				winnerPosition = scoredConstructionPosition;
			}
		}

		return winnerPosition.point;
	}

}