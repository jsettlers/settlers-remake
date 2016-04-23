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
package jsettlers.network.infrastructure.channel.ping;

import jsettlers.network.infrastructure.channel.Channel;

/**
 * This class holds information about the round trip time on the {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class RoundTripTime {
	private final long lastUpdated;
	private final int rtt;
	private final int jitter;
	private final int averagedJitter;

	public RoundTripTime(long lastUpdated, int rtt, int jitter, int averagedJitter) {
		this.lastUpdated = lastUpdated;
		this.rtt = rtt;
		this.jitter = jitter;
		this.averagedJitter = averagedJitter;
	}

	/**
	 * 
	 * @return Returns the time when this round trip time has been updated as linux timestamp.
	 */
	public long getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * 
	 * @return Returns the round trip time in milliseconds.
	 */
	public int getRtt() {
		return rtt;
	}

	/**
	 * 
	 * @return Returns the jittering that is currently noticed on the {@link Channel}.
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * 
	 * @return Returns an averaged jittering value.
	 */
	public int getAveragedJitter() {
		return averagedJitter;
	}
}
