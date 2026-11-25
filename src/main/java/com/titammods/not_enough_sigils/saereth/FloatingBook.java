package com.titammods.not_enough_sigils.saereth;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FloatingBook {
    public static final String TAG = "not_enough_sigils.saereth_fx";

    private final Display.ItemDisplay display;

    private FloatingBook(Display.ItemDisplay display) {
        this.display = display;
    }

    public static FloatingBook spawn(ServerLevel level, ItemStack stack, Vec3 pos, float scale, int glowColor) {
        Display.ItemDisplay display = EntityType.ITEM_DISPLAY.create(level);
        if (display == null) {
            return null;
        }
        CompoundTag tag = display.saveWithoutId(new CompoundTag());
        tag.put("item", stack.copyWithCount(1).save(level.registryAccess()));
        tag.putString("billboard", "fixed");
        tag.putString("item_display", "fixed");
        tag.putInt("teleport_duration", 2);
        CompoundTag brightness = new CompoundTag();
        brightness.putInt("sky", 15);
        brightness.putInt("block", 15);
        tag.put("brightness", brightness);
        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floats(0.0F, 0.0F, 0.0F));
        transformation.put("left_rotation", floats(0.0F, 0.0F, 0.0F, 1.0F));
        transformation.put("scale", floats(scale, scale, scale));
        transformation.put("right_rotation", floats(0.0F, 0.0F, 0.0F, 1.0F));
        tag.put("transformation", transformation);
        if (glowColor >= 0) {
            tag.putInt("glow_color_override", glowColor);
        }
        display.load(tag);
        display.addTag(TAG);
        display.setGlowingTag(glowColor >= 0);
        display.moveTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);
        level.addFreshEntity(display);
        return new FloatingBook(display);
    }

    private static ListTag floats(float... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }

    public void move(Vec3 pos, float yaw, float pitch) {
        display.moveTo(pos.x, pos.y, pos.z, yaw, pitch);
    }

    public Vec3 position() {
        return display.position();
    }

    public boolean alive() {
        return display.isAlive();
    }

    public void remove() {
        display.discard();
    }
}
