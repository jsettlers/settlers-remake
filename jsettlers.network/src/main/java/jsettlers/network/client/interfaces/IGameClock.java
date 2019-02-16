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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.synchronic.timer.INetworkTimerable;
import jsettlers.network.synchronic.timer.ITaskExecutor;

/**
 * This interface defines a clock supported by the network library to the user of the library.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGameClock extends IPausingSupplier {

	int getTime();

	void setTime(int newTime);

	void setTaskExecutor(ITaskExecutor taskExecutor);

	void multiplyGameSpeed(float factor);

	void setGameSpeed(float speedFactor);

	float getGameSpeed();

	void invertPausing();

	void setPausing(boolean b);

	void fastForward();

	void remove(INetworkTimerable timerable);

	void schedule(INetworkTimerable timerable, short delay);

	void startExecution();

	void stopExecution();

	/**
	 * Sets the stream to be used to log the actions of the users.
	 * 
	 * @param replayFileStream
	 */
	void setReplayLogStream(DataOutputStream replayFileStream);

	/**
	 * Saves the remaining tasks to the given stream.
	 * 
	 * @param dos
	 */
	void saveRemainingTasks(DataOutputStream dos) throws IOException;

	void loadReplayLogFromStream(DataInputStream dataInputStream);

	/**
	 * Plays the game with maximum speed to the given game time and then pauses the game.
	 * 
	 * @param targetGameTime
	 *            The desired game time in milliseconds.
	 */
	void fastForwardTo(int targetGameTime);

}
