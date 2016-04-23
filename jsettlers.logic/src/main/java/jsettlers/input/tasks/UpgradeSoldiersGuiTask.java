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

import jsettlers.common.movable.ESoldierType;

/**
 * @author codingberlin
 * @author Andreas Eberle
 */
public class UpgradeSoldiersGuiTask extends SimpleGuiTask {

	private ESoldierType soldierType;

	public UpgradeSoldiersGuiTask() {
	}

	public UpgradeSoldiersGuiTask(byte playerId, ESoldierType soldierType) {
		super(EGuiAction.UPGRADE_SOLDIERS, playerId);
		this.soldierType = soldierType;
	}

	public ESoldierType getSoldierType() {
		return soldierType;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeInt(soldierType.ordinal);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		soldierType = ESoldierType.VALUES[dis.readInt()];
	}
}
