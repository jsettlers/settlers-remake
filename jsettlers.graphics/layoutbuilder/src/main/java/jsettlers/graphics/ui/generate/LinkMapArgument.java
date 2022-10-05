package jsettlers.graphics.ui.generate;

import org.xml.sax.Attributes;

public class LinkMapArgument extends AbstractArgument {

	private String type;
	private String movable;

	public LinkMapArgument(Attributes attributes) {
		super();

		type = attributes.getValue("type");
		movable = attributes.getValue("movableType");
	}

	@Override
	public String getArgumentSource() {
		return "jsettlers.graphics.map.draw.ImageLinkMap.get(__civilisation, jsettlers.graphics.map.draw.ECommonLinkType." + type + ", jsettlers.common.movable.EMovableType." + movable + ")";
	}
}
