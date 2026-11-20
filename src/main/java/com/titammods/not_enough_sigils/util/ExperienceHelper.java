package com.titammods.not_enough_sigils.util;

import net.minecraft.world.entity.player.Player;

public class ExperienceHelper {

    private ExperienceHelper() {
    }

    public static long getTotalPoints(Player player) {
        long base = getPointsForLevel(player.experienceLevel);
        long partial = Math.round((double) player.experienceProgress * player.getXpNeededForNextLevel());
        return base + partial;
    }

    public static long getPointsForLevel(int level) {
        long l = level;
        if (level <= 16) {
            return l * l + 6 * l;
        }
        if (level <= 31) {
            return Math.round(2.5D * l * l - 40.5D * l + 360.0D);
        }
        return Math.round(4.5D * l * l - 162.5D * l + 2220.0D);
    }
}
