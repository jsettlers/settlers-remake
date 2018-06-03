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
package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * A task that is for a building (given by it's position).
 * 
 * @author Michael Zangl
 * @author Andreas Eberle
 */
public class SimpleBuildingGuiTask extends SimpleGuiTask {

	private ShortPoint2D buildingPos;

	public SimpleBuildingGuiTask() {
	}

	public SimpleBuildingGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D buildingPos) {
		super(guiAction, playerId);
		this.buildingPos = buildingPos;
	}

	public SimpleBuildingGuiTask(EGuiAction guiAction, byte playerId, ILocatable building) {
		this(guiAction, playerId, building.getPosition());
	}

	/**
	 * Gets the position of the building.
	 *
	 * @return The position.
	 */
	public ShortPoint2D getBuildingPos() {
		return buildingPos;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		serializePosition(dos, buildingPos);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		buildingPos = deserializePosition(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((buildingPos == null) ? 0 : buildingPos.hashCode());
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
		SimpleBuildingGuiTask other = (SimpleBuildingGuiTask) obj;
		if (buildingPos == null) {
			if (other.buildingPos != null)
				return false;
		} else if (!buildingPos.equals(other.buildingPos))
			return false;
		return true;
	}
}