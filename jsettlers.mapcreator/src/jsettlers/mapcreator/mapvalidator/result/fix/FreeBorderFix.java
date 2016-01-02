package jsettlers.mapcreator.mapvalidator.result.fix;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Delete invalid resources
 * 
 * @author Andreas Butti
 *
 */
public class FreeBorderFix implements IFix, IMapArea {
	private static final long serialVersionUID = 1L;

	/**
	 * Points to delete
	 */
	private List<ShortPoint2D> points = new ArrayList<>();

	/**
	 * Menu to display with possible fixes
	 */
	private JPopupMenu menu = new JPopupMenu();

	/**
	 * Constructor
	 * 
	 * @param data
	 *            Map data, to fix
	 */
	public FreeBorderFix(final MapData data) {
		JMenuItem menuFix = new JMenuItem(EditorLabels.getLabel("fix.free-borders"));
		menuFix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO UNDO / REDO !
				data.fill(ELandscapeType.WATER8, FreeBorderFix.this);
			}
		});
		menu.add(menuFix);
	}

	/**
	 * Add a point, which should be freed
	 * 
	 * @param p
	 *            Point
	 */
	public void addPosition(ShortPoint2D p) {
		points.add(p);
	}

	@Override
	public boolean isFixAvailable() {
		return points.size() > 0;
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return menu;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return points.contains(position);
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return points.iterator();
	}

}
