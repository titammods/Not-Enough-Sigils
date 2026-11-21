package com.titammods.not_enough_sigils.event;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.sigil.DelayedDamage;
import com.titammods.not_enough_sigils.sigil.MonkMode;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.SpiritusAffinity;
import com.titammods.not_enough_sigils.sigil.config.PunchProfile;
import com.titammods.not_enough_sigils.sigil.effect.SpiritualMonkSigilEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Optional;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class SpiritualMonkEvents {
    public static boolean hasSpiritualMonk(ServerPlayer player) {
        ItemStack active = MonkSigilEventHandler.findActiveMonkSigil(player);
        return active != null && active.is(ModItems.SIGIL_SPIRITUAL_MONK.get());
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!MonkMode.isSpiritualMonk(stack)) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.spiritual_monk.mode", MonkMode.get(stack).displayName()).withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.spiritual_monk.mode_hint").withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player)) {
            return;
        }
        if (!hasSpiritualMonk(player) || !MonkMode.allowsMining(player) || !event.getState().is(config(player).smeltable())) {
            return;
        }

        ServerLevel level = event.getLevel();
        float experience = 0.0F;
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            SingleRecipeInput input = new SingleRecipeInput(stack);
            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
            if (recipe.isEmpty()) {
                continue;
            }
            ItemStack result = recipe.get().value().assemble(input, level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }
            int count = Math.min(result.getMaxStackSize(), result.getCount() * stack.getCount());
            drop.setItem(result.copyWithCount(count));
            experience += recipe.get().value().getExperience() * stack.getCount();
        }

        if (experience > 0.0F) {
            int whole = Mth.floor(experience);
            if (level.getRandom().nextFloat() < experience - whole) {
                whole++;
            }
            event.setDroppedExperience(event.getDroppedExperience() + whole);
            level.sendParticles(ParticleTypes.FLAME, event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D, event.getPos().getZ() + 0.5D,
                    6, 0.25D, 0.25D, 0.25D, 0.01D);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPunch(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getSource().getDirectEntity() != player) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player || !player.getMainHandItem().isEmpty() || !hasSpiritualMonk(player) || !MonkMode.allowsCombat(player)) {
            return;
        }

        PunchProfile punch = config(player).punch(SpiritusAffinity.of(player));
        if (punch.damageMultiplier() != 1.0F) {
            event.setAmount(event.getAmount() * punch.damageMultiplier());
        }
        punch.effect().ifPresent(spec -> spec.apply(target, player));
        if (punch.freezeTicks() > 0 && target.canFreeze()) {
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), target.getTicksRequiredToFreeze() + punch.freezeTicks()));
        }
        if (punch.fireSeconds() > 0) {
            target.igniteForSeconds(punch.fireSeconds());
        }
        float magic = punch.arcaneDamage() + target.getMaxHealth() * punch.arcaneHealthRatio();
        if (punch.arcaneMaxDamage() > 0.0F) {
            magic = Math.min(punch.arcaneMaxDamage(), magic);
        }
        if (magic > 0.0F) {
            DelayedDamage.arcane(target, player, magic, SpiritualMonkEvents::magicEffects);
        }
    }

    private static SpiritualMonkSigilEffect config(ServerPlayer player) {
        SpiritualMonkSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.SPIRITUAL_MONK, SpiritualMonkSigilEffect.class);
        return config == null ? SpiritualMonkSigilEffect.DEFAULT : config;
    }

    private static void magicEffects(LivingEntity target) {
        if (target.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.WITCH, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    12, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }
}
