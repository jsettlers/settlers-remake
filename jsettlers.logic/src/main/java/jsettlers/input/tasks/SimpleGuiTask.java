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
package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.network.client.task.packets.TaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SimpleGuiTask extends TaskPacket {
	private EGuiAction guiAction;
	private byte playerId;

	public SimpleGuiTask() {
	}

	public SimpleGuiTask(EGuiAction guiAction, byte playerId) {
		this.guiAction = guiAction;
		this.playerId = playerId;
	}

	public EGuiAction getGuiAction() {
		return guiAction;
	}

	public byte getPlayerId() {
		return playerId;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		dos.writeInt(guiAction.ordinal());
		dos.writeByte(playerId);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		guiAction = EGuiAction.VALUES[dis.readInt()];
		playerId = dis.readByte();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guiAction == null) ? 0 : guiAction.hashCode());
		result = prime * result + playerId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleGuiTask other = (SimpleGuiTask) obj;
		if (guiAction != other.guiAction)
			return false;
		return playerId == other.playerId;
	}

	public static void serializePosition(DataOutputStream dos, ShortPoint2D position) throws IOException {
		dos.writeShort(position.x);
		dos.writeShort(position.y);
	}

	public static ShortPoint2D deserializePosition(DataInputStream dis) throws IOException {
		return new ShortPoint2D(dis.readShort(), dis.readShort());
	}

	@Override
	public String toString() {
		return "SimpleGuiTask: " + guiAction + " playerId: " + playerId;
	}
}
