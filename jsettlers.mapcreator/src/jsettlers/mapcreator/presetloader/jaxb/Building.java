package jsettlers.mapcreator.presetloader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import jsettlers.common.buildings.EBuildingType;

/**
 * Building
 * 
 * @author Andreas Butti
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Building {

	@XmlAttribute(name = "dx")
	protected Integer dx;
	@XmlAttribute(name = "dy")
	protected Integer dy;
	@XmlAttribute(name = "type")
	protected EBuildingType type;

	/**
	 * Gets the value of the dx property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getDx() {
		return dx;
	}

	/**
	 * Sets the value of the dx property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setDx(Integer value) {
		this.dx = value;
	}

	/**
	 * Gets the value of the dy property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getDy() {
		return dy;
	}

	/**
	 * Sets the value of the dy property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setDy(Integer value) {
		this.dy = value;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return EBuildingType
	 * 
	 */
	public EBuildingType getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            EBuildingType
	 * 
	 */
	public void setType(EBuildingType value) {
		this.type = value;
	}

}
