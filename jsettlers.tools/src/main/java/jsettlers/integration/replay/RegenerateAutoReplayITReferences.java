package jsettlers.integration.replay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.map.MapUtils;

/**
 * Created by Andreas Eberle on 23.04.2016.
 */
public class RegenerateAutoReplayITReferences {
    public static void main(String[] args) throws IOException, MapLoadException, ClassNotFoundException {
        System.out.println("Creating reference files for replays...");

        for (Object[] replaySet : AutoReplayIT.replaySets()) {
            String folderName = (String) replaySet[0];
            int targetTimeMinutes = (Integer) replaySet[1];

            AutoReplayIT replayIT = new AutoReplayIT(folderName, targetTimeMinutes);
            MapLoader newSavegame = ReplayUtils.replayAndCreateSavegame(replayIT.getReplayFile(), targetTimeMinutes, AutoReplayIT.REMAINING_REPLAY_FILENAME);
            MapLoader expectedSavegame = replayIT.getReferenceSavegamePath();

            try {
                MapUtils.compareMapFiles(expectedSavegame, newSavegame);
                System.out.println("New savegame is equal to old one => won't replace.");
                newSavegame.getListedMap().delete();
            } catch (AssertionError | IOException ex) { // if the files are not equal, replace the existing one.
                Files.move(Paths.get(newSavegame.getListedMap().getFile().toString()),
                        Paths.get(expectedSavegame.getListedMap().getFile().toString()),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Replacing reference file '" + expectedSavegame + "' with new savegame '" + newSavegame + "'");
            }
        }
    }
}
