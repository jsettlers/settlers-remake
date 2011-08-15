package jsettlers.graphics.progress;

public class ProgressConnector {
	private final ProgressContent content;

	public ProgressConnector(ProgressContent content) {
		this.content = content;
	}
	
	public void setProgressState(EProgressState state) {
		content.setProgressState(state);
	}
}
