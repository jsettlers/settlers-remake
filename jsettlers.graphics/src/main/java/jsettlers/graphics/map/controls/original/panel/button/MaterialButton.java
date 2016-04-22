package jsettlers.graphics.map.controls.original.panel.button;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIPanel;

/**
 * A special material button with a green/red dot and a
 * 
 * @author Michael Zangl
 */
public class MaterialButton extends Button {

	public enum DotColor {
		RED(7),
		GREEN(0),
		YELLOW(3);

		private OriginalImageLink image;

		private DotColor(int imageIndex) {
			image = new OriginalImageLink(EImageLinkType.SETTLER, 4, 6, imageIndex);
		}

		public OriginalImageLink getImage() {
			return image;
		}
	}

	private final EMaterialType material;
	private final UIPanel dot = new UIPanel();
	private final UIPanel selected = new UIPanel();

	public MaterialButton(Action action, EMaterialType material) {
		super(action, material.getIcon(), material.getIcon(), Labels.getName(material, false));
		this.material = material;
		setBackground(material.getIcon());
		addChild(dot, .1f, .6f, .4f, .9f);
		addChild(selected, 0, 0, 1, 1);
	}

	public void setDotColor(DotColor color) {
		dot.setBackground(color == null ? null : color.image);
	}

	public EMaterialType getMaterial() {
		return material;
	}

	public void setSelected(boolean selected) {
		this.selected.setBackground(selected ? new OriginalImageLink(EImageLinkType.GUI, 3, 339) : null);
	}
}
