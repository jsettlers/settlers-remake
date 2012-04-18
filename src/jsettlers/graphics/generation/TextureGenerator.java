package jsettlers.graphics.generation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.ImageDataPrivider;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;

/**
 * This class lets you generate a texture that can be understood by the graphics
 * module. It generates the .texture file.
 * 
 * @author michael
 */
public final class TextureGenerator {

	private static final Pattern ORIGINAL_SETTLER = Pattern
	        .compile("original_\\d+_SETTLER_\\d+_\\d+");
	private final File rawDirectory;
	private final File outDirectory;
	private final TextureIndex textureIndex;

	public TextureGenerator(
	        TextureIndex textureIndex) {
		this.textureIndex = textureIndex;
		rawDirectory = new File("../jsettlers.common/resources/textures_raw");
		outDirectory = new File("../jsettlers.common/resources/images");

	}
	
	public void addTexturesByName(List<String> list) {
		for (String name : list) {
			addIdToTexture(name);
		}
	}

	private void addIdToTexture(String name) {
		Matcher matcher = ORIGINAL_SETTLER.matcher(name);
		ImageDataPrivider data = null;
		ImageDataPrivider torso = null;
		if (matcher.matches()) {
			File datfile = null; // TODO: Load dat file matcher.group(1)
			AdvancedDatFileReader reader = new AdvancedDatFileReader(datfile);
			Image image =
			        reader.getSettlers()
			                .get(Integer.parseInt(matcher.group(2)))
			                .getImageSafe(Integer.parseInt(matcher.group(3)));
			if (image instanceof SingleImage) {
				data = (SingleImage) image;
			}
			if (image instanceof SettlerImage) {
				torso = (SingleImage) ((SettlerImage) image).getTorso();
			}
		} else {
			data = getImage(name);
			torso = getImage(name + ".t");
		}

		if (data == null) {
			System.err.println("WATNING: loading image " + name
			        + ": No image file found.");
		}

		try {
			if (data != null) {
				int texture = textureIndex.getNextTextureIndex();
				TexturePosition position = addAsNewImage(data, texture);
				textureIndex.registerTexture(name, texture, data.getOffsetX(),
				        data.getOffsetY(), data.getWidth(), data.getHeight(), torso != null, position);
			}
		} catch (Throwable t) {
			System.err.println("WARNING: Problem writing image " + name
			        + ". Pronblem was: " + t.getMessage());
		}
		try {
			if (torso != null) {
				int texture = textureIndex.getNextTextureIndex();
				TexturePosition position = addAsNewImage(torso, texture);
				textureIndex.registerTexture(name, texture, torso.getOffsetX(),
						torso.getOffsetY(), 0, 0, false, position);
			}
		} catch (Throwable t) {
			System.err.println("WARNING: Problem writing image " + name
			        + ". Pronblem was: " + t.getMessage());
		}
	}

	private TexturePosition addAsNewImage(ImageDataPrivider data, int texture)
	        throws IOException {
		int size = getNextPOT(Math.max(data.getWidth(), data.getHeight()));
		TextureFile file =
		        new TextureFile(new File(outDirectory, texture + ""), size,
		                size);
		TexturePosition position =
		        file.addImage(data.getData(), data.getWidth());
		file.write();
		return position;
	}

	private static int getNextPOT(int height) {
		int i = 2;
		while (i < height) {
			i *= 2;
		}
		return i;
	}

	private ImageDataPrivider getImage(String id) {
		try {
			File imageFile = new File(rawDirectory, id + ".png");
			int[] offsets = getOffsets(id);
			BufferedImage image = ImageIO.read(imageFile);

			return new ProvidedImage(image, offsets);
		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading image " + id
			        + ". Pronblem was: " + t.getMessage());
			return null;
		}
	}

	private int[] getOffsets(String id) {
		try {
			File offset = new File(rawDirectory, id + ".png.offset");
			int[] offsets = new int[2];
			Scanner in = new Scanner(offset);
			offsets[0] = in.nextInt();
			in.skip("\\s+");
			offsets[1] = in.nextInt();
			return offsets;

		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading offsets for " + id
			        + ", assuming (0,0). Pronblem was: " + t.getMessage());
			return new int[] {
			        0, 0
			};
		}
	}
}
