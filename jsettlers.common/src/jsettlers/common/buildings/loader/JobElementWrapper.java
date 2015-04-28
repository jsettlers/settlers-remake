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
package jsettlers.common.buildings.loader;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;

import org.xml.sax.Attributes;

public class JobElementWrapper implements BuildingJobData {
	private static final String SUCCESSJOB = "successjob";
	private static final String FAILJOB = "failjob";
	private static final String MATERIAL = "material";
	private static final String DY = "dy";
	private static final String DX = "dx";
	private static final String DIRECTION = "direction";
	private static final String TYPE = "type";
	private static final String ATTR_TIME = "time";
	private static final String SEARCH = "search";
	private static final String NAME = "name";

	private final EBuildingJobType type;
	private short dx;
	private short dy;
	private EMaterialType material;
	private ESearchType searchType;
	private String successjob;
	private String failjob;
	private float time;
	private EDirection direction;
	private String name;

	JobElementWrapper(Attributes attributes) {
		type = getType(attributes);
		dx = (short) getAttributeAsInt(attributes, DX);
		dy = (short) getAttributeAsInt(attributes, DY);
		material = getMaterial(attributes);
		searchType = getSearchType(attributes);
		name = attributes.getValue(NAME);
		successjob = attributes.getValue(SUCCESSJOB);
		failjob = attributes.getValue(FAILJOB);
		time = getAttributeAsFloat(attributes, ATTR_TIME);
		direction = getDirection(attributes);
	}

	private static EBuildingJobType getType(Attributes attributes)
			throws IllegalAccessError {
		String typeString = attributes.getValue(TYPE);
		try {
			return EBuildingJobType.valueOf(typeString);
		} catch (IllegalArgumentException e) {
			throw new IllegalAccessError("Job has unknown type: " + typeString);
		}
	}

	@Override
	public EDirection getDirection() {
		return direction;
	}

	private static EDirection getDirection(Attributes attributes) {
		String string = attributes.getValue(DIRECTION);
		if (string == null) {
			return null;
		} else {
			try {
				return EDirection.valueOf(string);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	private static int getAttributeAsInt(Attributes attributes, String attribute) {
		String string = attributes.getValue(attribute);
		if (string == null) {
			return 0;
		} else {
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	@Override
	public short getDx() {
		return dx;
	}

	@Override
	public short getDy() {
		return dy;
	}

	private static EMaterialType getMaterial(Attributes attributes) {
		String string = attributes.getValue(MATERIAL);
		if (string == null) {
			return null;
		} else {
			try {
				return EMaterialType.valueOf(string);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	@Override
	public EMaterialType getMaterial() {
		return material;
	}

	public ESearchType getSearchType(Attributes attributes) {
		String string = attributes.getValue(SEARCH);
		if (string == null) {
			return null;
		} else {
			try {
				return ESearchType.valueOf(string);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	@Override
	public ESearchType getSearchType() {
		return searchType;
	}

	@Override
	public String getNextFailJob() {
		return failjob;
	}

	@Override
	public String getNextSucessJob() {
		return successjob;
	}

	@Override
	public float getTime() {
		return time;
	}

	private static float getAttributeAsFloat(Attributes attributes, String attribute) {
		String string = attributes.getValue(attribute);
		if (string == null) {
			return 0f;
		} else {
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException e) {
				return 0f;
			}
		}
	}

	@Override
	public EBuildingJobType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

}
