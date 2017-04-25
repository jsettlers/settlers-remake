package jsettlers.main.android.core.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by tompr on 10/03/2017.
 */

public class Dispatcher {
	private final Handler handler;
	private final long threadId;

	public Dispatcher() {
		Looper mainLooper = Looper.getMainLooper();

		handler = new Handler(mainLooper);
		threadId = mainLooper.getThread().getId();
	}

	public void runOnMainThread(Runnable runnable) {
		if (threadId > 0 && Thread.currentThread().getId() == threadId) {
			runnable.run();
		} else {
			handler.post(runnable);
		}
	}
}
