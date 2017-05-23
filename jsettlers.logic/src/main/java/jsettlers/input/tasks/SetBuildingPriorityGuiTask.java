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
public class SetBuildingPriorityGuiTask extends SimpleGuiTask {
	private ShortPoint2D buildingPosition;
	private EPriority newPriority;

	public SetBuildingPriorityGuiTask() {
	}

	public SetBuildingPriorityGuiTask(byte playerId, ShortPoint2D buildingPosition, EPriority newPriority) {
		super(EGuiAction.SET_BUILDING_PRIORITY, playerId);
		this.buildingPosition = buildingPosition;
		this.newPriority = newPriority;
	}

	/**
	 * 
	 * @return Returns the position of the building that shall get the new priority.
	 */
	public ShortPoint2D getBuildingPosition() {
		return buildingPosition;
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
		SimpleGuiTask.serializePosition(dos, buildingPosition);
		dos.writeByte(newPriority.ordinal);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		buildingPosition = SimpleGuiTask.deserializePosition(dis);
		newPriority = EPriority.VALUES[dis.readByte()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((buildingPosition == null) ? 0 : buildingPosition.hashCode());
		result = prime * result + ((newPriority == null) ? 0 : newPriority.hashCode());
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
		SetBuildingPriorityGuiTask other = (SetBuildingPriorityGuiTask) obj;
		if (buildingPosition == null) {
			if (other.buildingPosition != null)
				return false;
		} else if (!buildingPosition.equals(other.buildingPosition))
			return false;
		return newPriority == other.newPriority;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SetBuildingPriorityGuiTask [buildingPosition=");
		builder.append(buildingPosition);
		builder.append(", newPriority=");
		builder.append(newPriority);
		builder.append(", guiAction=");
		builder.append(getGuiAction());
		builder.append(", playerId=");
		builder.append(getPlayerId());
		builder.append("]");
		return builder.toString();
	}
}
