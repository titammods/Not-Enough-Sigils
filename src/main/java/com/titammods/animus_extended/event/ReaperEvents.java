package com.titammods.animus_extended.event;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.spiritus.IPlayerSpiritusHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class ReaperEvents {
    private static final double SPIRITUS_PER_HEALTH = 0.075D;
    private static final double MAX_SPIRITUS_PER_KILL = 96.0D;
    private static final float BASE_XP_MULTIPLIER = 1.5F;
    private static final float XP_PER_LOOTING = 0.25F;

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player || target instanceof Player) {
            return;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.REAPER)) {
            return;
        }

        int looting = getLooting(player);
        double amount = Math.min(MAX_SPIRITUS_PER_KILL, target.getMaxHealth() * SPIRITUS_PER_HEALTH * (1 + looting));
        if (amount <= 0.0D) {
            return;
        }
        IPlayerSpiritusHandler handler = NeoVitaeAPI.getInstance().getPlayerSpiritusHandler();
        SpiritusType type = handler.getLargestSpiritusType(player);
        handler.addSpiritus(type == null ? SpiritusType.RAW : type, player, amount);
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getAttackingPlayer() instanceof ServerPlayer player)) {
            return;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.REAPER)) {
            return;
        }
        float multiplier = BASE_XP_MULTIPLIER + XP_PER_LOOTING * getLooting(player);
        event.setDroppedExperience(Math.round(event.getDroppedExperience() * multiplier));
    }

    private static int getLooting(ServerPlayer player) {
        Holder<Enchantment> looting = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        return player.getWeaponItem().getEnchantmentLevel(looting);
    }
}
