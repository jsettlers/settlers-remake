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
package jsettlers.mapcreator.presetloader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jsettlers.logic.map.loading.data.objects.StackMapDataObject;
import jsettlers.mapcreator.control.IPlayerSetter;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.presetloader.jaxb.Building;
import jsettlers.mapcreator.presetloader.jaxb.Moveable;
import jsettlers.mapcreator.presetloader.jaxb.Object;
import jsettlers.mapcreator.presetloader.jaxb.Preset;
import jsettlers.mapcreator.presetloader.jaxb.Presets;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateBuilding;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateMovable;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateObject;

/**
 * Loads preset templates from .xml
 * 
 * <h3>Create .xsd with</h3> <code>DevelopmentGenerateJaxbSchema.java</code>
 * 
 * @author Andreas Butti
 *
 */
public class PresetLoader {

	/**
	 * Node to add presets to
	 */
	private final ToolBox node;

	/**
	 * Interface to get current player
	 */
	private final IPlayerSetter player;

	/**
	 * Constructor
	 * 
	 * @param node
	 *            Node to add presets to
	 * @param player
	 *            Interface to get current player
	 */
	public PresetLoader(ToolBox node, IPlayerSetter player) {
		this.node = node;
		this.player = player;
	}

	/**
	 * Load an .xml
	 * 
	 * @param in
	 *            Input stream
	 * @throws JAXBException
	 */
	public void load(InputStream in) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Presets.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Presets xml = (Presets) unmarshaller.unmarshal(in);

		for (Preset p : xml.getPreset()) {
			TemplateObject[] objectes = loadObjects(p);
			PlaceTemplateTool template = new PlaceTemplateTool(EditorLabels.getLabel(p.getTextId()), objectes, player);
			node.add(template);
		}
	}

	/**
	 * Create the list with the objects
	 * 
	 * @param p
	 *            Preset
	 * @return Objectlist
	 */
	private TemplateObject[] loadObjects(Preset p) {
		List<TemplateObject> list = new ArrayList<>();

		for (Building b : p.getBuilding()) {
			list.add(new TemplateBuilding(b.getDx(), b.getDy(), b.getType()));
		}

		for (Moveable m : p.getMoveable()) {
			list.add(new TemplateMovable(m.getDx(), m.getDy(), m.getType()));
		}

		for (Object o : p.getObject()) {
			list.add(new TemplateObject(o.getDx(), o.getDy(), new StackMapDataObject(o.getType(), o.getCount())));
		}

		return list.toArray(new TemplateObject[] {});
	}

}
