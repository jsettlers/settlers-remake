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
package jsettlers.network.infrastructure.channel.listeners;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.IChannelListener;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This abstract class deserializes the packet received from a {@link Channel} with the {@link IDeserializingable}s supplied for each key and defines
 * a specialized method to receive the deserialized Packet in subtypes of this class.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class PacketChannelListener<T extends Packet> implements IChannelListener {

	private final ENetworkKey[] keys;
	private final Hashtable<ENetworkKey, IDeserializingable<T>> deserializers = new Hashtable<>();

	public PacketChannelListener(ENetworkKey key, IDeserializingable<T> deserializer) {
		this.keys = new ENetworkKey[] { key };
		this.deserializers.put(key, deserializer);
	}

	public PacketChannelListener(ENetworkKey[] keys, IDeserializingable<T>[] deserializers) {
		assert keys.length == deserializers.length;

		this.keys = keys;

		for (int i = 0; i < keys.length; i++) {
			this.deserializers.put(keys[i], deserializers[i]);
		}
	}

	@Override
	public ENetworkKey[] getKeys() {
		return keys;
	}

	@Override
	public final void receive(ENetworkKey key, int length, DataInputStream stream) throws IOException, ClassNotFoundException {
		IDeserializingable<T> deserializer = deserializers.get(key);
		assert deserializer != null;

		T deserializedPacket = deserializer.deserialize(key, stream);
		receivePacket(key, deserializedPacket);
	}

	protected abstract void receivePacket(ENetworkKey key, T packet) throws IOException;

}
