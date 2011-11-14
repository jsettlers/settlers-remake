package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.map.selection.SettlerSelection;
import jsettlers.graphics.utils.UIPanel;

public class BearerSelection implements IContentProvider {
	private UIPanel panel;
	private int count;

	public BearerSelection(SettlerSelection selection) {
		panel = new UIPanel();
		count = selection.getMovableCount(EMovableType.BEARER);
		
		addPioneers(.7f);
		addGeologists(.45f);
		addThieves(.2f);
	}

	private void addPioneers(float bottom) {
		ImageLink imageLink = new ImageLink(EImageLinkType.GUI, 14, 210, 0);
		EActionType action1 = EActionType.CONVERT_ONE_PIONEER;
		EActionType actionall = EActionType.CONVERT_ALL_PIONEER;
		
	    drawButtongroup(bottom, imageLink, action1, actionall);
    }

	private void addThieves(float bottom) {
		ImageLink imageLink = new ImageLink(EImageLinkType.GUI, 14, 189, 0);
		EActionType action1 = EActionType.CONVERT_ONE_PIONEER;
		EActionType actionall = EActionType.CONVERT_ALL_PIONEER;
		
	    drawButtongroup(bottom, imageLink, action1, actionall);
    }

	private void addGeologists(float bottom) {
		ImageLink imageLink = new ImageLink(EImageLinkType.GUI, 14, 192, 0);
		EActionType action1 = EActionType.CONVERT_ONE_GEOLOGIST;
		EActionType actionall = EActionType.CONVERT_ALL_GEOLOGIST;
		
	    drawButtongroup(bottom, imageLink, action1, actionall);
    }

	private void drawButtongroup(float bottom, ImageLink imageLink,
            EActionType action1, EActionType actionall) {
	    UIPanel icon = new UIPanel();
		icon.setBackground(imageLink);
		
		//TODO:Labels
		UILabeledButton convert1 = new UILabeledButton("convert 1", new Action(action1));
		UILabeledButton convertall = new UILabeledButton("convert all", new Action(actionall));
		
		panel.addChild(icon, .1f, bottom, .3f, bottom + .2f);
		panel.addChild(convert1, .3f, bottom + .1f, .9f, bottom + .2f);
		panel.addChild(convertall, .3f, bottom, .9f, bottom + .1f);
    }
	

	@Override
    public UIPanel getPanel() {
	    return panel;
    }

	@Override
    public IContextListener getContextListener() {
	    return null;
    }

	@Override
    public ESecondaryTabType getTabs() {
	    return null;
    }
	
	public static EMovableType[] getTypes() {
		return new EMovableType[] {EMovableType.BEARER};
	}
}
