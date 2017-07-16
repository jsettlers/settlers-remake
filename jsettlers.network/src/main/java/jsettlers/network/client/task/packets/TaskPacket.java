/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.network.client.task.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class TaskPacket extends Packet {
	public static final IDeserializingable<TaskPacket> DEFAULT_DESERIALIZER = (key, dis) -> {
		try {
			dis.readInt(); // read the length in bytes from the stream. We don't need it here, only the server needs it.
			String className = dis.readUTF();
			@SuppressWarnings("unchecked")
			Class<? extends TaskPacket> taskClass = (Class<? extends TaskPacket>) Class.forName(className);
			TaskPacket packet = taskClass.newInstance();
			packet.deserializeTask(dis);
			return packet;
		} catch (Exception e) {
			throw new IOException(e);
		}
	};

	@Override
	public final void serialize(DataOutputStream dos) throws IOException {
		ByteArrayOutputStream bufferOutStream = new ByteArrayOutputStream();
		DataOutputStream bufferDataOutStream = new DataOutputStream(bufferOutStream);

		bufferDataOutStream.writeUTF(this.getClass().getName());
		serializeTask(bufferDataOutStream);
		bufferDataOutStream.flush();

		dos.writeInt(bufferOutStream.size());
		bufferOutStream.writeTo(dos);
	}

	protected abstract void serializeTask(DataOutputStream dos) throws IOException;

	@Override
	public final void deserialize(DataInputStream dis) throws IOException {
	}

	protected abstract void deserializeTask(DataInputStream dis) throws IOException;
}
