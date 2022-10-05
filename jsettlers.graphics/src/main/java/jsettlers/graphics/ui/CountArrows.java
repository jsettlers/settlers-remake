package jsettlers.graphics.ui;

import java8.util.function.Supplier;

import jsettlers.common.action.Action;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;

public class CountArrows extends UIPanel {
	private static final ImageLink arrowsImageLink = new OriginalImageLink(EImageLinkType.GUI, 3, 231, 0); // checked in the original game

	public CountArrows(Supplier<Action> increase, Supplier<Action> decrease) {
		Button upButton = new ArrowButton(increase);
		Button downButton = new ArrowButton(decrease);

		addChild(upButton, 0f, 0.5f, 1f, 1f);
		addChild(downButton, 0f, 0f, 1f, 0.5f);}

	@Override
	protected ImageLink getBackgroundImage() {
		return arrowsImageLink;
	}

	private class ArrowButton extends Button {

		private Supplier<Action> action;

		public ArrowButton(Supplier<Action> action) {
			super(null, null, null, "");
			this.action = action;
		}

		@Override
		public Action getAction() {
			return action.get();
		}
	}
}
