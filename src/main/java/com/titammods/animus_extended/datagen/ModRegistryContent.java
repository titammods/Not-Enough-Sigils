package com.titammods.animus_extended.datagen;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.setup.ModDamageTypes;
import com.titammods.animus_extended.sigil.effect.BerserkSigilEffect;
import com.titammods.animus_extended.sigil.effect.BrunimneetsSigilEffect;
import com.titammods.animus_extended.sigil.effect.BulletTimeSigilEffect;
import com.titammods.animus_extended.sigil.effect.DemonNinjaSigilEffect;
import com.titammods.animus_extended.sigil.effect.EtherealStepSigilEffect;
import com.titammods.animus_extended.sigil.effect.HellbrandSigilEffect;
import com.titammods.animus_extended.sigil.effect.MagicianSigilEffect;
import com.titammods.animus_extended.sigil.effect.PonymealSigilEffect;
import com.titammods.animus_extended.sigil.effect.ReaperSigilEffect;
import com.titammods.animus_extended.sigil.effect.SanguineAegisSigilEffect;
import com.titammods.animus_extended.sigil.effect.SkywardSoulSigilEffect;
import com.titammods.animus_extended.sigil.effect.SoulPhoenixSigilEffect;
import com.titammods.animus_extended.sigil.effect.SpiritualMonkSigilEffect;
import com.titammods.animus_extended.sigil.effect.TemporalMasterySigilEffect;
import com.titammods.animus_extended.sigil.effect.VampireLordSigilEffect;
import com.titammods.animus_extended.sigil.effect.VeinSeekerSigilEffect;
import com.titammods.animus_extended.sigil.effect.WardingGaleSigilEffect;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import java.util.Optional;

public class ModRegistryContent {

    public static void damageTypes(BootstrapContext<DamageType> context) {
        context.register(ModDamageTypes.ARCANE, new DamageType("animus_extended.arcane", DamageScaling.NEVER, 0.0F));
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
    }

    private static void toggle(BootstrapContext<SigilType> context, String name, int active, ISigilEffect effect) {
        register(context, name, 0, 0, active, effect);
    }

    private static void register(BootstrapContext<SigilType> context, String name, int air, int block, int active, ISigilEffect effect) {
        context.register(SigilTypeRegistry.key(AnimusExtended.rl(name)),
                new SigilType(air, block, 0, active, SigilType.DEFAULT_DRAIN_INTERVAL, Optional.of(effect)));
    }
}
