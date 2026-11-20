package com.titammods.not_enough_sigils.sigil;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import net.minecraft.world.entity.player.Player;

public class SpiritusAffinity {

    public static SpiritusType of(Player player) {
        SpiritusType type = NeoVitaeAPI.getInstance().getPlayerSpiritusHandler().getLargestSpiritusType(player);
        if (type == null) {
            return SpiritusType.RAW;
        }
        if (NeoVitaeAPI.getInstance().getPlayerSpiritusHandler().getTotalSpiritus(type, player) <= 0.0D) {
            return SpiritusType.RAW;
        }
        return type;
    }
}
