package jsettlers.graphics.androidui.menu;

import jsettlers.graphics.androidui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public abstract class Dialog extends AndroidMenu {

	public Dialog(AndroidMenuPutable puttable) {
		super(puttable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView message = (TextView) view.findViewById(R.id.dialog_text);
		message.setText(getMessageId());

		Button ok = (Button) view.findViewById(R.id.dialog_ok);
		ok.setText(getOkId());
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				okClicked();
			}
		});

		Button abort = (Button) view.findViewById(R.id.dialog_abort);
		abort.setText(getAbortId());
		abort.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				abortClicked();
			}
		});

	}

	protected abstract int getMessageId();

	protected abstract int getOkId();

	protected abstract int getAbortId();

	protected abstract void okClicked();

	protected void abortClicked() {
		getPutable().hideMenu();
	}
}
