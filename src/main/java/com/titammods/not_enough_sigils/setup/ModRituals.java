package com.titammods.not_enough_sigils.setup;

import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.ritual.RitualBuilder;
import com.titammods.not_enough_sigils.saereth.RitualSaereth;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRituals {
    public static final DeferredRegister<Ritual> RITUALS = DeferredRegister.create(RitualRegistry.RITUAL_REGISTRY_KEY, NotEnoughSigils.MODID);

    public static final DeferredHolder<Ritual, RitualSaereth> SAERETH = RITUALS.register(RitualSaereth.NAME, RitualSaereth::new);
    public static final DeferredHolder<Ritual, RitualBuilder> BUILDER = RITUALS.register(RitualBuilder.NAME, RitualBuilder::new);
}
