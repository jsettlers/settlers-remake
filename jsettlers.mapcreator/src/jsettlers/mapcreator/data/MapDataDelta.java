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
package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.objects.ObjectContainer;

/**
 * This is a map data delta, that can be applyed from a map data to an other.
 * 
 * @author michael
 */
public class MapDataDelta {
	private HeightChange heightChanges = null;
	private LandscapeChange landscapeChanges = null;
	private ObjectAdder addObjects = null;
	private ObjectRemover removeObjects = null;
	private ResourceChanger changeResources = null;

	public MapDataDelta() {
	}

	public synchronized void addHeightChange(int x, int y, byte height) {
		HeightChange c = new HeightChange();
		c.x = (short) x;
		c.y = (short) y;
		c.height = height;
		c.next = heightChanges;
		heightChanges = c;
	}

	public HeightChange getHeightChanges() {
		return heightChanges;
	}

	public synchronized void addLandscapeChange(int x, int y, ELandscapeType l) {
		LandscapeChange c = new LandscapeChange();
		c.x = (short) x;
		c.y = (short) y;
		c.landscape = l;
		c.next = landscapeChanges;
		landscapeChanges = c;
	}

	public static class HeightChange {
		HeightChange next = null;
		short x;
		short y;
		byte height;
	}

	public LandscapeChange getLandscapeChanges() {
		return landscapeChanges;
	}

	public static class LandscapeChange {
		LandscapeChange next = null;
		short x;
		short y;
		ELandscapeType landscape;
	}

	public static class ObjectAdder {
		ObjectAdder next = null;
		short x;
		short y;
		ObjectContainer obj;
	}

	public void addObject(int x, int y, ObjectContainer obj) {
		if (obj != null) {
			ObjectAdder adder = new ObjectAdder();
			adder.x = (short) x;
			adder.y = (short) y;
			adder.obj = obj;
			adder.next = addObjects;
			addObjects = adder;
		}
	}

	public static class ResourceChanger {
		ResourceChanger next = null;
		short x;
		short y;
		EResourceType type;
		byte amount;
	}

	public ObjectAdder getAddObjects() {
		return addObjects;
	}

	public void changeResource(int x, int y,
			EResourceType type,
			byte amount) {
		ResourceChanger c = new ResourceChanger();
		c.x = (short) x;
		c.y = (short) y;
		c.type = type;
		c.amount = amount;
		c.next = changeResources;
		changeResources = c;
	}

	public ResourceChanger getChangeResources() {
		return changeResources;
	}

	public static class ObjectRemover {
		ObjectRemover next = null;
		short x;
		short y;
	}

	public void removeObject(int x, int y) {
		ObjectRemover remover = new ObjectRemover();
		remover.x = (short) x;
		remover.y = (short) y;
		remover.next = removeObjects;
		removeObjects = remover;
	}

	public ObjectRemover getRemoveObjects() {
		return removeObjects;
	}

	// ignore start item!
	private StartPointSetter startPoints = new StartPointSetter();

	public static class StartPointSetter {
		StartPointSetter next = null;
		int player;
		ShortPoint2D pos;
	}

	public StartPointSetter getStartPoints() {
		return startPoints.next;
	}

	public void setStartPoint(int player, ShortPoint2D pos) {
		StartPointSetter cur = startPoints;
		while (cur.next != null) {
			if (cur.next.player == player) {
				cur.next = cur.next.next;
			}
		}
		StartPointSetter item = new StartPointSetter();
		item.player = player;
		item.pos = pos;
		item.next = startPoints.next;
		startPoints.next = item;
	}

}
