package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.setup.ModDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DelayedDamage {
    private static final List<Entry> PENDING = new ArrayList<>();

    private record Entry(LivingEntity target, ServerPlayer source, float damage, Consumer<LivingEntity> afterHit) {
    }

    public static void arcane(LivingEntity target, ServerPlayer source, float damage, Consumer<LivingEntity> afterHit) {
        PENDING.add(new Entry(target, source, damage, afterHit));
    }

    public static void flush() {
        if (PENDING.isEmpty()) {
            return;
        }
        List<Entry> entries = new ArrayList<>(PENDING);
        PENDING.clear();
        for (Entry entry : entries) {
            LivingEntity target = entry.target();
            if (!target.isAlive() || !(target.level() instanceof ServerLevel level)) {
                continue;
            }
            target.hurtServer(level, ModDamageTypes.arcane(level, null, entry.source()), entry.damage());
            if (entry.afterHit() != null) {
                entry.afterHit().accept(target);
            }
        }
    }
}
