package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.sigil.effect.SkywardSoulSigilEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SkywardSoulState {
    public static final String FLIGHT_TAG = "not_enough_sigils_skyward_flight";
    public static final String FALL_SAFE_TAG = "not_enough_sigils_skyward_fall_safe";

    public static void grantFlight(Player player, ItemStack stack, SkywardSoulSigilEffect config) {
        if (!(player instanceof ServerPlayer serverPlayer) || isExempt(serverPlayer)) {
            return;
        }
        CompoundTag data = serverPlayer.getPersistentData();
        data.putBoolean(FLIGHT_TAG, true);
        data.remove(FALL_SAFE_TAG);
        serverPlayer.fallDistance = 0.0F;

        if (!serverPlayer.getAbilities().mayfly) {
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();
        }

        if (serverPlayer.getAbilities().flying && serverPlayer.tickCount % 20 == 0
                && !SigilTracker.drainEV(serverPlayer, stack, config.flightEvPerSecond())) {
            SigilTracker.deactivate(stack);
            revokeFlight(serverPlayer);
        }
    }

    public static void revokeFlight(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(FLIGHT_TAG)) {
            return;
        }
        data.remove(FLIGHT_TAG);
        if (isExempt(player)) {
            return;
        }
        boolean wasFlying = player.getAbilities().flying;
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
        if (wasFlying || !player.onGround()) {
            data.putBoolean(FALL_SAFE_TAG, true);
        }
    }

    public static boolean isExempt(ServerPlayer player) {
        return player.isCreative() || player.isSpectator();
    }
}
