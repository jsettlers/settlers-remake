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

    private static final SettlerImageFlavor TEST_IMAGE_FLAVOR = new SettlerImageFlavor(
            EMovableType.BEARER,
            EMovableAction.WALKING,
            EMaterialType.NO_MATERIAL,
            EDirection.SOUTH_WEST);
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
    public void givenTextWithComment_whenLoad_thenContainsNoEntry() throws IOException {
        String text = "# some comment";
        settlerImages.loadFromMovablesText(text);
        assertTrue(settlerImages.map.isEmpty());
        //assertSame(dummyImage, settlerImages.getImageForSettler(SettlerImageFlavor.NONE, 1.0f));
    }

    @Test
    public void givenTextWithDefaultEntry_whenLoad_thenContainsDefaultEntry() throws IOException {
        String text = "*,*,*,*=10, 0, 0, 1";
        settlerImages.loadFromMovablesText(text);
        assertEquals(1, settlerImages.map.size());
        assertThat(settlerImages.map, hasEntry(SettlerImageFlavor.NONE, SettlerImageMap.DEFAULT_ITEM));
    }

    @Test
    public void givenTextWithTestEntry_whenLoad_thenContainsTestEntry() throws IOException {
        String text = "BEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12";
        settlerImages.loadFromMovablesText(text);
        assertOneEntryInMap();
    }

    private void assertOneEntryInMap() {
        assertEquals(1, settlerImages.map.size());
        assertThat(settlerImages.map, hasEntry(TEST_IMAGE_FLAVOR,
                new SettlerImageMapItem(10, 0, 0, 12))
        );
    }

    @Test
    public void givenTextWithDuplicateEntry_whenLoad_thenContainsOnlyOneEntry() throws IOException {
        String text = "BEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12\nBEARER, WALKING, NO_MATERIAL, SOUTH_WEST = 10, 0,  0, 12";
        settlerImages.loadFromMovablesText(text);
        assertOneEntryInMap();
    }
}