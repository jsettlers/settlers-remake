package jsettlers.graphics.map.controls.original.panel;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.utils.Button;

/**
 * This is a button intended to be used tp change the content of the main panel.
 * 
 * @author michael
 */
public class TabButton extends Button {

	private final EContentType content;

	public TabButton(EContentType content, int file, int image,
	        int activeImage, String description) {
		this(content, new ImageLink(EImageLinkType.GUI, file, image, 0),
		        new ImageLink(EImageLinkType.GUI, file, activeImage, 0),
		        description);
	}

	public TabButton(EContentType content, ImageLink image,
	        ImageLink activeImage, String description) {
		super(new ChangePanelAction(content), image, activeImage, description);
		this.content = content;
	}

	public void setActiveByContent(EContentType content) {
		setActive(content.equals(this.content));
	}

}
