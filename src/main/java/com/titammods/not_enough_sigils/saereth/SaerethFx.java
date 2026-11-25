package com.titammods.not_enough_sigils.saereth;

import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SaerethFx {
    public static final int BLOOD = 0xAA0000;
    public static final int DEEP = 0x550011;
    public static final int ARCANE = 0x7A1FA2;
    public static final int CHEST_MARK = 0xCC2233;
    public static final int ARRAY_MARK = 0x8833CC;
    private static final double RANGE = 64.0D;

    private static ColoredParticleOptions flame(int color) {
        return new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color);
    }

    private static ColoredParticleOptions glow(int color) {
        return new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color);
    }

    private static ColoredParticleOptions drip(int color) {
        return new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), color);
    }

    private static ColoredParticleOptions rune(int color) {
        return new ColoredParticleOptions(NVParticles.RUNE_GLOW.get(), color);
    }

    public static Vec3 hands(Entity entity) {
        return entity.position().add(entity.getLookAngle().scale(0.35D)).add(0.0D, entity.getBbHeight() * 0.62D, 0.0D);
    }

    public static void drawing(ServerLevel level, Entity saereth, BlockPos array, int tick) {
        Vec3 center = Vec3.atBottomCenterOf(array);
        double angle = tick * 0.42D;
        for (int i = 0; i < 2; i++) {
            double a = angle + Math.PI * i;
            level.sendParticles(rune(BLOOD), center.x + Math.cos(a) * 0.85D, center.y + 0.04D, center.z + Math.sin(a) * 0.85D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.sendParticles(flame(DEEP), center.x, center.y + 0.05D, center.z, 2, 0.25D, 0.0D, 0.25D, 0.005D);
        Vec3 hand = hands(saereth);
        level.sendParticles(drip(BLOOD), hand.x, hand.y, hand.z, 1, 0.05D, 0.0D, 0.05D, 0.0D);
    }

    public static void channel(ServerLevel level, Entity saereth, BlockPos array) {
        Vec3 hand = hands(saereth);
        StreamEffect.builder(hand.x, hand.y, hand.z)
                .to(array)
                .color(BLOOD)
                .endColor(ARCANE)
                .scale(0.07F)
                .speed(1.6F)
                .wobble(0.035F)
                .wobbleFrequency(1.1F)
                .spiralInto(true)
                .spiralRadius(0.45F)
                .spiralSpeed(0.4F)
                .approachHeight(0.9F)
                .alphaStart(0.15F)
                .alphaEnd(0.95F)
                .glow(true)
                .build()
                .sendToNearby(level, array, RANGE);
        level.sendParticles(glow(BLOOD), hand.x, hand.y, hand.z, 2, 0.12D, 0.12D, 0.12D, 0.0D);
    }

    public static void complete(ServerLevel level, BlockPos array) {
        Vec3 center = Vec3.atCenterOf(array);
        level.sendParticles(glow(BLOOD), center.x, center.y, center.z, 14, 0.35D, 0.3D, 0.35D, 0.02D);
        level.sendParticles(flame(ARCANE), center.x, center.y - 0.3D, center.z, 10, 0.4D, 0.05D, 0.4D, 0.02D);
    }

    public static void deliver(ServerLevel level, BlockPos from, BlockPos chest) {
        StreamPresets.lifePulse(from, chest).build().sendToNearby(level, chest, RANGE);
    }

    public static void summon(ServerLevel level, BlockPos stone, BlockPos spawn) {
        StreamPresets.bloodTendril(stone, spawn).build().sendToNearby(level, spawn, RANGE);
        Vec3 base = Vec3.atBottomCenterOf(spawn);
        for (int i = 0; i < 12; i++) {
            double y = base.y + i * 0.18D;
            double a = i * 0.9D;
            level.sendParticles(flame(BLOOD), base.x + Math.cos(a) * 0.55D, y, base.z + Math.sin(a) * 0.55D, 1, 0.0D, 0.02D, 0.0D, 0.0D);
        }
        level.sendParticles(glow(BLOOD), base.x, base.y + 1.0D, base.z, 30, 0.5D, 0.9D, 0.5D, 0.03D);
        level.sendParticles(rune(ARCANE), base.x, base.y + 0.05D, base.z, 16, 0.9D, 0.0D, 0.9D, 0.0D);
    }

    public static void capture(ServerLevel level, Entity saereth, Entity player) {
        StreamEffect.builder(saereth)
                .to(player.getX(), player.getY() + player.getBbHeight() * 0.6D, player.getZ())
                .color(BLOOD)
                .endColor(ARCANE)
                .scale(0.12F)
                .speed(2.2F)
                .wobble(0.04F)
                .spiralInto(true)
                .spiralRadius(0.3F)
                .approachHeight(0.6F)
                .alphaStart(0.2F)
                .alphaEnd(0.9F)
                .glow(true)
                .build()
                .sendToNearby(level, saereth.blockPosition(), RANGE);
        level.sendParticles(glow(ARCANE), saereth.getX(), saereth.getY() + 1.0D, saereth.getZ(), 24, 0.3D, 0.8D, 0.3D, 0.02D);
        level.sendParticles(flame(DEEP), saereth.getX(), saereth.getY() + 0.1D, saereth.getZ(), 12, 0.4D, 0.0D, 0.4D, 0.01D);
    }

    public static void release(ServerLevel level, Entity player, Entity saereth) {
        StreamEffect.builder(player)
                .to(saereth.blockPosition())
                .color(ARCANE)
                .endColor(BLOOD)
                .scale(0.1F)
                .speed(2.0F)
                .wobble(0.03F)
                .spiralInto(true)
                .spiralRadius(0.5F)
                .approachHeight(1.2F)
                .alphaStart(0.15F)
                .alphaEnd(0.95F)
                .glow(true)
                .build()
                .sendToNearby(level, saereth.blockPosition(), RANGE);
        level.sendParticles(rune(BLOOD), saereth.getX(), saereth.getY() + 0.05D, saereth.getZ(), 14, 0.7D, 0.0D, 0.7D, 0.0D);
        level.sendParticles(glow(BLOOD), saereth.getX(), saereth.getY() + 1.0D, saereth.getZ(), 18, 0.3D, 0.8D, 0.3D, 0.02D);
    }

    public static void link(ServerLevel level, Entity saereth, BlockPos chest, BlockPos array) {
        StreamPresets.soulSiphon(saereth, chest).color(CHEST_MARK).build().sendToNearby(level, chest, RANGE);
        StreamPresets.soulSiphon(saereth, array).color(ARRAY_MARK).build().sendToNearby(level, array, RANGE);
    }

    public static void idle(ServerLevel level, Entity saereth, int tick) {
        Vec3 hand = hands(saereth);
        double a = tick * 0.15D;
        level.sendParticles(rune(ARCANE), hand.x + Math.cos(a) * 0.3D, hand.y + 0.15D, hand.z + Math.sin(a) * 0.3D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static final int ENCHANT = 0x9C4DFF;

    public static void librarySummon(ServerLevel level, BlockPos chest, Vec3 center) {
        StreamEffect.builder(chest)
                .to(center.x, center.y, center.z)
                .color(ENCHANT)
                .endColor(BLOOD)
                .scale(0.09F)
                .speed(1.8F)
                .wobble(0.04F)
                .wobbleFrequency(1.3F)
                .spiralInto(true)
                .spiralRadius(0.4F)
                .spiralSpeed(0.45F)
                .approachHeight(0.6F)
                .alphaStart(0.15F)
                .alphaEnd(0.9F)
                .glow(true)
                .build()
                .sendToNearby(level, chest, RANGE);
        Vec3 top = Vec3.atCenterOf(chest).add(0.0D, 0.6D, 0.0D);
        level.sendParticles(glow(ENCHANT), top.x, top.y, top.z, 10, 0.25D, 0.2D, 0.25D, 0.02D);
    }

    public static void libraryTrail(ServerLevel level, Vec3 pos, int color) {
        level.sendParticles(rune(color), pos.x, pos.y, pos.z, 1, 0.04D, 0.04D, 0.04D, 0.0D);
    }

    public static void libraryOrbit(ServerLevel level, Entity saereth, Vec3 center, int tick) {
        if (tick % 3 == 0) {
            level.sendParticles(glow(BLOOD), center.x, center.y, center.z, 1, 0.15D, 0.15D, 0.15D, 0.0D);
        }
        double ring = tick * 0.21D;
        for (int i = 0; i < 3; i++) {
            double a = ring + i * (Math.PI * 2.0D / 3.0D);
            level.sendParticles(flame(i == 0 ? BLOOD : DEEP), center.x + Math.cos(a) * 1.15D, center.y - 0.85D, center.z + Math.sin(a) * 1.15D, 1, 0.0D, 0.01D, 0.0D, 0.0D);
        }
        if (tick % 12 == 0) {
            Vec3 hand = hands(saereth);
            StreamEffect.builder(hand.x, hand.y, hand.z)
                    .to(center.x, center.y, center.z)
                    .color(BLOOD)
                    .endColor(ENCHANT)
                    .scale(0.05F)
                    .speed(2.4F)
                    .wobble(0.02F)
                    .spiralInto(false)
                    .alphaStart(0.3F)
                    .alphaEnd(0.9F)
                    .glow(true)
                    .build()
                    .sendToNearby(level, saereth.blockPosition(), RANGE);
        }
    }

    public static void libraryConverge(ServerLevel level, Vec3 center, double progress, int tick) {
        double radius = 1.3D * (1.0D - progress);
        for (int i = 0; i < 4; i++) {
            double a = tick * 0.5D + i * (Math.PI / 2.0D);
            double y = Math.sin(a * 0.7D) * 0.5D * (1.0D - progress);
            level.sendParticles(rune(i % 2 == 0 ? ENCHANT : BLOOD), center.x + Math.cos(a) * radius, center.y + y, center.z + Math.sin(a) * radius, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.sendParticles(flame(ARCANE), center.x, center.y, center.z, 2, 0.08D, 0.08D, 0.08D, 0.01D);
    }

    public static void libraryBind(ServerLevel level, Vec3 center) {
        StreamEffect.builder(center.x, center.y, center.z)
                .color(ENCHANT)
                .scale(0.38F)
                .stationary()
                .lifetime(24)
                .glow(true)
                .build()
                .sendToNearby(level, BlockPos.containing(center), RANGE);
        level.sendParticles(glow(BLOOD), center.x, center.y, center.z, 30, 0.35D, 0.35D, 0.35D, 0.06D);
        level.sendParticles(flame(ENCHANT), center.x, center.y, center.z, 16, 0.2D, 0.2D, 0.2D, 0.08D);
        for (int i = 0; i < 24; i++) {
            double a = i * (Math.PI * 2.0D / 24.0D);
            level.sendParticles(rune(i % 2 == 0 ? ENCHANT : BLOOD), center.x + Math.cos(a) * 1.4D, center.y, center.z + Math.sin(a) * 1.4D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            level.sendParticles(rune(ARCANE), center.x + Math.cos(a) * 0.9D, center.y + Math.sin(a) * 0.9D, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void libraryHover(ServerLevel level, Vec3 pos, int tick) {
        for (int i = 0; i < 2; i++) {
            double a = tick * 0.35D + Math.PI * i;
            level.sendParticles(rune(ENCHANT), pos.x + Math.cos(a) * 0.45D, pos.y + Math.sin(tick * 0.2D) * 0.1D, pos.z + Math.sin(a) * 0.45D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        if (tick % 4 == 0) {
            level.sendParticles(glow(ENCHANT), pos.x, pos.y, pos.z, 1, 0.1D, 0.1D, 0.1D, 0.0D);
        }
    }

    public static void libraryDeliver(ServerLevel level, Vec3 from, BlockPos chest) {
        StreamPresets.lifePulse(BlockPos.containing(from), chest).color(ENCHANT).build().sendToNearby(level, chest, RANGE);
    }

    public static void libraryDeposit(ServerLevel level, BlockPos chest) {
        Vec3 top = Vec3.atCenterOf(chest).add(0.0D, 0.6D, 0.0D);
        level.sendParticles(glow(ENCHANT), top.x, top.y, top.z, 18, 0.3D, 0.2D, 0.3D, 0.03D);
        level.sendParticles(rune(BLOOD), top.x, top.y - 0.1D, top.z, 12, 0.45D, 0.0D, 0.45D, 0.0D);
    }

    public static void marker(Level level, BlockPos pos, int color, double height) {
        level.addParticle(rune(color), pos.getX() + 0.5D, pos.getY() + height, pos.getZ() + 0.5D, 0.0D, 0.015D, 0.0D);
    }
}
