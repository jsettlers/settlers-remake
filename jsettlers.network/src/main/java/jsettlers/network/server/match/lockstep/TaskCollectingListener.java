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
package jsettlers.network.server.match.lockstep;

import java.util.LinkedList;
import java.util.List;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.server.packets.ServersideTaskPacket;

/**
 * This listener collects {@link Packet}s for the {@link NetworkConstants}.Keys.SYNCHRONOUS_TASK key and adds them to a list. The elements can then be
 * removed from the list to be send to the clients as batch.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskCollectingListener extends PacketChannelListener<ServersideTaskPacket> {
	private List<ServersideTaskPacket> currTasksList = new LinkedList<>();

	public TaskCollectingListener() {
		super(ENetworkKey.SYNCHRONOUS_TASK, new GenericDeserializer<>(ServersideTaskPacket.class));
	}

	/**
	 * 
	 * @return
	 */
	public List<ServersideTaskPacket> getAndResetTasks() {
		List<ServersideTaskPacket> temp = currTasksList;
		currTasksList = new LinkedList<>();
		return temp;
	}

	@Override
	protected void receivePacket(ENetworkKey key, ServersideTaskPacket deserialized) {
		currTasksList.add(deserialized);
	}
}
