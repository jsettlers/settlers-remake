/*******************************************************************************
 * Copyright (c) 2015 - 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jsettlers.mapcreator.mapvalidator.IScrollToAble;
import jsettlers.mapcreator.mapvalidator.ValidationResultListener;
import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;
import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;
import jsettlers.mapcreator.mapvalidator.result.fix.FixData;

/**
 * Sidebar with the error messages
 * 
 * @author Andreas Butti
 */
public class ErrorSidebar extends JScrollPane implements ValidationResultListener {
	private static final long serialVersionUID = 1L;

	/**
	 * List with the error entries
	 */
	private JList<AbstractErrorEntry> errorList = new JList<>();

	/**
	 * Interface to scroll to position
	 */
	private final IScrollToAble scrollTo;

	/**
	 * Fix data helper
	 */
	private FixData fixData;

	/**
	 * Listener to react to list clicks
	 */
	private ListSelectionListener selectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			AbstractErrorEntry value = errorList.getSelectedValue();
			if (value == null) {
				return;
			}

			if (value instanceof ErrorEntry) {
				ErrorEntry entry = (ErrorEntry) value;
				scrollTo.scrollTo(entry.getPos());
			} else if (value instanceof ErrorHeader) {
				ErrorHeader header = (ErrorHeader) value;
				AbstractFix fix = header.getFix();
				if (fix == null) {
					return;
				}

				if (fix.isFixAvailable()) {
					fix.setData(fixData);
					JPopupMenu menu = fix.getPopupMenu();
					int selectedIndex = errorList.getSelectedIndex();
					Rectangle bounds = errorList.getCellBounds(selectedIndex, selectedIndex);

					menu.show(errorList, 0, bounds.y + bounds.height);
				}
			}
		}

	};

	/**
	 * Constructor
	 * 
	 * @param scrollTo
	 *            Interface to scroll to position
	 */
	public ErrorSidebar(IScrollToAble scrollTo) {
		super();
		setViewportView(errorList);
		this.scrollTo = scrollTo;
		this.errorList.setCellRenderer(new ErrorListRenderer());
		errorList.addListSelectionListener(selectionListener);

	}

	/**
	 * @param fixData
	 *            Fix data helper
	 */
	public void setFixData(FixData fixData) {
		this.fixData = fixData;
	}

	@Override
	public void validationFinished(ValidationListModel list) {
		errorList.setModel(list);
	}

}
