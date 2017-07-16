/*******************************************************************************
 * Copyright (c) 2016 - 2017
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.buildings;

import java.util.ArrayList;

import jsettlers.common.position.RelativePoint;

/**
 * Created by Andreas Eberle on 25.04.2016.
 */
public final class BuildingAreaUtils {
    private BuildingAreaUtils() { // Utility class.
    }

    public static RelativePoint[] createRelativePoints(boolean[][] blockedMap) {
        ArrayList<RelativePoint> positions = new ArrayList<>();

        int xOffset = blockedMap[0].length / 2;
        int yOffset = blockedMap.length / 2;

        for (int y = 0; y < blockedMap.length; y++) {
            for (int x = 0; x < blockedMap[y].length; x++) {
                if (blockedMap[y][x]) {
                    positions.add(new RelativePoint(x - xOffset, y - yOffset));
                }
            }
        }

        return positions.toArray(new RelativePoint[positions.size()]);
    }
}
