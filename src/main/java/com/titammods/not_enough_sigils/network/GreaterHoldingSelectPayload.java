package com.titammods.not_enough_sigils.network;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GreaterHoldingSelectPayload(int selectedSlot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GreaterHoldingSelectPayload> TYPE =
            new CustomPacketPayload.Type<>(NotEnoughSigils.rl("greater_holding_select"));

    public static final StreamCodec<FriendlyByteBuf, GreaterHoldingSelectPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, GreaterHoldingSelectPayload::selectedSlot,
                    GreaterHoldingSelectPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
