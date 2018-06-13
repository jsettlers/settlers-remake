/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.logic.map.grid.partition.manager.materials.requests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jsettlers.testutils.TestUtils;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;

import org.junit.Test;

/**
 * This is a test for the {@link MaterialsForBuildingsRequestPriorityQueue} data structure.
 * 
 * @author Andreas Eberle
 * 
 */
public class SimpleMaterialRequestPriorityQueueTest {
	static {
		MatchConstants.init(null, 1000);
	}

	@Test
	public void testInsertAndRemoveOne() {
		SimpleMaterialRequestPriorityQueue queue = new SimpleMaterialRequestPriorityQueue();
		TestMaterialRequest request = new TestMaterialRequest(3, 0);
		queue.insertRequest(request);

		assertSame(request, popHighest(queue));
		assertSame(request, popHighest(queue));
		assertSame(request, popHighest(queue));
		assertNull(popHighest(queue));
	}

	@Test
	public void testIsInQueue() {
		SimpleMaterialRequestPriorityQueue queue = new SimpleMaterialRequestPriorityQueue();
		TestMaterialRequest request = new TestMaterialRequest(2, 0);
		assertFalse(request.isInQueue());
		queue.insertRequest(request);
		assertTrue(request.isInQueue());

		assertSame(request, popHighest(queue));
		assertTrue(request.isInQueue());
		assertSame(request, popHighest(queue));
		assertTrue(request.isInQueue());

		assertNull(popHighest(queue)); // still needed is not 0 so it should stay in the queue
		assertTrue(request.isInQueue());

		request.stillRequired = 0;
		request.inDelivery = 0;
		assertNull(popHighest(queue)); // now the request should be removed
		assertFalse(request.isInQueue());
	}

	@Test
	public void testInsertAndRemoveTwoWithPriorityChange() {
		SimpleMaterialRequestPriorityQueue queue = new SimpleMaterialRequestPriorityQueue();
		TestMaterialRequest request1 = new TestMaterialRequest(3, 0);
		TestMaterialRequest request2 = new TestMaterialRequest(2, 0);

		queue.insertRequest(request1);
		queue.insertRequest(request2);

		MaterialRequestObject firstReq = popHighest(queue);
		TestMaterialRequest secondReq;
		if (firstReq == request1) {
			secondReq = request2;
		} else {
			secondReq = request1;
		}

		assertSame(firstReq, popHighest(queue));
		secondReq.updatePriority(EPriority.HIGH);

		assertSame(secondReq, popHighest(queue));
		assertSame(secondReq, popHighest(queue));

		assertSame(firstReq, popHighest(queue));
		assertNull(popHighest(queue));
	}

	@Test
	public void testInsertAndRemoveTwoWithOneStopped() {
		SimpleMaterialRequestPriorityQueue queue = new SimpleMaterialRequestPriorityQueue();
		TestMaterialRequest request1 = new TestMaterialRequest(4, 0);
		TestMaterialRequest request2 = new TestMaterialRequest(2, 0);

		queue.insertRequest(request1);
		queue.insertRequest(request2);

		MaterialRequestObject firstReq = popHighest(queue);
		TestMaterialRequest secondReq;
		if (firstReq == request1) {
			secondReq = request2;
		} else {
			secondReq = request1;
		}

		assertSame(firstReq, popHighest(queue));
		firstReq.updatePriority(EPriority.STOPPED);

		assertSame(secondReq, popHighest(queue));
		assertSame(secondReq, popHighest(queue));

		assertNull(popHighest(queue));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		SimpleMaterialRequestPriorityQueue queue = new SimpleMaterialRequestPriorityQueue();
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

		SimpleMaterialRequestPriorityQueue queue2 = new SimpleMaterialRequestPriorityQueue();
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

		SimpleMaterialRequestPriorityQueue deSerializedQueue = TestUtils.serializeAndDeserialize(queue);
		queue.equals(queue2);
		assertEquals(queue, queue2);
		assertEquals(queue, deSerializedQueue);
	}

	private static MaterialRequestObject popHighest(SimpleMaterialRequestPriorityQueue queue) {
		MaterialRequestObject result = queue.getHighestRequest();
		if (result != null) {
			result.deliveryAccepted(); // this needs to be done to emulate the user of the queue.
		}
		return result;
	}

	private static class TestMaterialRequest extends MaterialRequestObject {
		private static final long serialVersionUID = 3244165203515699980L;

		private final ShortPoint2D position;

		private short stillRequired;
		private final int onStack;

		public TestMaterialRequest(ShortPoint2D position, short stillNeeded, int onStack) {
			this.position = position;
			this.stillRequired = stillNeeded;
			this.onStack = onStack;
		}

		public TestMaterialRequest(int stillNeeded, int onStack) {
			this(new ShortPoint2D(0, 0), (short) stillNeeded, onStack);
		}

		@Override
		public short getStillNeeded() {
			return (short) (stillRequired - inDelivery);
		}

		@Override
		public int getInDeliveryable() {
			return Constants.STACK_SIZE - onStack;
		}

		@Override
		public ShortPoint2D getPosition() {
			return position;
		}

		@Override
		protected void materialDelivered() {
			stillRequired--;
		}

		@Override
		protected boolean isRoundRobinRequest() {
			return false;
		}

		@Override
		public EBuildingType getBuildingType() {
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + onStack;
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + stillRequired;
			return result;
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
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			return stillRequired == other.stillRequired;
		}
	}
}
