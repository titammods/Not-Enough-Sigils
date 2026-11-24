package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModDamageTypes;
import com.titammods.not_enough_sigils.sigil.effect.BerserkSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.BrunimneetsSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.BulletTimeSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.CurseOfGluttonySigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.DemonNinjaSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.EtherealStepSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.FairysBlessingSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.HellbrandSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.MagicianSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.PonymealSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.AlchemistSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.ExecutionerSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.ChainsSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.HarvestSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.RecallSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SanctuarySigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.ReaperSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SanguineAegisSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SkywardSoulSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SoulPhoenixSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SpiritualMonkSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.TemporalMasterySigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.VampireLordSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.VeinSeekerSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.WardingGaleSigilEffect;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import java.util.Optional;

public class ModRegistryContent {

    public static void damageTypes(BootstrapContext<DamageType> context) {
        context.register(ModDamageTypes.ARCANE, new DamageType("not_enough_sigils.arcane", DamageScaling.NEVER, 0.0F));
        context.register(ModDamageTypes.EXECUTION, new DamageType("not_enough_sigils.execution", DamageScaling.NEVER, 0.0F));
    }

    public static void sigilTypes(BootstrapContext<SigilType> context) {
        toggle(context, "soul_phoenix", 50, SoulPhoenixSigilEffect.LESSER);
        toggle(context, "soul_phoenix_greater", 80, SoulPhoenixSigilEffect.GREATER);
        register(context, "demon_ninja", 20, 0, 25, DemonNinjaSigilEffect.DEFAULT);
        toggle(context, "berserk", 30, BerserkSigilEffect.DEFAULT);
        register(context, "magician", 50, 0, 0, MagicianSigilEffect.DEFAULT);
        register(context, "archmage", 100, 0, 0, MagicianSigilEffect.basic(3, true, 30));
        toggle(context, "brunimneets", 40, BrunimneetsSigilEffect.DEFAULT);
        toggle(context, "sanguine_aegis", 150, SanguineAegisSigilEffect.DEFAULT);
        toggle(context, "vampire_lord", 25, VampireLordSigilEffect.DEFAULT);
        register(context, "ethereal_step", 30, 30, 0, EtherealStepSigilEffect.DEFAULT);
        toggle(context, "skyward_soul", 150, SkywardSoulSigilEffect.DEFAULT);
        toggle(context, "reaper", 25, ReaperSigilEffect.DEFAULT);
        toggle(context, "hellbrand", 30, HellbrandSigilEffect.DEFAULT);
        register(context, "vein_seeker", 75, 75, 0, VeinSeekerSigilEffect.DEFAULT);
        toggle(context, "warding_gale", 40, WardingGaleSigilEffect.DEFAULT);
        register(context, "bullet_time", 300, 0, 0, BulletTimeSigilEffect.DEFAULT);
        toggle(context, "spiritual_monk", 20, SpiritualMonkSigilEffect.DEFAULT);
        register(context, "ponymeal", 0, 10, 0, PonymealSigilEffect.DEFAULT);
        register(context, "temporal_mastery", 0, 0, 0, TemporalMasterySigilEffect.DEFAULT);
        toggle(context, "curse_of_gluttony", 0, CurseOfGluttonySigilEffect.DEFAULT);
        toggle(context, "fairys_blessing", 60, FairysBlessingSigilEffect.DEFAULT);
        toggle(context, "fairys_blessing_divine", 150, new FairysBlessingSigilEffect(4, FairysBlessingSigilEffect.DEFAULT_DURATION, FairysBlessingSigilEffect.DEFAULT_REFRESH_BELOW));
        register(context, "recall", 0, 0, 0, RecallSigilEffect.DEFAULT);
        register(context, "alchemist", 0, 0, 0, AlchemistSigilEffect.DEFAULT);
        toggle(context, "sanctuary", 120, SanctuarySigilEffect.DEFAULT);
        toggle(context, "executioner", 30, ExecutionerSigilEffect.DEFAULT);
        register(context, "chains", 40, 40, 0, ChainsSigilEffect.DEFAULT);
        toggle(context, "harvest", 40, HarvestSigilEffect.DEFAULT);
        toggle(context, "berserk_divine", 80, new BerserkSigilEffect(6, 300, 600, 4, 2, 140));
    }

    private static void toggle(BootstrapContext<SigilType> context, String name, int active, ISigilEffect effect) {
        register(context, name, 0, 0, active, effect);
    }

    private static void register(BootstrapContext<SigilType> context, String name, int air, int block, int active, ISigilEffect effect) {
        context.register(SigilTypeRegistry.key(NotEnoughSigils.rl(name)),
                new SigilType(air, block, 0, active, SigilType.DEFAULT_DRAIN_INTERVAL, Optional.of(effect)));
    }
}
