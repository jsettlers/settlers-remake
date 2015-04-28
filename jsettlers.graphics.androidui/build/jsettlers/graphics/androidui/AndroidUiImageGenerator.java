/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.androidui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.DirectImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.resources.SwingResourceProvider;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;

/**
 * This is an ant task to copy over the android images.
 * 
 * @author michael
 */
public class AndroidUiImageGenerator {

	private File destinationDirectory = null;
	private int resolution = 0;
	private File listOutputDirectory = null;
	private String listOutputClass = null;
	public File configFile = null;

	public void setDestinationDirectory(File sourceDirectory) {
		this.destinationDirectory = sourceDirectory;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public void setListOutputDirectory(File listOutputDirectory) {
		this.listOutputDirectory = listOutputDirectory;
	}

	public void setListOutputClass(String listOutputClass) {
		this.listOutputClass = listOutputClass;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void execute() throws IOException {
		if (destinationDirectory == null) {
			throw new RuntimeException("please use destinationDirectory=\"...\"");
		}
		if (configFile == null) {
			throw new RuntimeException("please use configFile=\"...\"");
		}

		System.out.println("ConfigFile: "+configFile);
		SwingResourceLoader.setupResourcesByConfigFile(new ConfigurationPropertiesFile(configFile));

		String listOutputPackage = listOutputClass.replaceAll("\\.\\w*$", "");
		File listOutputDir =
				new File(listOutputDirectory.getAbsolutePath() + "/"
						+ listOutputPackage.replaceAll("\\.", "/"));
		listOutputDir.mkdirs();
		PrintWriter listOutput =
				new PrintWriter(listOutputDirectory.getAbsolutePath() + "/"
						+ listOutputClass.replaceAll("\\.", "/") + ".java");

		listOutput.println("package " + listOutputPackage + ";");

		listOutput.println("import jsettlers.graphics.androidui.R;");

		listOutput.println("public class "
				+ listOutputClass.replaceAll("(.*\\.)*", "") + " {");
		exportBuildingImages(listOutput);
		exportMaterialImages(listOutput);
		listOutput.println("}");

		listOutput.close();
	}

	private void exportBuildingImages(PrintWriter listOutput) {
		listOutput
				.println("\tpublic static final int[] BUILDING_IMAGE_MAP = new int[] {");

		ImageProvider i = ImageProvider.getInstance();

		for (EBuildingType t : EBuildingType.values) {
			String name = "bui_" + t.toString().toLowerCase();
			File file = new File(destinationDirectory, name + ".png");

			Image guiImage = i.getImage(t.getGuiImage());
			if (guiImage instanceof SingleImage) {
				export((SingleImage) guiImage, file);
				listOutput.println("\t\tR.drawable." + name + ",");
			} else {
				listOutput.println("\t\t-1,");
			}
		}

		listOutput.println("\t};");
	}

	private void exportMaterialImages(PrintWriter listOutput) {
		listOutput
				.println("\tpublic static final int[] MATERIAL_IMAGE_MAP = new int[] {");

		ImageProvider i = ImageProvider.getInstance();

		for (EMaterialType t : EMaterialType.values()) {
			String name = "mat_" + t.toString().toLowerCase();
			File file = new File(destinationDirectory, name + ".png");

			Image guiImage = i.getGuiImage(t.getGuiFile(), t.getGuiIconBase() + resolution);
			if (guiImage instanceof SingleImage) {
				export((SingleImage) guiImage, file);
				listOutput.println("\t\tR.drawable." + name + ",");
			} else {
				listOutput.println("\t\t-1,");
			}
		}

		listOutput.println("\t};");
	}

	private static void export(SingleImage image, File file) {
		// does not work if gpu does not support non-power-of-two
		int width = image.getWidth();
		int height = image.getHeight();
		if (width <= 0 || height <= 0) {
			return;
		}

		BufferedImage rendered =
				new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		ShortBuffer data = image.getData().duplicate();
		data.rewind();
		int[] rgbArray = new int[data.remaining()];
		for (int i = 0; i < rgbArray.length; i++) {
			short myColor = data.get();
			float red = (float) ((myColor >> 11) & 0x1f) / 0x1f;
			float green = (float) ((myColor >> 6) & 0x1f) / 0x1f;
			float blue = (float) ((myColor >> 1) & 0x1f) / 0x1f;
			float alpha = myColor & 0x1;
			rgbArray[i] = Color.getARGB(red, green, blue, alpha);
		}

		rendered.setRGB(0, 0, width, height, rgbArray, 0, width);

		try {
			ImageIO.write(rendered, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
