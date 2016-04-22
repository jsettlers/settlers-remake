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
package jsettlers.network.client.interfaces;

import jsettlers.network.synchronic.timer.INetworkTimerable;

/**
 * Interface acting as an access point to the network functionality needed by a starting and then active match.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkConnector {
	/**
	 * 
	 * @return Returns the {@link ITaskScheduler} of used for this match.
	 */
	ITaskScheduler getTaskScheduler();

	/**
	 * 
	 * @return Returns the {@link IGameClock} that can be used to attach {@link INetworkTimerable}s for synchronous execution.
	 */
	IGameClock getGameClock();

	/**
	 * Shuts down the network connector.
	 */
	void shutdown();

	void setStartFinished(boolean startFinished);

	boolean haveAllPlayersStartFinished();
}
