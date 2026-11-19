package com.titammods.animus_extended.event;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.setup.ModItems;
import com.titammods.animus_extended.sigil.DelayedDamage;
import com.titammods.animus_extended.sigil.SpiritusAffinity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Optional;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class SpiritualMonkEvents {
    private static final float RAW_DAMAGE_MULTIPLIER = 1.25F;
    private static final int WITHER_TICKS = 100;
    private static final int WITHER_AMPLIFIER = 1;
    private static final int FIRE_SECONDS = 4;
    private static final int FROST_SLOW_TICKS = 60;
    private static final int FROST_SLOW_AMPLIFIER = 2;
    private static final int FROST_FREEZE_TICKS = 100;
    private static final float MAGIC_BASE_DAMAGE = 4.0F;
    private static final float MAGIC_HEALTH_RATIO = 0.05F;
    private static final float MAGIC_MAX_DAMAGE = 20.0F;

    public static boolean hasSpiritualMonk(ServerPlayer player) {
        ItemStack active = MonkSigilEventHandler.findActiveMonkSigil(player);
        return active != null && active.is(ModItems.SIGIL_SPIRITUAL_MONK.get());
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player)) {
            return;
        }
        if (!event.getState().is(Tags.Blocks.ORES) || !hasSpiritualMonk(player)) {
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
        if (target == player || !player.getMainHandItem().isEmpty() || !hasSpiritualMonk(player)) {
            return;
        }

        SpiritusType type = SpiritusAffinity.of(player);
        switch (type) {
            case RUINA -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_TICKS, WITHER_AMPLIFIER), player);
            case VINDICTA -> target.igniteForSeconds(FIRE_SECONDS);
            case INVICTUS -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FROST_SLOW_TICKS, FROST_SLOW_AMPLIFIER), player);
                if (target.canFreeze()) {
                    target.setTicksFrozen(Math.max(target.getTicksFrozen(), target.getTicksRequiredToFreeze() + FROST_FREEZE_TICKS));
                }
            }
            case NIHILUM -> {
                float magic = Math.min(MAGIC_MAX_DAMAGE, MAGIC_BASE_DAMAGE + target.getMaxHealth() * MAGIC_HEALTH_RATIO);
                DelayedDamage.arcane(target, player, magic, SpiritualMonkEvents::magicEffects);
            }
            default -> event.setAmount(event.getAmount() * RAW_DAMAGE_MULTIPLIER);
        }
    }

    private static void magicEffects(LivingEntity target) {
        if (target.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.WITCH, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    12, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }
}
