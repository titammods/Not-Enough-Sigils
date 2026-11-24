package com.titammods.not_enough_sigils.sigil;

import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.item.IActivatable;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SigilTracker {
    private static final long ACTIVE_WINDOW = 2L;
    private static final Map<UUID, EnumMap<Kind, Entry>> ACTIVE = new ConcurrentHashMap<>();

    public enum Kind {
        SOUL_PHOENIX,
        SOUL_PHOENIX_GREATER,
        DEMON_NINJA,
        BERSERK,
        BRUNIMNEETS,
        SANGUINE_AEGIS,
        VAMPIRE_LORD,
        SKYWARD_SOUL,
        REAPER,
        SPIRITUAL_MONK,
        HELLBRAND,
        WARDING_GALE,
        SANCTUARY
    }

    private record Entry(long tick, ItemStack stack, ISigilEffect effect) {
    }

    public static void mark(Player player, Kind kind, ItemStack stack, ISigilEffect effect) {
        if (player.level().isClientSide()) {
            return;
        }
        ACTIVE.computeIfAbsent(player.getUUID(), id -> new EnumMap<>(Kind.class))
                .put(kind, new Entry(player.level().getGameTime(), stack, effect));
    }

    public static <T extends ISigilEffect> T getEffect(Player player, Kind kind, Class<T> type) {
        Entry entry = getEntry(player, kind);
        if (entry == null || !type.isInstance(entry.effect())) {
            return null;
        }
        return type.cast(entry.effect());
    }

    public static boolean isActive(Player player, Kind kind) {
        return !getStack(player, kind).isEmpty();
    }

    public static ItemStack getStack(Player player, Kind kind) {
        Entry entry = getEntry(player, kind);
        return entry == null ? ItemStack.EMPTY : entry.stack();
    }

    private static Entry getEntry(Player player, Kind kind) {
        if (player.level().isClientSide()) {
            return null;
        }
        EnumMap<Kind, Entry> entries = ACTIVE.get(player.getUUID());
        if (entries == null) {
            return null;
        }
        Entry entry = entries.get(kind);
        if (entry == null || player.level().getGameTime() - entry.tick() > ACTIVE_WINDOW) {
            return null;
        }
        return entry;
    }

    public static void deactivate(ItemStack stack) {
        if (stack.getItem() instanceof IActivatable activatable) {
            activatable.setActivatedState(stack, false);
        }
    }

    public static int addEV(Player player, ItemStack stack, int amount) {
        if (amount <= 0 || !(stack.getItem() instanceof IBindable bindable)) {
            return 0;
        }
        Binding binding = bindable.getBinding(stack);
        if (binding == null) {
            return 0;
        }
        Anima network = AnimaHelper.getAnima(binding);
        return network == null ? 0 : network.add(AnimaTicket.create(amount), Integer.MAX_VALUE);
    }

    public static int getEV(ItemStack stack) {
        if (!(stack.getItem() instanceof IBindable bindable)) {
            return 0;
        }
        Binding binding = bindable.getBinding(stack);
        if (binding == null) {
            return 0;
        }
        Anima network = AnimaHelper.getAnima(binding);
        return network == null ? 0 : network.getCurrentEV();
    }

    public static boolean drainEV(Player player, ItemStack stack, int amount) {
        if (amount <= 0 || player.isCreative()) {
            return true;
        }
        if (!(stack.getItem() instanceof IBindable bindable)) {
            return false;
        }
        Binding binding = bindable.getBinding(stack);
        if (binding == null) {
            return false;
        }
        Anima network = AnimaHelper.getAnima(binding);
        if (network == null || network.getCurrentEV() < amount) {
            return false;
        }
        network.syphon(AnimaTicket.create(amount));
        return true;
    }

    public static void clear(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
