/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.graphics.map.draw.settlerimages;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SettlerImageMapTest {

	private static final SettlerImageFlavor TEST_IMAGE_FLAVOR = new SettlerImageFlavor(EMovableType.BEARER, EMovableAction.WALKING, EMaterialType.NO_MATERIAL, EDirection.SOUTH_WEST);
	private SettlerImageMap settlerImages;
	private Image dummyImage;

	@Before
	public void setUp(){
		dummyImage = mock(Image.class);
		ImageProvider imageProvider = mock(ImageProvider.class);
		when(imageProvider.getImageSafe(any(SettlerImageMapItem.class), anyFloat())).thenReturn(dummyImage);

		settlerImages = new SettlerImageMap(imageProvider);
	}

	@Test
	public void givenTextWithCommentWhenLoadThenContainsNoEntry() throws IOException {
		String text = "# some comment";
		settlerImages.loadFromMovablesText(text);
		assertTrue(settlerImages.map.isEmpty());
	}

	@Test
	public void givenTextWithDefaultEntryWhenLoadThenContainsDefaultEntry() throws IOException {
		String text = "*,*,*,*=10, 0, 0, 1";
		settlerImages.loadFromMovablesText(text);
		assertEquals(1, settlerImages.map.size());
		assertThat(settlerImages.map, hasEntry(SettlerImageFlavor.NONE, SettlerImageMap.DEFAULT_ITEM));
	}

	@Test
	public void givenTextWithTestEntryWhenLoadthenContainsTestEntry() throws IOException {
		String text = "BEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12";
		settlerImages.loadFromMovablesText(text);
		assertOneEntryInMap();
	}

	private void assertOneEntryInMap() {
		assertEquals(1, settlerImages.map.size());
		assertThat(settlerImages.map, hasEntry(TEST_IMAGE_FLAVOR, new SettlerImageMapItem(10, 0, 0, 12)));
	}

	@Test
	public void givenTextWithDuplicateEntryWhenLoadThenContainsOnlyOneEntry() throws IOException {
		String text = "BEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12\nBEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12";
		settlerImages.loadFromMovablesText(text);
		assertOneEntryInMap();
	}
}