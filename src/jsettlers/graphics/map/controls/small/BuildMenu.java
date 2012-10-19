package jsettlers.graphics.map.controls.small;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionMap;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.mobile.MobileMenu;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.ImageUtils;
import jsettlers.graphics.utils.UIPanel;

public class BuildMenu extends UIPanel implements MobileMenu {
	private static final int LINE_PADDING = 3;
	private static final int BUILDINGS_PER_LINE = 4;
	private Section[] sections = new Section[] {
	new Section(Labels.getString("buildingtypes_normal"),
	        BuildingBuildContent.normalBuildings),
	        new Section(Labels.getString("buildingtypes_food"),
	                BuildingBuildContent.foodBuildings),
	        new Section(Labels.getString("buildingtypes_military"),
	                BuildingBuildContent.militaryBuildings),
	        new Section(Labels.getString("buildingtypes_social"),
	                BuildingBuildContent.socialBuildings),
	};
	private BuildMenuScroller scrollhandler = new BuildMenuScroller();

	private double currentscroll = 0;
	private double currentheight = 0;

	private boolean actionMapInvalid = true;

	private ActionMap actionMap = new ActionMap();

	@Override
	public void drawAt(GLDrawContext gl) {
		drawContent(gl);
	}

	private void drawContent(GLDrawContext gl) {
		if (actionMapInvalid) {
			actionMap.removeAll();
		}
		gl.color(1, 1, 1, 1);
		gl.fillQuad(getPosition().getMinX(), getPosition().getMinY(),
		        getPosition().getMaxX(), getPosition().getMaxY());

		int startdy =
		        (int) (getPosition().getMaxY() + clampScrollY(currentscroll
		                + scrollhandler.additional));
		int dy = startdy;

		for (Section section : sections) {
			dy = drawSection(gl, section, dy);
		}

		currentheight = startdy - dy;
		actionMapInvalid = false;
	}

	private int drawSection(GLDrawContext gl, Section section, int startdy) {
		int dy = startdy;

		TextDrawer textdrawer = gl.getTextDrawer(EFontSize.NORMAL);
		textdrawer.setColor(0, 0, 0, 1);
		double textheight = textdrawer.getHeight(section.name);
		dy -= textheight;
		textdrawer.drawString(getPosition().getWidth() / 8, dy, section.name);

		gl.color(1, 1, 1, 1);
		dy -= LINE_PADDING;
		gl.color(0, 0, 0, 1);
		gl.drawLine(new float[] {
		        0, dy, 0, getPosition().getWidth(), dy, 0,
		}, false);
		dy -= LINE_PADDING;

		// draw the buildings
		float buildingsperline =
		        Math.max(BUILDINGS_PER_LINE, BUILDINGS_PER_LINE
		                * getPosition().getWidth() / getPosition().getHeight());
		float buildingButtonSize = getPosition().getWidth() / buildingsperline;
		for (int i = 0; i < section.buildings.length; i++) {
			float topy = i / buildingsperline * buildingButtonSize;
			float leftx = i % buildingsperline * buildingButtonSize;

			float bottom = dy - topy - buildingButtonSize;
			float texttop = (int) (bottom + textheight);
			float top = dy - topy;
			EBuildingType buildingType = section.buildings[i];
			gl.color(1, 1, 1, 1);
			Image image =
			        ImageProvider.getInstance().getImage(
			        		buildingType.getGuiImage());
			if (image == null) {
				continue;
			}
			ImageUtils.drawAtRectAspect(gl, image, leftx, texttop, leftx
			        + buildingButtonSize, top);
			textdrawer.renderCentered(leftx + buildingButtonSize / 2, bottom
			        + (int) (textheight / 2), Labels.getName(buildingType));

			if (actionMapInvalid) {
				actionMap.addAction(new BuildAction(buildingType),
				        new FloatRectangle(leftx, bottom, leftx
				                + buildingButtonSize, top));
			}
		}

		dy -=
		        ((section.buildings.length - 1) / buildingsperline + 1)
		                * buildingButtonSize;

		return dy;
	}

	@Override
	public void setPosition(FloatRectangle position) {
		super.setPosition(position);
		actionMapInvalid = true;
	}

	private class Section {
		private final String name;
		private final EBuildingType[] buildings;

		private Section(String name, EBuildingType[] buildings) {
			this.name = name;
			this.buildings = buildings;
		}
	}

	public Action getActionFor(float x, float y) {
		return actionMap.getAction(new UIPoint(x, y - currentscroll));
	}

	public Action getActionFor(UIPoint p) {
		return actionMap.getAction(new UIPoint(p.getX(), p.getY()
		        - currentscroll));
	}

	public GOEventHandler getScrollHandler() {
		return scrollhandler;
	}

	private class BuildMenuScroller implements GOModalEventHandler {
		private double starty;
		private double additional;

		@Override
		public void phaseChanged(GOEvent event) {
			if (event.getPhase() == GOEvent.PHASE_STARTED) {
				starty = ((GODrawEvent) event).getDrawPosition().getY();
			}
		}

		@Override
		public void finished(GOEvent event) {
			scrollRelative(((GODrawEvent) event).getDrawPosition().getY()
			        - starty, true);
		}

		@Override
		public void aborted(GOEvent event) {
			scrollRelative(0, true);
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			scrollRelative(((GODrawEvent) event).getDrawPosition().getY()
			        - starty, false);
		}

		private void scrollRelative(double distance, boolean end) {
			System.out.println(distance);
			if (end) {
				currentscroll = clampScrollY(currentscroll + additional);
				additional = 0;
			} else {
				additional = distance;
			}
		}
	}

	private double clampScrollY(double scrolly) {
		if (scrolly < 0) {
			return 0;
		} else if (scrolly > currentheight - getPosition().getHeight()) {
			return currentheight - getPosition().getHeight();
		} else {
			return scrolly;
		}
	}

}
