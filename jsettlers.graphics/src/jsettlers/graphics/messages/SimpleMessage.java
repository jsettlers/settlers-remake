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
package jsettlers.graphics.messages;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.menu.messages.EMessageType;
import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;

/**
 * This is a message that states that the user was attacked by an other player.
 * 
 * @author michael
 */
public class SimpleMessage implements IMessage {
	private final byte sender;
	private final ShortPoint2D pos;
	private final String message;
	private final EMessageType type;
	private int age;

	public SimpleMessage(EMessageType type, String message, byte sender,
			ShortPoint2D pos) {
		this.type = type;
		this.message = message;
		this.sender = sender;
		this.pos = pos;
		this.age = 0;
	}

	@Override
	public EMessageType getType() {
		return type;
	}

	@Override
	public int getAge() {
		return this.age;
	}

	@Override
	public int ageBy(int interval) {
		this.age += interval;
		return this.age;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public byte getSender() {
		return sender;
	}

	@Override
	public ShortPoint2D getPosition() {
		return pos;
	}

	@Override
	public boolean duplicates(IMessage m) {
		if ((m.getSender() == this.sender)
				&& m.getMessage().equals(this.message)
				&& m.getType() == this.type) {
			if (m.getAge() < MESSAGE_TTL / 6) {
				if ((this.type == EMessageType.ATTACKED)
						|| (this.type == EMessageType.MINERALS)) {
					if (this.pos.getOnGridDistTo(m.getPosition())
							< MESSAGE_DIST_THRESHOLD) {
						return true;
					}
				} else if (this.pos.equals(m.getPosition()))
					return true;
			}
		}
		return false;
	}

	public static IMessage attacked(byte otherplayer, ShortPoint2D pos) {
		String message = Labels.getString("attacked");
		return new SimpleMessage(EMessageType.ATTACKED, message, otherplayer,
				pos);
	}

	public static IMessage foundMinerals(EMaterialType type, ShortPoint2D pos) {
		String message = Labels.getString("minerals_" + type.toString());
		return new SimpleMessage(EMessageType.MINERALS, message, (byte) -1, pos);
	}

	public static IMessage cannotFindWork(IBuilding building) {
		String message = Labels.getString("cannot_find_work_" + building.getBuildingType());
		return new SimpleMessage(EMessageType.NOTHING_FOUND_IN_SEARCH_AREA, message, (byte) -1, building.getPos());
	}

}
