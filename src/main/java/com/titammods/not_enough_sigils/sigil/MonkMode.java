package com.titammods.not_enough_sigils.sigil;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public enum MonkMode {
    BOTH("both", ChatFormatting.LIGHT_PURPLE, true, true),
    MINING("mining", ChatFormatting.AQUA, true, false),
    COMBAT("combat", ChatFormatting.RED, false, true);

    private static final String TAG = "SpiritualMonkMode";

    private final String key;
    private final ChatFormatting color;
    private final boolean mining;
    private final boolean combat;

    MonkMode(String key, ChatFormatting color, boolean mining, boolean combat) {
        this.key = key;
        this.color = color;
        this.mining = mining;
        this.combat = combat;
    }

    public boolean mining() {
        return mining;
    }

    public boolean combat() {
        return combat;
    }

    public Component displayName() {
        return Component.translatable("monk_mode.not_enough_sigils." + key).withStyle(color);
    }

    public MonkMode next() {
        MonkMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static MonkMode get(ItemStack stack) {
        String name = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(TAG);
        for (MonkMode mode : values()) {
            if (mode.key.equals(name)) {
                return mode;
            }
        }
        return BOTH;
    }

    public static void set(ItemStack stack, MonkMode mode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(TAG, mode.key));
    }

    public static boolean isSpiritualMonk(ItemStack stack) {
        return stack != null && stack.is(ModItems.SIGIL_SPIRITUAL_MONK.get());
    }

    public static MonkMode of(ItemStack activeSigil) {
        return isSpiritualMonk(activeSigil) ? get(activeSigil) : BOTH;
    }

    public static boolean allowsMining(Player player) {
        return of(MonkSigilEventHandler.findActiveMonkSigil(player)).mining();
    }

    public static boolean allowsCombat(Player player) {
        return of(MonkSigilEventHandler.findActiveMonkSigil(player)).combat();
    }
}
