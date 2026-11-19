package com.titammods.animus_extended.event;

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
    private static final double HEIGHT_REDUCTION = 4.0D / 16.0D;
    private static final double SCALE_MODIFIER = -HEIGHT_REDUCTION / PLAYER_HEIGHT;
    private static final double RANGE_BONUS = 10.0D;

    private static final ResourceLocation SCALE_ID = AnimusExtended.rl("brunimneets_scale");
    private static final ResourceLocation BLOCK_RANGE_ID = AnimusExtended.rl("brunimneets_block_range");
    private static final ResourceLocation ENTITY_RANGE_ID = AnimusExtended.rl("brunimneets_entity_range");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        boolean active = SigilTracker.isActive(player, SigilTracker.Kind.BRUNIMNEETS);
        update(player, Attributes.SCALE, SCALE_ID, SCALE_MODIFIER, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
        update(player, Attributes.BLOCK_INTERACTION_RANGE, BLOCK_RANGE_ID, RANGE_BONUS, AttributeModifier.Operation.ADD_VALUE, active);
        update(player, Attributes.ENTITY_INTERACTION_RANGE, ENTITY_RANGE_ID, RANGE_BONUS, AttributeModifier.Operation.ADD_VALUE, active);
    }

    private static void update(Player player, Holder<Attribute> attribute, ResourceLocation id, double amount,
                               AttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        boolean present = instance.hasModifier(id);
        if (active && !present) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
        } else if (!active && present) {
            instance.removeModifier(id);
        }
    }
}
