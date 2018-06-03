package jsettlers.graphics.map.minimap;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * calculates the shape of the screen displayed on the minimap.
 * the origin of coordinates is in the bottom left and values increase to the top right.
 */
class MinimapShapeCalculator {

    private float width;
    private float height;
    private float stride;
    private MapRectangle mapViewport;
    private final MapCoordinateConverter converter;


    MinimapShapeCalculator(float stride, MapCoordinateConverter converter) {
        this.stride = stride;

        this.converter = converter;
    }


    float[] getMinimapShapeNodes() {
        if (mapViewport == null) {
            return new float[0];
        }
        int offset = 50;
        int minX = mapViewport.getMinX();
        int minY = mapViewport.getMinY();
        int maxX = minX + mapViewport.getWidth();
        int maxY = minY + mapViewport.getHeight() - offset;
        float minviewx = converter.getViewX(minX, minY, 0) * width;
        float maxviewy = converter.getViewY(minX, minY, 0) * height;
        float maxviewx = converter.getViewX(maxX, maxY + offset, 0) * width;
        float minviewy = converter.getViewY(maxX, maxY, 0) * height;

        float[] points =  new float[] {
                // bottom left
                minviewx,
                minviewy,
                0,
                // bottom right
                Math.min(maxviewx, (minviewy / height * stride + 1) * width),
                minviewy,
                0,
                // mid right
                maxviewx,
                Math.min(
                        (Math.max(maxviewx,
                                (minviewy / height * stride + 1) * width) - width)
                                / width / stride * height,
                        maxviewy),
                0,
                // top right
                maxviewx,
                maxviewy,
                0,
                // top left
                Math.max(minviewx, minviewy / height * stride * width),
                maxviewy,
                0,
                // mid left
                Math.min(minviewx, Math.max(minviewx, maxviewy / height * stride * width)),
                Math.max(
                        Math.min(minviewy,
                                (maxviewy / height * stride + 1) * width) - height
                                / width / stride * height,
                        minviewy),
                0,
        };
        return points;
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
