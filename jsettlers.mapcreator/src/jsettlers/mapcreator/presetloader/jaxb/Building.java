/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
