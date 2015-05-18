package jsettlers;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import jsettlers.common.CommonConstants;
import jsettlers.common.resources.ResourceManager;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.replay.ReplayTool;

import org.junit.Test;

public class AutoReplayIT {
	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
	}

	private static final String remainingReplay = "out/remainingReplay.log";

	@Test
	public void testSingleplayerMountainLake() throws IOException {
		testReplay("basicProduction-mountainlake", 15);
	}

	private void testReplay(String folderName, int targetTimeMinutes) throws IOException {
		TestUtils.setupResourcesManager();

		File replay = new File("resources/autoreplay/" + folderName + "/replay.log");
		ReplayTool.replayAndCreateSavegame(replay, targetTimeMinutes * 60 * 1000, remainingReplay);

		Path savegameFile = findSavegameFile();
		System.out.println("Savegame found: " + savegameFile);
		Path expectedFile = Paths.get("resources/autoreplay/" + folderName + "/savegame.map");

		compareMapFiles(expectedFile, savegameFile);
	}

	private void compareMapFiles(Path expectedFile, Path actualFile) throws IOException {
		BufferedInputStream expectedStream = new BufferedInputStream(Files.newInputStream(expectedFile));
		MapFileHeader expectedHeader = MapFileHeader.readFromStream(expectedStream);
		BufferedInputStream actualStream = new BufferedInputStream(Files.newInputStream(actualFile));
		MapFileHeader actualHeader = MapFileHeader.readFromStream(actualStream);

		assertEquals(expectedHeader.getBaseMapId(), actualHeader.getBaseMapId());

		int e, a;
		int idx = 0;
		while ((e = expectedStream.read()) != -1 & (a = actualStream.read()) != -1) {
			assertEquals("difference at byte " + idx, a, e);
			idx++;
		}
		assertEquals("files have different lengths", e, a);

		expectedStream.close();
		actualStream.close();
	}

	private Path findSavegameFile() throws IOException { // TODO implement better way to find the correct savegame
		Path saveDirPath = new File(ResourceManager.getSaveDirectory(), "save").toPath();

		final Path[] newestFile = new Path[1];
		Files.walkFileTree(saveDirPath, new SimpleFileVisitor<Path>() {
			private FileTime newestCreationTime = null;

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.toString().endsWith(MapList.MAP_EXTENSION)
						&& (newestCreationTime == null || newestCreationTime.compareTo(attrs.creationTime()) < 0)) {
					newestCreationTime = attrs.creationTime();
					newestFile[0] = file;
				}
				return super.visitFile(file, attrs);
			}
		});

		return newestFile[0];
	}
}
