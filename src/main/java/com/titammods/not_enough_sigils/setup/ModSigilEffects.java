package com.titammods.not_enough_sigils.setup;

import com.breakinblocks.neovitae.api.registry.NeoVitaeRegistries;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.effect.BerserkSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.BrunimneetsSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.DemonNinjaSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.MagicianSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.PonymealSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SoulPhoenixSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SpiritualMonkSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.TemporalMasterySigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SanguineAegisSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.VampireLordSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.CurseOfGluttonySigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.EtherealStepSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.FairysBlessingSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.SkywardSoulSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.RecallSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.ReaperSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.HellbrandSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.VeinSeekerSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.WardingGaleSigilEffect;
import com.titammods.not_enough_sigils.sigil.effect.BulletTimeSigilEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSigilEffects {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
            DeferredRegister.create(
                    (ResourceKey<Registry<MapCodec<? extends ISigilEffect>>>) (ResourceKey<?>) NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY,
                    NotEnoughSigils.MODID
            );

    public static final Supplier<MapCodec<SoulPhoenixSigilEffect>> SOUL_PHOENIX =
            SIGIL_EFFECTS.register("soul_phoenix", () -> SoulPhoenixSigilEffect.CODEC);

    public static final Supplier<MapCodec<DemonNinjaSigilEffect>> DEMON_NINJA =
            SIGIL_EFFECTS.register("demon_ninja", () -> DemonNinjaSigilEffect.CODEC);

    public static final Supplier<MapCodec<BerserkSigilEffect>> BERSERK =
            SIGIL_EFFECTS.register("berserk", () -> BerserkSigilEffect.CODEC);

    public static final Supplier<MapCodec<MagicianSigilEffect>> MAGICIAN =
            SIGIL_EFFECTS.register("magician", () -> MagicianSigilEffect.CODEC);

    public static final Supplier<MapCodec<BrunimneetsSigilEffect>> BRUNIMNEETS =
            SIGIL_EFFECTS.register("brunimneets", () -> BrunimneetsSigilEffect.CODEC);

    public static final Supplier<MapCodec<SanguineAegisSigilEffect>> SANGUINE_AEGIS =
            SIGIL_EFFECTS.register("sanguine_aegis", () -> SanguineAegisSigilEffect.CODEC);

    public static final Supplier<MapCodec<VampireLordSigilEffect>> VAMPIRE_LORD =
            SIGIL_EFFECTS.register("vampire_lord", () -> VampireLordSigilEffect.CODEC);

    public static final Supplier<MapCodec<EtherealStepSigilEffect>> ETHEREAL_STEP =
            SIGIL_EFFECTS.register("ethereal_step", () -> EtherealStepSigilEffect.CODEC);

    public static final Supplier<MapCodec<SkywardSoulSigilEffect>> SKYWARD_SOUL =
            SIGIL_EFFECTS.register("skyward_soul", () -> SkywardSoulSigilEffect.CODEC);

    public static final Supplier<MapCodec<ReaperSigilEffect>> REAPER =
            SIGIL_EFFECTS.register("reaper", () -> ReaperSigilEffect.CODEC);

    public static final Supplier<MapCodec<SpiritualMonkSigilEffect>> SPIRITUAL_MONK =
            SIGIL_EFFECTS.register("spiritual_monk", () -> SpiritualMonkSigilEffect.CODEC);

    public static final Supplier<MapCodec<HellbrandSigilEffect>> HELLBRAND =
            SIGIL_EFFECTS.register("hellbrand", () -> HellbrandSigilEffect.CODEC);

    public static final Supplier<MapCodec<VeinSeekerSigilEffect>> VEIN_SEEKER =
            SIGIL_EFFECTS.register("vein_seeker", () -> VeinSeekerSigilEffect.CODEC);

    public static final Supplier<MapCodec<WardingGaleSigilEffect>> WARDING_GALE =
            SIGIL_EFFECTS.register("warding_gale", () -> WardingGaleSigilEffect.CODEC);

    public static final Supplier<MapCodec<BulletTimeSigilEffect>> BULLET_TIME =
            SIGIL_EFFECTS.register("bullet_time", () -> BulletTimeSigilEffect.CODEC);

    public static final Supplier<MapCodec<PonymealSigilEffect>> PONYMEAL =
            SIGIL_EFFECTS.register("ponymeal", () -> PonymealSigilEffect.CODEC);

    public static final Supplier<MapCodec<TemporalMasterySigilEffect>> TEMPORAL_MASTERY =
            SIGIL_EFFECTS.register("temporal_mastery", () -> TemporalMasterySigilEffect.CODEC);

    public static final Supplier<MapCodec<CurseOfGluttonySigilEffect>> CURSE_OF_GLUTTONY =
            SIGIL_EFFECTS.register("curse_of_gluttony", () -> CurseOfGluttonySigilEffect.CODEC);

    public static final Supplier<MapCodec<FairysBlessingSigilEffect>> FAIRYS_BLESSING =
            SIGIL_EFFECTS.register("fairys_blessing", () -> FairysBlessingSigilEffect.CODEC);

    public static final Supplier<MapCodec<RecallSigilEffect>> RECALL =
            SIGIL_EFFECTS.register("recall", () -> RecallSigilEffect.CODEC);
}
