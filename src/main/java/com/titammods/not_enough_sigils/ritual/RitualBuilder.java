package com.titammods.not_enough_sigils.ritual;

import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.block.BlockMasterRitualStone;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.EnumMiningMode;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualBuilder extends Ritual {
    public static final String NAME = "builder";
    public static final String MINING_RANGE = "miningRange";
    public static final String CHEST_RANGE = "chestRange";
    public static final int ACTIVATION_COST = 30000;
    public static final int FORTUNE_LEVEL = 8;
    private static final int BLOCKS_PER_REFRESH = 8;
    private static final int CHECKS_PER_REFRESH = 4096;
    private static final TicketType<ChunkPos> TICKET = TicketType.create(NotEnoughSigils.MODID + "_builder", Comparator.comparingLong(ChunkPos::toLong), 100);

    private int chunkIndex;
    private BlockPos cursor;
    private long areaSignature = Long.MIN_VALUE;

    public RitualBuilder() {
        super(NAME, 1, ACTIVATION_COST, "ritual." + NotEnoughSigils.MODID + "." + NAME);
        addBlockRange(MINING_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-8, -16, -8), 16, 15, 16));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(MINING_RANGE, Integer.MAX_VALUE, 1024, 512);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public boolean usesMiningMode() {
        return true;
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null || !(ctx.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos masterPos = ctx.masterPos();
        AreaDescriptor descriptor = RitualHelper.getEffectiveRange(ctx.master(), this, MINING_RANGE);
        if (!(descriptor instanceof AreaDescriptor.Rectangle area)) {
            return;
        }
        BlockPos min = masterPos.offset(area.getMinimumOffset());
        BlockPos max = masterPos.offset(area.getMaximumOffset()).offset(-1, -1, -1);
        if (max.getX() < min.getX() || max.getY() < min.getY() || max.getZ() < min.getZ()) {
            return;
        }
        long signature = signature(min, max);
        if (signature != areaSignature) {
            areaSignature = signature;
            chunkIndex = 0;
            cursor = null;
        }

        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, masterPos).orElse(masterPos.above());
        IItemHandler chest = level.getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);
        if (chest == null) {
            return;
        }

        int minChunkX = min.getX() >> 4;
        int minChunkZ = min.getZ() >> 4;
        int chunksX = (max.getX() >> 4) - minChunkX + 1;
        int chunksZ = (max.getZ() >> 4) - minChunkZ + 1;
        int totalChunks = chunksX * chunksZ;
        if (chunkIndex >= totalChunks) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        int budget = Math.min(BLOCKS_PER_REFRESH, ctx.maxOperations(getRefreshCost()));
        if (budget <= 0) {
            return;
        }

        UUID owner = ctx.master().getOwner();
        boolean fortune = ctx.master().getMiningMode() == EnumMiningMode.FORTUNE;
        ItemStack tool = createTool(level, fortune);
        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(level, owner, "Builder Ritual", masterPos);

        int mined = 0;
        int checks = 0;
        while (mined < budget && checks < CHECKS_PER_REFRESH && chunkIndex < totalChunks) {
            int cx = minChunkX + chunkIndex % chunksX;
            int cz = minChunkZ + chunkIndex / chunksX;
            ChunkPos chunk = new ChunkPos(cx, cz);
            level.getChunkSource().addRegionTicket(TICKET, chunk, 1, chunk);
            level.getChunk(cx, cz);

            int x0 = Math.max(min.getX(), chunk.getMinBlockX());
            int x1 = Math.min(max.getX(), chunk.getMaxBlockX());
            int z0 = Math.max(min.getZ(), chunk.getMinBlockZ());
            int z1 = Math.min(max.getZ(), chunk.getMaxBlockZ());
            int y0 = Math.max(min.getY(), level.getMinBuildHeight());
            int y1 = Math.min(max.getY(), level.getMaxBuildHeight() - 1);

            if (cursor == null) {
                cursor = new BlockPos(x0, y1, z0);
            }
            int x = cursor.getX();
            int y = cursor.getY();
            int z = cursor.getZ();
            boolean chunkDone = false;
            boolean blocked = false;

            while (mined < budget && checks < CHECKS_PER_REFRESH) {
                if (y < y0) {
                    chunkDone = true;
                    break;
                }
                BlockPos pos = new BlockPos(x, y, z);
                checks++;
                int result = mine(level, ctx, pos, masterPos, chestPos, chest, tool, fakePlayer, owner);
                if (result < 0) {
                    blocked = true;
                    break;
                }
                mined += result;
                z++;
                if (z > z1) {
                    z = z0;
                    x++;
                    if (x > x1) {
                        x = x0;
                        y--;
                    }
                }
            }

            if (chunkDone) {
                chunkIndex++;
                cursor = null;
                continue;
            }
            cursor = new BlockPos(x, y, z);
            if (blocked) {
                break;
            }
        }

        if (mined > 0) {
            ctx.syphon(getRefreshCost() * mined);
        }
    }

    private int mine(ServerLevel level, RitualContext ctx, BlockPos pos, BlockPos masterPos, BlockPos chestPos,
                     IItemHandler chest, ItemStack tool, FakePlayer fakePlayer, UUID owner) {
        if (pos.equals(masterPos) || pos.equals(chestPos)) {
            return 0;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getBlock() instanceof LiquidBlock || state.is(NVTags.Blocks.QUARRY_BLACKLIST)) {
            return 0;
        }
        if (state.getDestroySpeed(level, pos) < 0.0F || state.getBlock() instanceof BlockRitualStone || state.getBlock() instanceof BlockMasterRitualStone) {
            return 0;
        }
        if (!BlockProtectionHelper.canBreakBlock(level, pos, owner)) {
            return 0;
        }
        List<ItemStack> drops = RitualHelper.getBlockDrops(level, state, pos, tool, fakePlayer);
        for (ItemStack drop : drops) {
            if (!ItemHandlerHelper.insertItemStacked(chest, drop.copy(), true).isEmpty()) {
                return -1;
            }
        }
        for (ItemStack drop : drops) {
            ItemHandlerHelper.insertItemStacked(chest, drop, false);
        }
        level.removeBlock(pos, false);
        return 1;
    }

    private static ItemStack createTool(ServerLevel level, boolean fortune) {
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        Holder<Enchantment> enchantment = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(fortune ? Enchantments.FORTUNE : Enchantments.SILK_TOUCH);
        tool.enchant(enchantment, fortune ? FORTUNE_LEVEL : 1);
        return tool;
    }

    private static long signature(BlockPos min, BlockPos max) {
        long hash = min.asLong();
        hash = hash * 31L + max.asLong();
        return hash;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        chunkIndex = tag.getInt("builderChunk");
        cursor = tag.contains("builderCursor") ? BlockPos.of(tag.getLong("builderCursor")) : null;
        areaSignature = tag.contains("builderArea") ? tag.getLong("builderArea") : Long.MIN_VALUE;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putInt("builderChunk", chunkIndex);
        if (cursor != null) {
            tag.putLong("builderCursor", cursor.asLong());
        }
        tag.putLong("builderArea", areaSignature);
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".mining")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 3, 0, EnumRuneType.FIRE);
        addOffsetRunes(components, 1, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 1, EnumRuneType.AIR);
        addParallelRunes(components, 2, 1, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 2, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualBuilder();
    }
}
