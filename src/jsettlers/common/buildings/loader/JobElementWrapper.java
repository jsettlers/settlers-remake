package jsettlers.common.buildings.loader;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;

import org.w3c.dom.Element;

public class JobElementWrapper implements BuildingJobData {
	private static final String SUCCESSJOB = "successjob";
	private static final String FAILJOB = "failjob";
	private static final String MATERIAL = "material";
	private static final String DY = "dy";
	private static final String DX = "dx";
	private static final String DIRECTION = "direction";
	private static final String TYPE2 = "type";
	private static final String ATTR_TIME = "time";
	private static final String SEARCH = "search";
	private final Element element;
	private final EBuildingJobType type;

	JobElementWrapper(Element element) {
		this.element = element;

		type = getType(element);
	}

	private EBuildingJobType getType(Element element) throws IllegalAccessError {
		String typeString = element.getAttribute(TYPE2);
		try {
			return EBuildingJobType.valueOf(typeString);
		} catch (IllegalArgumentException e) {
			throw new IllegalAccessError("Job has unknown type: " + typeString);
		}
	}

	@Override
	public EDirection getDirection() {
		String string = element.getAttribute(DIRECTION);
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return EDirection.valueOf(string);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	@Override
	public short getDx() {
		String attribute = DX;
		return (short) getAttributeAsInt(attribute);
	}

	private int getAttributeAsInt(String attribute) {
		String string = element.getAttribute(attribute);
		if (string.isEmpty()) {
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
	public short getDy() {
		String attribute = DY;
		return (short) getAttributeAsInt(attribute);
	}

	@Override
	public EMaterialType getMaterial() {
		String string = element.getAttribute(MATERIAL);
		if (string.isEmpty()) {
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
	public ESearchType getSearchType() {
		String string = element.getAttribute(SEARCH);
		if (string.isEmpty()) {
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
	public String getNextFailJob() {
		return element.getAttribute(FAILJOB);
	}

	@Override
	public String getNextSucessJob() {
		return element.getAttribute(SUCCESSJOB);
	}

	@Override
	public float getTime() {
		String attribute = ATTR_TIME;
		return getAttributeAsFloat(attribute);
	}

	private float getAttributeAsFloat(String attribute) {
		String string = element.getAttribute(attribute);
		if (string.isEmpty()) {
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

}
