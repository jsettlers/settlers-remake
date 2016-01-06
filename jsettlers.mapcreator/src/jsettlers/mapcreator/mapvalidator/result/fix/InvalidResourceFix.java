package jsettlers.mapcreator.mapvalidator.result.fix;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Delete invalid resources
 * 
 * @author Andreas Butti
 *
 */
public class InvalidResourceFix extends AbstractFix {

	/**
	 * List with invalid resource position
	 */
	private List<ShortPoint2D> list = new ArrayList<>();

	/**
	 * Constructor
	 */
	public InvalidResourceFix() {
	}

	@Override
	public boolean isFixAvailable() {
		return list.size() > 0;
	}

	@Override
	public JPopupMenu getPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuFix = new JMenuItem(EditorLabels.getLabel("fix.delete-invalid-resources"));
		menuFix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				autoFix();
			}
		});
		menu.add(menuFix);
		return menu;
	}

	@Override
	public void autoFix() {
		MapData map = data.getMap();
		for (ShortPoint2D p : list) {
			map.decreaseResourceTo(p.x, p.y, (byte) 0);
		}

		data.getUndoRedo().endUseStep();
		data.getValidator().reValidate();
	}

	/**
	 * Add an invalid resource position
	 * 
	 * @param point
	 *            Position
	 */
	public void addInvalidResource(ShortPoint2D point) {
		list.add(point);
	}

}
