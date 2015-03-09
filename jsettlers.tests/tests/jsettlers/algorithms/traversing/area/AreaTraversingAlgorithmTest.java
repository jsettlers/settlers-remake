package jsettlers.algorithms.traversing.area;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.interfaces.IContainingProvider;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
import jsettlers.logic.algorithms.traversing.area.AreaTraversingAlgorithm;

import org.junit.Test;

public class AreaTraversingAlgorithmTest {

	private static final int WIDTH = 200;
	private static final int HEIGHT = 200;

	@Test
	public void testTraversing1() {
		final MapCircle c1 = new MapCircle(100, 100, 50);
		final MapCircle c2 = new MapCircle(120, 100, 20);
		final MapCircle c3 = new MapCircle(120, 100, 10);

		final IContainingProvider containingProvider = new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return c1.contains(x, y) && !c2.contains(x, y) || c3.contains(x, y);
			}
		};

		final LinkedList<ShortPoint2D> area = new LinkedList<ShortPoint2D>();
		final BitSet visited = new BitSet(WIDTH * HEIGHT);
		ITraversingVisitor visitor = new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				assertTrue(c1.contains(x, y) && !c2.contains(x, y)); // checks if the position is in the area
				area.add(new ShortPoint2D(x, y));
				int idx = x + y * WIDTH;
				assertFalse(visited.get(idx)); // every position is only visited once
				visited.set(idx);
				return true;
			}
		};

		boolean result = AreaTraversingAlgorithm.traverseArea(containingProvider, visitor, c1.iterator().next(), WIDTH, HEIGHT);
		assertTrue(result);

		// check if all positions in the area have been traversed
		FreeMapArea mapArea = new FreeMapArea(area);
		for (ShortPoint2D curr : c1) {
			if (!c2.contains(curr)) {
				assertTrue(mapArea.contains(curr));
			}
		}
	}
}
