package jsettlers.mapcreator.main.error;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import jsettlers.common.position.ILocatable;

public class ErrorList extends AbstractListModel<ILocatable> {

	/**
     *
     */
    private static final long serialVersionUID = -6645362444519496534L;

	private ArrayList<Error> errors = new ArrayList<Error>();

	public void setErrors(ArrayList<Error> errors) {
		int max = Math.max(errors.size(), this.errors.size());
		this.errors = errors;

		fireContentsChanged(this, 0, max);
	}

	@Override
	public ILocatable getElementAt(int arg0) {
		return errors.get(arg0);
	}

	@Override
	public int getSize() {
		return errors.size();
	}

}
