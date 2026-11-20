package com.titammods.animus_extended.event;

import com.titammods.animus_extended.sigil.effect.BrunimneetsSigilEffect;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class BrunimneetsEvents {
    private static final double PLAYER_HEIGHT = 1.8D;
    private static final double PIXEL = 1.0D / 16.0D;

    private static final ResourceLocation SCALE_ID = AnimusExtended.rl("brunimneets_scale");
    private static final ResourceLocation BLOCK_RANGE_ID = AnimusExtended.rl("brunimneets_block_range");
    private static final ResourceLocation ENTITY_RANGE_ID = AnimusExtended.rl("brunimneets_entity_range");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        BrunimneetsSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.BRUNIMNEETS, BrunimneetsSigilEffect.class);
        boolean active = config != null;
        double scale = active ? -(config.heightReductionPixels() * PIXEL) / PLAYER_HEIGHT : 0.0D;
        double blockRange = active ? config.blockRangeBonus() : 0.0D;
        double entityRange = active ? config.entityRangeBonus() : 0.0D;
        update(player, Attributes.SCALE, SCALE_ID, scale, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
        update(player, Attributes.BLOCK_INTERACTION_RANGE, BLOCK_RANGE_ID, blockRange, AttributeModifier.Operation.ADD_VALUE, active);
        update(player, Attributes.ENTITY_INTERACTION_RANGE, ENTITY_RANGE_ID, entityRange, AttributeModifier.Operation.ADD_VALUE, active);
    }

    private static void update(Player player, Holder<Attribute> attribute, ResourceLocation id, double amount,
                               AttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(id);
        if (!active || amount == 0.0D) {
            if (current != null) {
                instance.removeModifier(id);
            }
            return;
        }
        if (current != null && current.amount() == amount && current.operation() == operation) {
            return;
        }
        if (current != null) {
            instance.removeModifier(id);
        }
        instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }
}
