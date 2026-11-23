package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.saereth.SaerethSigilItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class SaerethSigilMarkers {
    private static final int INTERVAL = 5;

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null || minecraft.isPaused() || player.tickCount % INTERVAL != 0) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SaerethSigilItem) || !SaerethSigilItem.isLinkedTo(stack, level)) {
            return;
        }
        BlockPos chest = SaerethSigilItem.chestOf(stack);
        BlockPos array = SaerethSigilItem.arrayOf(stack);
        if (chest != null) {
            level.addParticle(ParticleTypes.END_ROD, chest.getX() + 0.5D, chest.getY() + 1.1D, chest.getZ() + 0.5D, 0.0D, 0.02D, 0.0D);
        }
        if (array != null) {
            level.addParticle(ParticleTypes.WITCH, array.getX() + 0.5D, array.getY() + 0.1D, array.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }
}
