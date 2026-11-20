package com.titammods.not_enough_sigils.network;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.client.VeinSeekerClient;
import com.titammods.not_enough_sigils.holding.GreaterHoldingMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NotEnoughSigils.MODID);
        registrar.playToServer(
                GreaterHoldingSelectPayload.TYPE,
                GreaterHoldingSelectPayload.STREAM_CODEC,
                ModNetworking::handleGreaterHoldingSelect
        );
        registrar.playToClient(
                VeinSeekerPayload.TYPE,
                VeinSeekerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> VeinSeekerClient.receive(payload))
        );
    }

    public static void handleGreaterHoldingSelect(final GreaterHoldingSelectPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof GreaterHoldingMenu menu && menu.stillValid(context.player())) {
                menu.setSelectedSlot(payload.selectedSlot());
            }
        });
    }
}
