package jsettlers.mapcreator.presetloader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.mapcreator.control.IPlayerSetter;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.presetloader.jaxb.Building;
import jsettlers.mapcreator.presetloader.jaxb.Moveable;
import jsettlers.mapcreator.presetloader.jaxb.Object;
import jsettlers.mapcreator.presetloader.jaxb.ObjectFactory;
import jsettlers.mapcreator.presetloader.jaxb.Preset;
import jsettlers.mapcreator.presetloader.jaxb.Presets;
import jsettlers.mapcreator.tools.DynamicToolBox;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateBuilding;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateMovable;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateObject;

/**
 * Loads preset templates from .xml
 * 
 * <h3>Create Jaxb classes from .xsd</h3>
 * <code>xjc -d src -p jsettlers.mapcreator.presetloader.jaxb src/jsettlers/mapcreator/presetloader/preset.xsd</code>
 * 
 * @author Andreas Butti
 *
 */
public class PresetLoader {

	/**
	 * Node to add presets to
	 */
	private final DynamicToolBox node;

	/**
	 * Interface to get current player
	 */
	private IPlayerSetter player;

	/**
	 * Constructor
	 * 
	 * @param node
	 *            Node to add presets to
	 * @param player
	 *            Interface to get current player
	 */
	public PresetLoader(DynamicToolBox node, IPlayerSetter player) {
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
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
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
			list.add(new TemplateBuilding(b.getDx(), b.getDy(), EBuildingType.valueOf(b.getType())));
		}

		for (Moveable m : p.getMoveable()) {
			list.add(new TemplateMovable(m.getDx(), m.getDy(), EMovableType.valueOf(m.getType())));
		}

		for (Object o : p.getObject()) {
			list.add(new TemplateObject(o.getDx(), o.getDy(), new StackObject(EMaterialType.valueOf(o.getType()), o.getCount())));
		}

		return list.toArray(new TemplateObject[] {});
	}

}
