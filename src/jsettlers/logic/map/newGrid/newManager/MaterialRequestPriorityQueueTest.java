package jsettlers.logic.map.newGrid.newManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jsettlers.TestUtils;
import jsettlers.logic.constants.Constants;

import org.junit.Test;

public class MaterialRequestPriorityQueueTest {

	private final MaterialRequestPriorityQueue queue = new MaterialRequestPriorityQueue();

	@Test
	public void testInsertAndRemoveOne() {
		TestMaterialRequest request = new TestMaterialRequest(3, 0);
		queue.insertRequest(request);

		assertSame(request, popHighest());
		assertSame(request, popHighest());
		assertSame(request, popHighest());
		assertNull(popHighest());
	}

	@Test
	public void testIsInQueue() {
		TestMaterialRequest request = new TestMaterialRequest(2, 0);
		assertFalse(request.isInQueue());
		queue.insertRequest(request);
		assertTrue(request.isInQueue());

		assertSame(request, popHighest());
		assertTrue(request.isInQueue());
		assertSame(request, popHighest());
		assertTrue(request.isInQueue());

		assertNull(popHighest()); // still needed is not 0 so it should stay in the queue
		assertTrue(request.isInQueue());

		request.stillNeeded = 0;
		assertNull(popHighest()); // now the request should be removed
		assertFalse(request.isInQueue());
	}

	@Test
	public void testInsertAndRemoveTwoWithPriorityChange() {
		TestMaterialRequest request1 = new TestMaterialRequest(3, 0);
		TestMaterialRequest request2 = new TestMaterialRequest(2, 0);

		queue.insertRequest(request1);
		queue.insertRequest(request2);

		MaterialRequestPriorityQueueItem firstReq = popHighest();
		TestMaterialRequest secondReq;
		if (firstReq == request1) {
			secondReq = request2;
		} else {
			secondReq = request1;
		}

		assertSame(firstReq, popHighest());
		secondReq.updatePriority(EPriority.HIGH);

		assertSame(secondReq, popHighest());
		assertSame(secondReq, popHighest());

		assertSame(firstReq, popHighest());
		assertNull(popHighest());
	}

	@Test
	public void testInsertAndRemoveTwoWithOneStopped() {
		TestMaterialRequest request1 = new TestMaterialRequest(4, 0);
		TestMaterialRequest request2 = new TestMaterialRequest(2, 0);

		queue.insertRequest(request1);
		queue.insertRequest(request2);

		MaterialRequestPriorityQueueItem firstReq = popHighest();
		TestMaterialRequest secondReq;
		if (firstReq == request1) {
			secondReq = request2;
		} else {
			secondReq = request1;
		}

		assertSame(firstReq, popHighest());
		firstReq.updatePriority(EPriority.STOPPED);

		assertSame(secondReq, popHighest());
		assertSame(secondReq, popHighest());

		assertNull(popHighest());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		{
			TestMaterialRequest request1 = new TestMaterialRequest(4, 0);
			TestMaterialRequest request2 = new TestMaterialRequest(2, 0);
			TestMaterialRequest request3 = new TestMaterialRequest(2, 1);
			TestMaterialRequest request4 = new TestMaterialRequest(6, 3);

			queue.insertRequest(request1);
			queue.insertRequest(request2);
			queue.insertRequest(request3);
			queue.insertRequest(request4);

			request1.updatePriority(EPriority.HIGH);
			request2.updatePriority(EPriority.STOPPED);
		}

		//

		MaterialRequestPriorityQueue queue2 = new MaterialRequestPriorityQueue();
		{
			TestMaterialRequest request1 = new TestMaterialRequest(4, 0);
			TestMaterialRequest request2 = new TestMaterialRequest(2, 0);
			TestMaterialRequest request3 = new TestMaterialRequest(2, 1);
			TestMaterialRequest request4 = new TestMaterialRequest(6, 3);

			queue2.insertRequest(request1);
			queue2.insertRequest(request2);
			queue2.insertRequest(request3);
			queue2.insertRequest(request4);

			request1.updatePriority(EPriority.HIGH);
			request2.updatePriority(EPriority.STOPPED);
		}

		//

		MaterialRequestPriorityQueue deSerializedQueue = TestUtils.serializeAndDeserialize(queue);
		assertEquals(queue, queue2);
		assertEquals(queue, deSerializedQueue);
	}

	private MaterialRequestPriorityQueueItem popHighest() {
		MaterialRequestPriorityQueueItem result = queue.popHighest();
		if (result != null) {
			result.inDelivery++; // this needs to be done to emulate the user of the queue.
		}
		return result;
	}

	private static class TestMaterialRequest extends MaterialRequestPriorityQueueItem {
		private static final long serialVersionUID = 3244165203515699980L;

		private int stillNeeded;
		private int onStack;

		public TestMaterialRequest(int stillNeeded, int onStack) {
			this.stillNeeded = stillNeeded;
			this.onStack = onStack;
		}

		@Override
		public int getStillNeeded() {
			return stillNeeded;
		}

		@Override
		public int getInDeliveryable() {
			return Constants.STACK_SIZE - onStack;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestMaterialRequest other = (TestMaterialRequest) obj;
			if (onStack != other.onStack)
				return false;
			if (stillNeeded != other.stillNeeded)
				return false;
			return true;
		}
	}
}
