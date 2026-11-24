package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.animusnv.util.AnimusRitualHelper;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.util.ExperienceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record AlchemistSigilEffect(int xpPerUse, int evPerXp, int evCostPerXp, boolean requireOrb, int cooldown) implements ISigilEffect {
    public static final int DEFAULT_XP_PER_USE = 100;
    public static final int DEFAULT_EV_PER_XP = 10;
    public static final int DEFAULT_EV_COST_PER_XP = 20;
    public static final boolean DEFAULT_REQUIRE_ORB = true;
    public static final int DEFAULT_COOLDOWN = 4;

    public static final AlchemistSigilEffect DEFAULT = new AlchemistSigilEffect(DEFAULT_XP_PER_USE, DEFAULT_EV_PER_XP,
            DEFAULT_EV_COST_PER_XP, DEFAULT_REQUIRE_ORB, DEFAULT_COOLDOWN);

    public static final MapCodec<AlchemistSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("xp_per_use", DEFAULT_XP_PER_USE).forGetter(AlchemistSigilEffect::xpPerUse),
            Codec.INT.optionalFieldOf("ev_per_xp", DEFAULT_EV_PER_XP).forGetter(AlchemistSigilEffect::evPerXp),
            Codec.INT.optionalFieldOf("ev_cost_per_xp", DEFAULT_EV_COST_PER_XP).forGetter(AlchemistSigilEffect::evCostPerXp),
            Codec.BOOL.optionalFieldOf("require_orb", DEFAULT_REQUIRE_ORB).forGetter(AlchemistSigilEffect::requireOrb),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(AlchemistSigilEffect::cooldown)
    ).apply(instance, AlchemistSigilEffect::new));

    private static final String TAG = "AlchemistToExperience";
    private static final String PREFIX = "chat.not_enough_sigils.alchemist.";

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        return use(level, player, stack);
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return use(level, player, stack);
    }

    public static boolean isToExperience(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(TAG);
    }

    private static void setToExperience(ItemStack stack, boolean value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(TAG, value));
    }

    private boolean use(Level level, Player player, ItemStack stack) {
        if (player == null) {
            return false;
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                boolean toExperience = !isToExperience(stack);
                setToExperience(stack, toExperience);
                player.displayClientMessage(Component.translatable(PREFIX + (toExperience ? "mode_experience" : "mode_essence"))
                        .withStyle(toExperience ? ChatFormatting.GREEN : ChatFormatting.DARK_RED), true);
            }
            return true;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        IAnima network = AnimusRitualHelper.getNetworkForBoundItem(player, stack);
        if (network == null) {
            return false;
        }
        boolean converted = isToExperience(stack) ? toExperience(player, network) : toEssence(player, network);
        if (converted && cooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
        return converted;
    }

    private boolean toEssence(Player player, IAnima network) {
        int capacity = requireOrb ? orbCapacity(player) : Integer.MAX_VALUE;
        if (capacity <= 0) {
            fail(player, "no_orb");
            return false;
        }
        long room = (long) capacity - network.getCurrentEV();
        if (room < evPerXp || evPerXp <= 0) {
            fail(player, "full");
            return false;
        }
        long available = ExperienceHelper.getTotalPoints(player);
        int points = (int) Math.min(Math.min(xpPerUse, available), room / evPerXp);
        if (points <= 0) {
            fail(player, "no_experience");
            return false;
        }
        player.giveExperiencePoints(-points);
        int added = network.add(AnimaTicket.create(points * evPerXp), capacity);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.5F, 1.2F);
        player.displayClientMessage(Component.translatable(PREFIX + "to_essence", points, added).withStyle(ChatFormatting.DARK_RED), true);
        return true;
    }

    private boolean toExperience(Player player, IAnima network) {
        if (evCostPerXp <= 0) {
            return false;
        }
        int points = Math.min(xpPerUse, network.getCurrentEV() / evCostPerXp);
        if (points <= 0) {
            player.displayClientMessage(Component.translatable(PREFIX + "no_ev", evCostPerXp).withStyle(ChatFormatting.RED), true);
            return false;
        }
        int cost = points * evCostPerXp;
        network.syphon(AnimaTicket.create(cost));
        player.giveExperiencePoints(points);
        player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 0.9F);
        player.displayClientMessage(Component.translatable(PREFIX + "to_experience", cost, points).withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    private static int orbCapacity(Player player) {
        int best = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item.isEmpty()) {
                continue;
            }
            BloodOrb orb = item.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
            if (orb != null) {
                best = Math.max(best, orb.animaCapacity());
            }
        }
        return best;
    }

    private static void fail(Player player, String key) {
        player.displayClientMessage(Component.translatable(PREFIX + key).withStyle(ChatFormatting.RED), true);
    }
}
