package com.titammods.not_enough_sigils.saereth;

import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModEntities;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class RitualSaereth extends Ritual {
    public static final String NAME = "ritual_saereth";
    public static final int ACTIVATION_COST = 50000;

    public RitualSaereth() {
        super(NAME, 0, ACTIVATION_COST, "ritual." + NotEnoughSigils.MODID + "." + NAME);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        if (!(masterRitualStone.getWorldObj() instanceof ServerLevel level)) {
            return;
        }
        BlockPos master = masterRitualStone.getMasterBlockPos();
        BlockPos spawn = master.above();

        SaerethEntity saereth = ModEntities.SAERETH.get().create(level);
        if (saereth != null) {
            UUID owner = masterRitualStone.getOwner();
            ServerPlayer ownerPlayer = owner == null ? null : level.getServer().getPlayerList().getPlayer(owner);
            saereth.setOwner(owner, ownerPlayer == null ? "" : ownerPlayer.getGameProfile().getName());
            saereth.moveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(saereth);
        }

        ItemEntity sigil = new ItemEntity(level, spawn.getX() + 0.5D, spawn.getY() + 0.5D, spawn.getZ() + 0.5D, new ItemStack(ModItems.SAERETH_SIGIL.get()));
        sigil.setDefaultPickUpDelay();
        level.addFreshEntity(sigil);

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, spawn.getX() + 0.5D, spawn.getY() + 1.0D, spawn.getZ() + 0.5D, 60, 0.6D, 1.0D, 0.6D, 0.05D);
        level.playSound(null, spawn, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, 1.0F, 0.8F);

        masterRitualStone.stopRitual(BreakType.DEACTIVATE);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addOffsetRunes(components, 1, 3, 0, EnumRuneType.WATER);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 4, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 1, EnumRuneType.TENEBRAE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSaereth();
    }
}
