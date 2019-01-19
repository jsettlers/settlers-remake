/*
 * Copyright (c) 2018
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
 */

package jsettlers.graphics.map.minimap;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * Calculates the shape of the screen displayed on the minimap.
 * the origin of coordinates is in the bottom left and values increase to the top right.
 */
class MiniMapShapeCalculator {

    private float width;
    private float height;
    private float stride;
    private MapRectangle mapViewport;
    private final MapCoordinateConverter converter;


    MiniMapShapeCalculator(float stride, MapCoordinateConverter converter) {
        this.stride = stride;

        this.converter = converter;
    }

    float[] getMiniMapShapeNodes() {
        if (mapViewport == null) {
            return new float[0];
        }
        int offset = 50;
        int minX = mapViewport.getMinX();
        int minY = mapViewport.getMinY();
        int maxX = minX + mapViewport.getWidth();
        int maxY = minY + mapViewport.getHeight() - offset;
        float minViewX = converter.getViewX(minX, minY, 0) * width;
        float maxViewY = converter.getViewY(minX, minY, 0) * height;
        float maxViewX = converter.getViewX(maxX, maxY + offset, 0) * width;
        float minViewY = converter.getViewY(maxX, maxY, 0) * height;

        return new float[] {
                // bottom left
                minViewX,
                minViewY,
                // bottom right
                Math.min(maxViewX, (minViewY / height * stride + 1) * width),
                minViewY,
                // mid right
                maxViewX,
                Math.max(minViewY, (maxViewX - width) / width / stride * height),
                // top right
                maxViewX,
                maxViewY,
                // top left
                Math.max(minViewX, (maxViewY / height * stride ) * width),
                maxViewY,
                // mid left
                minViewX,
                Math.min(maxViewY, minViewX  / stride / width * height + 10),
        };
    }

    void setMapViewport(MapRectangle mapViewport) {
        this.mapViewport = mapViewport;
    }

    void setWidth(float width) {
        this.width = width;
    }

    void setHeight(float height) {
        this.height = height;
    }
}
