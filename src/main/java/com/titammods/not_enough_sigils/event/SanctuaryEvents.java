package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.effect.SanctuarySigilEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import java.util.Set;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class SanctuaryEvents {
    private static final Set<MobSpawnType> NATURAL = Set.of(MobSpawnType.NATURAL, MobSpawnType.CHUNK_GENERATION,
            MobSpawnType.PATROL, MobSpawnType.EVENT, MobSpawnType.REINFORCEMENT);
    private static final Set<MobSpawnType> SPAWNERS = Set.of(MobSpawnType.SPAWNER, MobSpawnType.TRIAL_SPAWNER);

    @SubscribeEvent
    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity().getType().getCategory() != MobCategory.MONSTER) {
            return;
        }
        MobSpawnType type = event.getSpawnType();
        boolean spawner = SPAWNERS.contains(type);
        if (!spawner && !NATURAL.contains(type)) {
            return;
        }
        ServerLevel level = event.getLevel().getLevel();
        for (ServerPlayer player : level.players()) {
            SanctuarySigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.SANCTUARY, SanctuarySigilEffect.class);
            if (config == null || (spawner && !config.blockSpawners())) {
                continue;
            }
            if (player.distanceToSqr(event.getX(), event.getY(), event.getZ()) <= config.radius() * config.radius()) {
                event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                return;
            }
        }
    }
}
