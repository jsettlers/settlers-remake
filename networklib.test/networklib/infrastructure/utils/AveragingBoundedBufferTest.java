package networklib.infrastructure.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the {@link AveragingBoundedBuffer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class AveragingBoundedBufferTest {
	private static final int BUFFER_LENGTH = 11;

	private AveragingBoundedBuffer buffer = new AveragingBoundedBuffer(BUFFER_LENGTH);

	@Test
	public void testAverageNoOverlap() {
		for (int i = 1; i <= BUFFER_LENGTH; i++) {
			buffer.insert(i);
		}

		int expectedAvg = ((int) (BUFFER_LENGTH * 0.5f * (BUFFER_LENGTH + 1))) / BUFFER_LENGTH;
		assertEquals(expectedAvg, buffer.getAvg());
	}

	@Test
	public void testAverageNoOverlapDouble() {
		testAverageNoOverlap();
		testAverageNoOverlap();
	}

	@Test
	public void testAverageOverlapUneven() {
		for (int i = 1; i <= 3 * BUFFER_LENGTH; i++) {
			buffer.insert(i);

			int base = (int) ((i - BUFFER_LENGTH) > 0 ? ((i - BUFFER_LENGTH) * 0.5f * (i - BUFFER_LENGTH + 1)) : 0);
			int expectedSum = (int) (i * 0.5f * (i + 1) - base);
			int expectedAvg = expectedSum / BUFFER_LENGTH;
			assertEquals("index: " + i, expectedAvg, buffer.getAvg());
		}
	}
}
