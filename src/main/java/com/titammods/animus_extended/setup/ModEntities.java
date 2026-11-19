package com.titammods.animus_extended.setup;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.entity.MagicMissileEntity;
import com.titammods.animus_extended.entity.NinjaKnifeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AnimusExtended.MODID);

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
}
