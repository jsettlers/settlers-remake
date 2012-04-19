package jsettlers.graphics.action;

public interface ActionThreadBlockingListener {
	void actionThreadSlow(boolean isBlocking);

	void actionThreadCoughtException(Throwable e);
}
