/*
 * Copyright (c) 2015 - 2018
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
 */
package jsettlers.textures.generation;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

/**
 * This program compiles all textures into the needed format for graphics
 *
 * @author michael
 */
public class TextureGenerationTask extends DefaultTask {
	private static final String PACKAGE_NAME = "jsettlers/common/images/";

	@InputDirectory
	public File resourceDirectory;

	@OutputDirectory
	public File generatedSourcesDirectory;

	@OutputDirectory
	public File generatedResourcesDirectory;

	@TaskAction
	public void compileTextures() throws IOException {
		if (generatedSourcesDirectory == null) {
			throw new RuntimeException("please use generationDirectory=\"...\"");
		}
		if (resourceDirectory == null) {
			throw new RuntimeException("please use resourceDirectory=\"...\"");
		}

		File outputResourcesDirectory = new File(this.generatedResourcesDirectory, PACKAGE_NAME);
		File outputSourcesDirectory = new File(this.generatedSourcesDirectory, PACKAGE_NAME);

		TextureIndex textureIndex = new TextureIndex(outputResourcesDirectory, outputSourcesDirectory);
		textureIndex.openTextureIndex();

		TextureGenerator generator = new TextureGenerator(textureIndex, outputResourcesDirectory);
		generator.processTextures(resourceDirectory);

		textureIndex.closeTextureIndex();
	}
}
