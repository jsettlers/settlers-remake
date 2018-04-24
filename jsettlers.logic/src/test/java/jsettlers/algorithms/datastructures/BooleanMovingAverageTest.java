package jsettlers.algorithms.datastructures;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanMovingAverageTest {

    @Test
    public void testGetAverageDefaultFalse() {
        BooleanMovingAverage booleanMovingAverage = new BooleanMovingAverage(3, false);

        float average = booleanMovingAverage.getAverage();

        assertEquals("All values should be false", 0.0, average, 0.0);
    }

    @Test
    public void testGetAverageDefaultTrue() {
        BooleanMovingAverage booleanMovingAverage = new BooleanMovingAverage(3, true);

        float average = booleanMovingAverage.getAverage();

        assertEquals("All values should be true", 1.0, average, 0.0);
    }

    @Test
    public void testInsertValue() {
        BooleanMovingAverage booleanMovingAverage = new BooleanMovingAverage(3, false);

        booleanMovingAverage.insertValue(true);
        float average = booleanMovingAverage.getAverage();

        assertEquals("One out of three values should be true", 1f/3, average, 0.0);
    }

    @Test
    public void testInsertValueDoesNotOverflow() {
        BooleanMovingAverage booleanMovingAverage = new BooleanMovingAverage(3, false);

        booleanMovingAverage.insertValue(true);
        booleanMovingAverage.insertValue(true);
        booleanMovingAverage.insertValue(true);

        booleanMovingAverage.insertValue(false);

        float average = booleanMovingAverage.getAverage();

        assertEquals("One out of three values should be false", 2f/3, average, 0.0);
    }
}
