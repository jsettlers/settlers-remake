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

import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;

/**
 * This task is used to set the priority of a building.
 * 
 * @author Andreas Eberle
 * 
 */
public class SetBuildingPriorityGuiTask extends SimpleBuildingGuiTask {
	private EPriority newPriority;

	public SetBuildingPriorityGuiTask() {
	}

	public SetBuildingPriorityGuiTask(byte playerId, ShortPoint2D buildingPosition, EPriority newPriority) {
		super(EGuiAction.SET_BUILDING_PRIORITY, playerId, buildingPosition);
		this.newPriority = newPriority;
	}

	/**
	 * 
	 * @return Returns the new priority that shall be set to the building.
	 */
	public EPriority getNewPriority() {
		return newPriority;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeByte(newPriority.ordinal);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		newPriority = EPriority.VALUES[dis.readByte()];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		SetBuildingPriorityGuiTask that = (SetBuildingPriorityGuiTask) o;

		return newPriority == that.newPriority;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (newPriority != null ? newPriority.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SetBuildingPriorityGuiTask{" +
				"newPriority=" + newPriority +
				"} " + super.toString();
	}
}
