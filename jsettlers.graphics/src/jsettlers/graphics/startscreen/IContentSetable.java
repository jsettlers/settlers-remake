package jsettlers.graphics.startscreen;

import go.graphics.region.RegionContent;
import go.graphics.sound.SoundPlayer;
import jsettlers.graphics.utils.UIPanel;

/**
 * Classes implementing this interface allow you to set the screen content and to access the sound player.
 * 
 * @author michael
 */
public interface IContentSetable {
	void setContent(UIPanel panel);

	void setContent(RegionContent panel);

	SoundPlayer getSoundPlayer();

	void goToStartScreen(String message);
}
