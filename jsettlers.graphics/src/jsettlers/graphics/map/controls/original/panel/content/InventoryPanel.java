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
package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.text.EFontSize;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.utils.UIPanel;

public class InventoryPanel extends AbstractContentProvider
{
    private static class InventoryItem extends UIPanel
    {
        private static final float quantityTextTop = (fontSize + tileSpacingV) / (iconSize_px + fontSize + tileSpacingV);
        private UIPanel goodsIcon;
        private Label lblQuantity;

        private InventoryItem(ImageLink imageLink)
        {
            goodsIcon = new UIPanel();
            goodsIcon.setBackground(imageLink);
            addChild(goodsIcon, 0f, quantityTextTop, 1f, 1f);

            lblQuantity = new Label(Labels.getString("0"), EFontSize.SMALL);
            addChild(lblQuantity, 0f, 0f, 1f, quantityTextTop);
        }
    }

    private final InventoryItem[] inventoryItems = {
        new InventoryItem(EMaterialType.PLANK.getImageLink()),
        new InventoryItem(EMaterialType.STONE.getImageLink()),
        new InventoryItem(EMaterialType.TRUNK.getImageLink()),
        new InventoryItem(EMaterialType.COAL.getImageLink()),
        new InventoryItem(EMaterialType.IRONORE.getImageLink()),
        new InventoryItem(EMaterialType.GOLDORE.getImageLink()),
        new InventoryItem(EMaterialType.IRON.getImageLink()),
        new InventoryItem(EMaterialType.HAMMER.getImageLink()),
        new InventoryItem(EMaterialType.BLADE.getImageLink()),
        new InventoryItem(EMaterialType.AXE.getImageLink()),
        new InventoryItem(EMaterialType.SAW.getImageLink()),
        new InventoryItem(EMaterialType.PICK.getImageLink()),
        new InventoryItem(EMaterialType.FISHINGROD.getImageLink()),
        new InventoryItem(EMaterialType.SCYTHE.getImageLink()),
        new InventoryItem(EMaterialType.SWORD.getImageLink()),
        new InventoryItem(EMaterialType.BOW.getImageLink()),
        new InventoryItem(EMaterialType.SPEAR.getImageLink()),
        new InventoryItem(EMaterialType.MEAD.getImageLink()),
        new InventoryItem(EMaterialType.FISH.getImageLink()),
        new InventoryItem(EMaterialType.PIG.getImageLink()),
        new InventoryItem(EMaterialType.MEAT.getImageLink()),
        new InventoryItem(EMaterialType.CROP.getImageLink()),
        new InventoryItem(EMaterialType.FLOUR.getImageLink()),
        new InventoryItem(EMaterialType.BREAD.getImageLink()),
        new InventoryItem(EMaterialType.WATER.getImageLink()),
        new InventoryItem(EMaterialType.WINE.getImageLink()),
        new InventoryItem(EMaterialType.HONEY.getImageLink()),
        new InventoryItem(EMaterialType.GOLD.getImageLink()),
    };

    private static final float contentHeight_px = 216; // 360
    private static final float contentWidth_px = 118; // 197

    private static final float titleTop_px = 2;
    private static final float titleTextHeight_px = 12;
    private static final float titleTop = 1 - (titleTop_px / contentHeight_px);
    private static final float titleTextHeight = titleTextHeight_px / contentHeight_px;

    private static final float iconSize_px = 18; // 30
    private static final float fontSize = 7f;
    private static final float tileSpacingV = 2f;
    private static final float tileHeight = (iconSize_px + fontSize + tileSpacingV) / contentHeight_px;
    private static final float tileWidth = iconSize_px / contentWidth_px;
    private static final float marginTop = 1 - (29 / contentHeight_px); // 56
    private static final float marginLeft = 5 / contentWidth_px; // 9 <-> 8
    private static final int COLUMNS = 6;
    private static final int ROWS = 6;

    private UIPanel panel;

    public InventoryPanel()
    {
        panel = new UIPanel();

        panel.addChild(new Label(Labels.getString("controlpanel_title_inventory"), EFontSize.NORMAL), 0f, titleTop - titleTextHeight, 1f, titleTop);

        int itemIdx = 0;
        float top = marginTop;
        for (int r = 0; r < ROWS && itemIdx < inventoryItems.length; r++, top -= tileHeight) {
            float left = marginLeft;
            for (int c = 0; c < COLUMNS; c++, left += tileWidth) {

                if (r == 4 && 1 < c && c < 5) {
                    continue; // Position is kept blank.
                }

                panel.addChild(
                        inventoryItems[itemIdx],
                        left,
                        top - tileHeight,
                        left + tileWidth,
                        top
                        );
                if (++itemIdx == inventoryItems.length) {
                    break;
                }
            }
        }
    }

    @Override
    public ESecondaryTabType getTabs()
    {
        return ESecondaryTabType.GOODS;
    }

    @Override
    public UIPanel getPanel()
    {
        return panel;
    }
}