package jsettlers.mapcreator.main.error;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import jsettlers.mapcreator.localization.EditorLabels;

public class ShowErrorsButton extends JButton implements ActionListener, ListDataListener {
	/**
     * 
     */
	private static final long serialVersionUID = -1759142509787969743L;
	private final ErrorList list;
	private final IScrollToAble scrollTo;
	private ErrorsWindow window = null;

	public ShowErrorsButton(ErrorList list, IScrollToAble scrollTo) {
		super(EditorLabels.getLabel("errors"));
		this.list = list;
		this.scrollTo = scrollTo;
		addActionListener(this);
		list.addListDataListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (window == null || window.isClosed()) {
			window = new ErrorsWindow(list, scrollTo);
		} else {
			window.show();
		}
	}

	@Override
	public Color getForeground() {
		if (isEnabled()) {
			return Color.RED;
		} else {
			return super.getForeground();
		}
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		setText(String.format(EditorLabels.getLabel("errors_n"), list.getSize()));
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
	}
}
