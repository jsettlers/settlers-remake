package jsettlers.mapcreator.main.error;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jsettlers.common.position.ILocatable;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * This is a window that contains a list of errors.
 * @author michaelz
 *
 */
public class ErrorsWindow implements ListSelectionListener {

	private final JFrame window;
	private final JList<ILocatable> elist;
	private final ErrorList list;
	private final IScrollToAble scrollTo;

	public ErrorsWindow(ErrorList list, IScrollToAble scrollTo) {
		this.list = list;
		this.scrollTo = scrollTo;
		elist = new JList<ILocatable>(list);
		elist.addListSelectionListener(this);

		window = new JFrame(EditorLabels.getLabel("errors"));
		window.add(new JScrollPane(elist));
		window.setPreferredSize(new Dimension(500, 300));
		window.pack();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public boolean isClosed() {
		return !window.isVisible();
	}

	@Override
    public void valueChanged(ListSelectionEvent arg0) {
	    int index = elist.getSelectedIndex();
	    if (index  > 0) {
	    	scrollTo.scrollTo(list.getElementAt(index).getPos());
	    }
    }

	public void show() {
	    window.toFront();
    }

}
