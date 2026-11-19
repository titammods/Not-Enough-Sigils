package com.titammods.animus_extended.network;

import com.titammods.animus_extended.AnimusExtended;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record VeinSeekerPayload(List<BlockPos> positions, int duration) implements CustomPacketPayload {
    public static final int MAX_POSITIONS = 1024;

    public static final CustomPacketPayload.Type<VeinSeekerPayload> TYPE =
            new CustomPacketPayload.Type<>(AnimusExtended.rl("vein_seeker"));

    public static final StreamCodec<ByteBuf, VeinSeekerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_POSITIONS)), VeinSeekerPayload::positions,
                    ByteBufCodecs.VAR_INT, VeinSeekerPayload::duration,
                    VeinSeekerPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
