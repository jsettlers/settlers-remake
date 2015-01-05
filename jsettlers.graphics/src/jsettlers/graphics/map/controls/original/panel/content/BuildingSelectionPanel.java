package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

public class BuildingSelectionPanel implements IContentProvider {
	private static final OriginalImageLink SOILDER_MISSING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 45, 0);
	private static final OriginalImageLink SOILDER_COMMING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 48, 0);
	private final IBuilding building;
	private final UIPanel panel;

	private static OriginalImageLink SET_WORK_AREA = new OriginalImageLink(EImageLinkType.GUI,
			3, 201, 0);
	private static OriginalImageLink PRIORITY_STOPPED = new OriginalImageLink(EImageLinkType.GUI,
			3, 192, 0);
	private static OriginalImageLink PRIORITY_LOW = new OriginalImageLink(EImageLinkType.GUI,
			3, 195, 0);
	private static OriginalImageLink PRIORITY_HIGH = new OriginalImageLink(EImageLinkType.GUI,
			3, 378, 0);

	private static OriginalImageLink DESTROY = new OriginalImageLink(EImageLinkType.GUI, 3,
			198, 0);
	private static OriginalImageLink NEXT = new OriginalImageLink(EImageLinkType.GUI, 3,
			230, 0);

	private BuildingState lastState = null;

	public BuildingSelectionPanel(ISelectionSet selection) {
		building = (IBuilding) selection.get(0);

		ImageLink[] images = building.getBuildingType().getImages();
		panel = new BuidlingBackgroundPanel(images);

		lastState = new BuildingState(building);
		addPanelContent();
	}

	private void addPanelContent() {
		addPriorityButton();

		if (building.getBuildingType().getWorkradius() > 0) {
			Button setWorkcenter =
					new Button(new Action(EActionType.ASK_SET_WORK_AREA),
							SET_WORK_AREA, SET_WORK_AREA,
							Labels.getName(EActionType.SET_WORK_AREA));
			panel.addChild(setWorkcenter, .4f, .9f, .6f, 1);
		}

		Button destroy =
				new Button(new Action(EActionType.ASK_DESTROY), DESTROY,
						DESTROY, Labels.getName(EActionType.DESTROY));
		panel.addChild(destroy, .8f, .9f, 1, 1);
		Button next =
				new Button(new Action(EActionType.NEXT_OF_TYPE), NEXT,
						NEXT, Labels.getName(EActionType.NEXT_OF_TYPE));
		panel.addChild(next, .6f, .9f, .8f, 1);

		UIPanel namePanel = new UIPanel() {
			@Override
			public void drawAt(GLDrawContext gl) {
				gl.getTextDrawer(EFontSize.HEADLINE).renderCentered(
						getPosition().getCenterX(), getPosition().getCenterY(),
						Labels.getName(building.getBuildingType()));
			}
		};
		panel.addChild(namePanel, 0, .8f, 1, .9f);

		if (building instanceof IBuilding.IOccupyed) {
			List<? extends IBuildingOccupyer> occupyers =
					((IBuilding.IOccupyed) building).getOccupyers();
			addOccupyerPlaces(occupyers);

		}
	}

	private void addPriorityButton() {
		EPriority[] supported = building.getSupportedPriorities();
		if (supported.length < 2) {
			return;
		}

		EPriority priority = building.getPriority();
		OriginalImageLink image;
		switch (priority) {
		case HIGH:
			image = PRIORITY_HIGH;
			break;
		case LOW:
			image = PRIORITY_LOW;
			break;
		case STOPPED:
		default:
			image = PRIORITY_STOPPED;
			break;
		}

		EPriority next = supported[0];
		for (int i = 0; i < supported.length; i++) {
			if (supported[i] == priority) {
				next = supported[(i + 1) % supported.length];
			}
		}

		Button changeWorking = new Button(new SetBuildingPriorityAction(next),
				image, image,
				Labels.getString("priority_" + next));
		panel.addChild(changeWorking, 0, .9f, .2f, 1);
	}

	private void addOccupyerPlaces(List<? extends IBuildingOccupyer> occupyers) {
		int bottomindex = 0;

		int topindex = 0;

		for (IBuildingOccupyer occupyer : occupyers) {
			OccupyerPlace place = occupyer.getPlace();
			OriginalImageLink link = SOILDER_COMMING;
			boolean big = false;

			if (occupyer.getMovable() != null) {
				link = getIconFor(occupyer.getMovable());
				big = true;
			}
			float width = big ? .2f : .1f;
			float height = big ? .15f : .05f;

			Button button = new Button(null, link, link, "");
			if (place.getType() == ESoldierType.BOWMAN) {
				float left = topindex * .1f + .3f;
				panel.addChild(button, left, .6f, left + width, .6f + height);
				topindex++;
			} else {
				float left = bottomindex * .1f + .1f;
				panel.addChild(button, left, .4f, left + width, .4f + height);
				bottomindex++;
			}
		}
	}

	private static OriginalImageLink getIconFor(IMovable movable) {
		switch (movable.getMovableType()) {
		case SWORDSMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 213, 0);
		case SWORDSMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 222, 0);
		case SWORDSMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 231, 0);
		case PIKEMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 216, 0);
		case PIKEMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 225, 0);
		case PIKEMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 234, 0);
		case BOWMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 219, 0);
		case BOWMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 228, 0);
		case BOWMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 237, 0);

		default:
			System.err.println("A unknown image was requested for gui. "
					+ "Type=" + movable.getMovableType());
			return new OriginalImageLink(EImageLinkType.GUI, 24, 213, 0);
		}
	}

	private class BuidlingBackgroundPanel extends UIPanel {
		private final ImageLink[] links;

		public BuidlingBackgroundPanel(ImageLink[] images) {
			this.links = images;
		}

		@Override
		public void drawAt(GLDrawContext gl) {
			refreshContentIfNeeded();
			super.drawAt(gl);
		}

		@Override
		protected void drawBackground(GLDrawContext gl) {
			float cx = getPosition().getCenterX();
			float cy = getPosition().getCenterY();

			for (ImageLink link : links) {
				ImageProvider.getInstance().getImage(link).drawAt(gl, cx, cy);
			}
		}

	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	public void refreshContentIfNeeded() {
		if (!lastState.isStillInState(building)) {
			panel.removeAll();
			addPanelContent();
			lastState = new BuildingState(building);
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return null;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
	}

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
	}
}
