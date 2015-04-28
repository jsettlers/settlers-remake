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

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkAreaGuiTask extends SimpleGuiTask {
	private ShortPoint2D workAreaPosition;
	private ShortPoint2D buildingPos;

	public WorkAreaGuiTask() {
	}

	/**
	 * 
	 * @param guiAction
	 * @param playerId
	 * @param workAreaPosition
	 * @param buildingPos
	 */
	public WorkAreaGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D workAreaPosition, ShortPoint2D buildingPos) {
		super(guiAction, playerId);
		this.workAreaPosition = workAreaPosition;
		this.buildingPos = buildingPos;
	}

	public ShortPoint2D getPosition() {
		return workAreaPosition;
	}

	public ShortPoint2D getBuildingPos() {
		return buildingPos;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, workAreaPosition);
		SimpleGuiTask.serializePosition(dos, buildingPos);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		workAreaPosition = SimpleGuiTask.deserializePosition(dis);
		buildingPos = SimpleGuiTask.deserializePosition(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((buildingPos == null) ? 0 : buildingPos.hashCode());
		result = prime * result + ((workAreaPosition == null) ? 0 : workAreaPosition.hashCode());
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
		WorkAreaGuiTask other = (WorkAreaGuiTask) obj;
		if (buildingPos == null) {
			if (other.buildingPos != null)
				return false;
		} else if (!buildingPos.equals(other.buildingPos))
			return false;
		if (workAreaPosition == null) {
			if (other.workAreaPosition != null)
				return false;
		} else if (!workAreaPosition.equals(other.workAreaPosition))
			return false;
		return true;
	}
}
