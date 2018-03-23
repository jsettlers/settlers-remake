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
import java.util.List;

import jsettlers.common.menu.action.EMoveToMode;
import jsettlers.common.position.ShortPoint2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MoveToGuiTask extends MovableGuiTask {
	private ShortPoint2D position;
	private EMoveToMode mode;

	public MoveToGuiTask() {
	}

	public MoveToGuiTask(byte playerId, ShortPoint2D pos, List<Integer> selection, EMoveToMode mode) {
		super(EGuiAction.MOVE_TO, playerId, selection);
		this.position = pos;
		this.mode = mode;
	}

	public ShortPoint2D getPosition() {
		return position;
	}
	
	public EMoveToMode getMode() {
		return mode;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, position);
		dos.writeByte(mode.ordinal());
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		position = SimpleGuiTask.deserializePosition(dis);
		mode = EMoveToMode.values()[dis.readByte()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
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
		MoveToGuiTask other = (MoveToGuiTask) obj;
		if (mode != other.mode)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
}
