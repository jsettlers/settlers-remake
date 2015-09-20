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
package jsettlers.logic.map.grid.partition;

import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.map.grid.flags.IBlockingChangedListener;

/**
 * This is an extended {@link IBlockingProvider}. Implementors of this interface also need to supply the possibility to register a listener for
 * changes of the blocking state.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionsGridBlockingProvider extends IBlockingProvider {
	/**
	 * This is a default implementation of the {@link IPartitionsGridBlockingProvider} interface. It's {@link #isBlocked(int, int)} method always
	 * returns false and the {@link #registerBlockingChangedListener(IBlockingChangedListener)} ignores every listener.
	 */
	public static final IPartitionsGridBlockingProvider DEFAULT_IMPLEMENTATION = new IPartitionsGridBlockingProvider() {
		@Override
		public boolean isBlocked(int x, int y) {
			return false;
		}

		@Override
		public void registerBlockingChangedListener(IBlockingChangedListener listener) {
		}
	};

	/**
	 * Registers the given listener. (Only one listener can be registered).
	 * 
	 * @param listener
	 */
	void registerBlockingChangedListener(IBlockingChangedListener listener);
}
