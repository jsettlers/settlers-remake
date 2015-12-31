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
package jsettlers.logic.map.grid.partition.manager;

import java.io.Serializable;

import jsettlers.algorithms.queue.ITypeAcceptor;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;

public final class MovableTypeAcceptor implements ITypeAcceptor<IManageableWorker>, Serializable {
	private static final long serialVersionUID = 111392803354934224L;

	public EMovableType movableType = null;

	public MovableTypeAcceptor() {
	}

	public MovableTypeAcceptor(EMovableType movableType) {
		this.movableType = movableType;
	}

	@Override
	public final boolean accepts(IManageableWorker worker) {
		return this.movableType == worker.getMovableType();
	}
}