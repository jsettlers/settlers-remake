package jsettlers.common.map.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapRectangleTest {
    @Test
    public void containsMax() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertTrue(mapRectangle.contains(10, 10));
    }

    @Test
    public void containsMin() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertTrue(mapRectangle.contains(0, 0));
    }

    @Test
    public void containsNotMaxX() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertFalse(mapRectangle.contains(11, 10));
    }

    @Test
    public void containsNotMinX() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertFalse(mapRectangle.contains(-1, 0));
    }

    @Test
    public void containsNotMaxY() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertFalse(mapRectangle.contains(10, 11));
    }

    @Test
    public void containsNotMinY() {
        MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

        assertFalse(mapRectangle.contains(0, -1));
    }

}
