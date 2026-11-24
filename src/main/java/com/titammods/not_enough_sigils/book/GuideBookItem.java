package com.titammods.not_enough_sigils.book;

import com.klikli_dev.modonomicon.item.ModonomiconCustomItemBase;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.world.item.Item;

public class GuideBookItem extends ModonomiconCustomItemBase {
    public GuideBookItem() {
        super(NotEnoughSigils.rl("guide"), new Item.Properties().stacksTo(1));
    }
}
