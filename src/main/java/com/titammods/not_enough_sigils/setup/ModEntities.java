package com.titammods.not_enough_sigils.setup;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.entity.MagicMissileEntity;
import com.titammods.not_enough_sigils.entity.NinjaKnifeEntity;
import com.titammods.not_enough_sigils.saereth.SaerethEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NotEnoughSigils.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileEntity>> MAGIC_MISSILE = ENTITY_TYPES.register(
            "magic_missile",
            () -> EntityType.Builder.<MagicMissileEntity>of(MagicMissileEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(6)
                    .updateInterval(10)
                    .build("magic_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<NinjaKnifeEntity>> NINJA_KNIFE = ENTITY_TYPES.register(
            "ninja_knife",
            () -> EntityType.Builder.<NinjaKnifeEntity>of(NinjaKnifeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("ninja_knife")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SaerethEntity>> SAERETH = ENTITY_TYPES.register(
            "saereth",
            () -> EntityType.Builder.<SaerethEntity>of(SaerethEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .eyeHeight(1.62F)
                    .clientTrackingRange(10)
                    .build("saereth")
    );

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SAERETH.get(), SaerethEntity.createAttributes().build());
    }
}
