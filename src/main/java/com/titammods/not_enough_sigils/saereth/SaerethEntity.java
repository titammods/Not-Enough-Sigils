package com.titammods.not_enough_sigils.saereth;

import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.block.AlchemyArrayBlock;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.ItemArcaneScribeTool;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SaerethEntity extends PathfinderMob {
    public static final String NAME = "Saereth";

    private static final EntityDataAccessor<Byte> DATA_ACTION = SynchedEntityData.defineId(SaerethEntity.class, EntityDataSerializers.BYTE);

    private UUID owner;
    private String ownerName = "";
    private BlockPos chestPos;
    private BlockPos arrayPos;
    private Phase phase = Phase.IDLE;
    private Status status = Status.NOT_LINKED;
    private int phaseTicks;
    private int scanCooldown;
    private ResourceLocation recipeId;
    private ItemStack carried = ItemStack.EMPTY;

    public SaerethEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        setCustomName(Component.literal(NAME));
        setCustomNameVisible(true);
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTION, (byte) Action.IDLE.ordinal());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !isBusy() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isBusy() && super.canContinueToUse();
            }
        });
        goalSelector.addGoal(2, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !isBusy() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isBusy() && super.canContinueToUse();
            }
        });
    }

    public Action getAction() {
        byte id = entityData.get(DATA_ACTION);
        Action[] values = Action.values();
        return id >= 0 && id < values.length ? values[id] : Action.IDLE;
    }

    private void setAction(Action action) {
        if (getAction() != action) {
            entityData.set(DATA_ACTION, (byte) action.ordinal());
        }
    }

    public void setOwner(UUID owner, String ownerName) {
        this.owner = owner;
        this.ownerName = ownerName == null ? "" : ownerName;
    }

    public boolean isOwner(Player player) {
        return owner == null || owner.equals(player.getUUID()) || player.hasPermissions(2);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (level() instanceof ServerLevel serverLevel) {
            work(serverLevel);
        }
    }

    private void work(ServerLevel level) {
        SaerethSettings settings = SaerethSettings.get();
        phaseTicks++;
        if (arrayPos != null && phase != Phase.IDLE) {
            Vec3 center = Vec3.atCenterOf(arrayPos);
            getLookControl().setLookAt(center.x, center.y - 0.5D, center.z);
        }
        switch (phase) {
            case IDLE -> idle(level, settings);
            case DRAW -> draw(level, settings);
            case PLACE_BASE -> place(level, settings, true);
            case PLACE_ADDED -> place(level, settings, false);
            case CAST -> cast(level, settings);
        }
    }

    private void idle(ServerLevel level, SaerethSettings settings) {
        setAction(Action.IDLE);
        if (--scanCooldown > 0) {
            return;
        }
        scanCooldown = Math.max(5, settings.scanInterval());

        IItemHandler chest = validate(level, settings);
        if (chest == null) {
            return;
        }
        if (findAsh(chest) < 0) {
            status = Status.NO_ASH;
            return;
        }
        RecipeHolder<AlchemyArrayRecipe> recipe = findRecipe(level, chest);
        if (recipe == null) {
            status = Status.NO_RECIPE;
            return;
        }
        recipeId = recipe.id();
        status = Status.WORKING;
        startPhase(Phase.DRAW);
        setItemSlot(EquipmentSlot.MAINHAND, chest.getStackInSlot(findAsh(chest)).copyWithCount(1));
        setAction(Action.DRAW);
    }

    private void draw(ServerLevel level, SaerethSettings settings) {
        setAction(Action.DRAW);
        if (phaseTicks % 4 == 0) {
            Vec3 center = Vec3.atBottomCenterOf(arrayPos);
            level.sendParticles(ParticleTypes.ASH, center.x, center.y + 0.05D, center.z, 6, 0.4D, 0.02D, 0.4D, 0.0D);
            level.playSound(null, arrayPos, SoundEvents.SAND_STEP, SoundSource.NEUTRAL, 0.4F, 0.8F + random.nextFloat() * 0.4F);
        }
        if (phaseTicks < settings.drawTicks()) {
            return;
        }
        IItemHandler chest = validate(level, settings);
        int ashSlot = chest == null ? -1 : findAsh(chest);
        if (chest == null || ashSlot < 0) {
            abort(level);
            return;
        }
        ItemStack ash = chest.extractItem(ashSlot, 1, false);
        if (ash.isEmpty() || !placeArray(level, ash)) {
            if (!ash.isEmpty()) {
                returnToChest(chest, ash, level);
            }
            abort(level);
            return;
        }
        ash.setDamageValue(ash.getDamageValue() + 1);
        if (ash.getDamageValue() >= ash.getMaxDamage()) {
            level.playSound(null, blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 0.6F, 1.0F);
        } else {
            returnToChest(chest, ash, level);
        }
        startPhase(Phase.PLACE_BASE);
    }

    private void place(ServerLevel level, SaerethSettings settings, boolean base) {
        setAction(Action.PLACE);
        if (phaseTicks == 1) {
            IItemHandler chest = validate(level, settings);
            AlchemyArrayRecipe recipe = currentRecipe(level);
            if (chest == null || recipe == null) {
                abort(level);
                return;
            }
            Ingredient ingredient = base ? recipe.getBaseInput() : recipe.getAddedInput();
            int slot = findMatching(chest, ingredient, -1, 1);
            if (slot < 0) {
                status = Status.NO_RECIPE;
                abort(level);
                return;
            }
            carried = chest.extractItem(slot, 1, false);
            setItemSlot(EquipmentSlot.MAINHAND, carried.copy());
            return;
        }
        if (phaseTicks < Math.max(2, settings.placeTicks())) {
            return;
        }
        if (!(level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity array)) {
            abort(level);
            return;
        }
        array.inv.setStackInSlot(base ? 0 : 1, carried.copyWithCount(1));
        carried = ItemStack.EMPTY;
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        swing(InteractionHand.MAIN_HAND);
        level.playSound(null, arrayPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.NEUTRAL, 0.6F, 1.2F);
        if (base) {
            startPhase(Phase.PLACE_ADDED);
            return;
        }
        array.attemptCraft();
        BlockState state = level.getBlockState(arrayPos);
        level.sendBlockUpdated(arrayPos, state, state, 3);
        startPhase(Phase.CAST);
    }

    private void cast(ServerLevel level, SaerethSettings settings) {
        setAction(Action.CAST);
        boolean arrayPresent = level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity;
        if (arrayPresent && phaseTicks % 3 == 0) {
            Vec3 hands = position().add(0.0D, 2.1D, 0.0D);
            Vec3 target = Vec3.atCenterOf(arrayPos).subtract(hands);
            double progress = (phaseTicks % 15) / 15.0D;
            Vec3 point = hands.add(target.scale(progress));
            level.sendParticles(ParticleTypes.ENCHANT, hands.x, hands.y, hands.z, 3, 0.25D, 0.1D, 0.25D, 0.4D);
            level.sendParticles(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, point.x, point.y, point.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
        }
        if (arrayPresent && level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity array
                && phaseTicks > 20 && !array.isActive && phaseTicks < settings.castTimeout()) {
            array.attemptCraft();
        }
        if (arrayPresent && phaseTicks < settings.castTimeout()) {
            return;
        }
        if (arrayPresent && level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity array) {
            array.dropItems();
            level.removeBlock(arrayPos, false);
        }
        IItemHandler chest = chestHandler(level);
        collect(level, chest);
        level.playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 0.8F, 1.0F);
        finish();
    }

    private void collect(ServerLevel level, IItemHandler chest) {
        AABB area = new AABB(arrayPos).inflate(0.75D);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area, Entity::isAlive);
        for (ItemEntity item : items) {
            if (chest == null) {
                status = Status.CHEST_FULL;
                return;
            }
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(chest, item.getItem().copy(), false);
            if (remainder.isEmpty()) {
                item.discard();
            } else {
                item.setItem(remainder);
                status = Status.CHEST_FULL;
            }
        }
    }

    private void abort(ServerLevel level) {
        if (!carried.isEmpty()) {
            IItemHandler chest = chestHandler(level);
            if (chest != null) {
                returnToChest(chest, carried, level);
            } else {
                spawnAtLevel(level, carried);
            }
        }
        if (arrayPos != null && phase != Phase.DRAW && phase != Phase.IDLE
                && level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity array) {
            array.dropItems();
            level.removeBlock(arrayPos, false);
            collect(level, chestHandler(level));
        }
        finish();
    }

    private void finish() {
        carried = ItemStack.EMPTY;
        recipeId = null;
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        startPhase(Phase.IDLE);
        scanCooldown = 10;
        if (status == Status.WORKING) {
            status = Status.READY;
        }
    }

    private void startPhase(Phase next) {
        phase = next;
        phaseTicks = 0;
    }

    private void returnToChest(IItemHandler chest, ItemStack stack, ServerLevel level) {
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(chest, stack, false);
        if (!remainder.isEmpty()) {
            spawnAtLevel(level, remainder);
            status = Status.CHEST_FULL;
        }
    }

    private void spawnAtLevel(ServerLevel level, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5D, getZ(), stack));
    }

    private IItemHandler validate(ServerLevel level, SaerethSettings settings) {
        if (chestPos == null || arrayPos == null) {
            status = Status.NOT_LINKED;
            return null;
        }
        if (!level.isLoaded(chestPos) || !level.isLoaded(arrayPos)) {
            status = Status.UNLOADED;
            return null;
        }
        if (!inRange(chestPos, settings) || !inRange(arrayPos, settings)) {
            status = Status.TOO_FAR;
            return null;
        }
        IItemHandler chest = chestHandler(level);
        if (chest == null) {
            status = Status.CHEST_MISSING;
            return null;
        }
        if (phase == Phase.IDLE && !arraySpotFree(level)) {
            status = Status.BLOCKED;
            return null;
        }
        return chest;
    }

    private boolean inRange(BlockPos pos, SaerethSettings settings) {
        double range = settings.linkRange() + 0.5D;
        return pos.distToCenterSqr(position()) <= range * range;
    }

    private IItemHandler chestHandler(ServerLevel level) {
        if (chestPos == null || !level.isLoaded(chestPos)) {
            return null;
        }
        return level.getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);
    }

    private boolean arraySpotFree(ServerLevel level) {
        FluidState fluid = level.getFluidState(arrayPos);
        boolean water = fluid.is(Fluids.WATER) && fluid.isSource();
        if (!level.isEmptyBlock(arrayPos) && !water) {
            return false;
        }
        return NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState().canSurvive(level, arrayPos);
    }

    private boolean placeArray(ServerLevel level, ItemStack ash) {
        if (!arraySpotFree(level)) {
            status = Status.BLOCKED;
            return false;
        }
        FluidState fluid = level.getFluidState(arrayPos);
        boolean water = fluid.is(Fluids.WATER) && fluid.isSource();
        BlockState state = NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState().setValue(AlchemyArrayBlock.WATERLOGGED, water);
        if (!level.setBlock(arrayPos, state, 3)) {
            return false;
        }
        BlockEntity tile = level.getBlockEntity(arrayPos);
        if (tile instanceof AlchemyArrayBlockEntity array) {
            array.setRotation(Direction.fromYRot(getYHeadRot()));
            Binding binding = ash.getItem() instanceof ItemArcaneScribeTool tool ? tool.getBinding(ash) : null;
            if (binding == null && owner != null) {
                binding = new Binding(owner, ownerName);
            }
            if (binding != null) {
                array.setOwnerBinding(binding);
            }
            DyeColor color = ash.get(NVDataComponents.ALCHEMY_ARRAY_COLOR.get());
            if (color != null) {
                array.setArrayColor(color);
            }
        }
        level.playSound(null, arrayPos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.NEUTRAL, 0.7F, 1.0F);
        return true;
    }

    private static int findAsh(IItemHandler chest) {
        for (int slot = 0; slot < chest.getSlots(); slot++) {
            ItemStack stack = chest.getStackInSlot(slot);
            if (stack.getItem() instanceof ItemArcaneScribeTool && (!stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage())) {
                return slot;
            }
        }
        return -1;
    }

    private static int findMatching(IItemHandler chest, Ingredient ingredient, int skipSlot, int needed) {
        for (int slot = 0; slot < chest.getSlots(); slot++) {
            ItemStack stack = chest.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getItem() instanceof ItemArcaneScribeTool || !ingredient.test(stack)) {
                continue;
            }
            int available = slot == skipSlot ? stack.getCount() - 1 : stack.getCount();
            if (available >= needed) {
                return slot;
            }
        }
        return -1;
    }

    private static RecipeHolder<AlchemyArrayRecipe> findRecipe(ServerLevel level, IItemHandler chest) {
        for (RecipeHolder<AlchemyArrayRecipe> holder : level.getRecipeManager().getAllRecipesFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get())) {
            AlchemyArrayRecipe recipe = holder.value();
            AlchemyArrayEffectType type = recipe.getEffectType();
            if ((type != AlchemyArrayEffectType.CRAFTING && type != AlchemyArrayEffectType.BINDING) || recipe.getOutput().isEmpty()) {
                continue;
            }
            int baseSlot = findMatching(chest, recipe.getBaseInput(), -1, 1);
            if (baseSlot < 0) {
                continue;
            }
            if (findMatching(chest, recipe.getAddedInput(), baseSlot, 1) >= 0) {
                return holder;
            }
        }
        return null;
    }

    private AlchemyArrayRecipe currentRecipe(ServerLevel level) {
        if (recipeId == null) {
            return null;
        }
        Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(recipeId);
        return holder.map(RecipeHolder::value).filter(AlchemyArrayRecipe.class::isInstance).map(AlchemyArrayRecipe.class::cast).orElse(null);
    }

    public boolean isBusy() {
        return phase != Phase.IDLE;
    }

    public void link(BlockPos chest, BlockPos array) {
        chestPos = chest;
        arrayPos = array;
        status = Status.READY;
        scanCooldown = 1;
    }

    public boolean canReach(BlockPos pos) {
        return inRange(pos, SaerethSettings.get());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof SaerethSigilItem) {
            return InteractionResult.PASS;
        }
        if (hand != InteractionHand.MAIN_HAND || !held.isEmpty()) {
            return super.mobInteract(player, hand);
        }
        if (!level().isClientSide()) {
            player.displayClientMessage(Component.translatable("status.not_enough_sigils.saereth." + status.key()).withStyle(status.color()), false);
            if (chestPos != null && arrayPos != null) {
                player.displayClientMessage(Component.translatable("status.not_enough_sigils.saereth.links",
                        chestPos.toShortString(), arrayPos.toShortString()).withStyle(ChatFormatting.GRAY), false);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    public ItemStack captureInto(ItemStack sigil) {
        if (level() instanceof ServerLevel serverLevel && phase != Phase.IDLE) {
            abort(serverLevel);
        }
        CompoundTag tag = new CompoundTag();
        saveWithoutId(tag);
        SaerethSigilItem.storeCaptured(sigil, tag);
        discard();
        return sigil;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (owner != null) {
            tag.putUUID("SaerethOwner", owner);
        }
        tag.putString("SaerethOwnerName", ownerName);
        if (chestPos != null) {
            tag.putLong("SaerethChest", chestPos.asLong());
        }
        if (arrayPos != null) {
            tag.putLong("SaerethArray", arrayPos.asLong());
        }
        tag.putString("SaerethPhase", phase.name());
        tag.putInt("SaerethPhaseTicks", phaseTicks);
        if (recipeId != null) {
            tag.putString("SaerethRecipe", recipeId.toString());
        }
        if (!carried.isEmpty()) {
            tag.put("SaerethCarried", carried.save(registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        owner = tag.hasUUID("SaerethOwner") ? tag.getUUID("SaerethOwner") : null;
        ownerName = tag.getString("SaerethOwnerName");
        chestPos = tag.contains("SaerethChest") ? BlockPos.of(tag.getLong("SaerethChest")) : null;
        arrayPos = tag.contains("SaerethArray") ? BlockPos.of(tag.getLong("SaerethArray")) : null;
        try {
            phase = Phase.valueOf(tag.getString("SaerethPhase"));
        } catch (IllegalArgumentException exception) {
            phase = Phase.IDLE;
        }
        phaseTicks = tag.getInt("SaerethPhaseTicks");
        recipeId = tag.contains("SaerethRecipe") ? ResourceLocation.tryParse(tag.getString("SaerethRecipe")) : null;
        carried = tag.contains("SaerethCarried") ? ItemStack.parseOptional(registryAccess(), tag.getCompound("SaerethCarried")) : ItemStack.EMPTY;
        status = chestPos != null && arrayPos != null ? Status.READY : Status.NOT_LINKED;
        if (phase != Phase.IDLE && recipeId == null) {
            phase = Phase.IDLE;
        }
    }

    public enum Action {
        IDLE,
        DRAW,
        PLACE,
        CAST
    }

    private enum Phase {
        IDLE,
        DRAW,
        PLACE_BASE,
        PLACE_ADDED,
        CAST
    }

    private enum Status {
        NOT_LINKED("not_linked", ChatFormatting.YELLOW),
        READY("ready", ChatFormatting.GREEN),
        WORKING("working", ChatFormatting.LIGHT_PURPLE),
        NO_ASH("no_ash", ChatFormatting.RED),
        NO_RECIPE("no_recipe", ChatFormatting.GRAY),
        CHEST_MISSING("chest_missing", ChatFormatting.RED),
        CHEST_FULL("chest_full", ChatFormatting.RED),
        TOO_FAR("too_far", ChatFormatting.RED),
        UNLOADED("unloaded", ChatFormatting.GRAY),
        BLOCKED("blocked", ChatFormatting.RED);

        private final String key;
        private final ChatFormatting color;

        Status(String key, ChatFormatting color) {
            this.key = key;
            this.color = color;
        }

        public String key() {
            return key;
        }

        public ChatFormatting color() {
            return color;
        }
    }
}
