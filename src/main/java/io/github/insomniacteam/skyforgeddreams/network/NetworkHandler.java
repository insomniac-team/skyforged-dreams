package io.github.insomniacteam.skyforgeddreams.network;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.worldstate.ClientEpochCache;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles network packet registration and synchronization.
 */
@EventBusSubscriber(modid = SkyforgedDreams.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SkyforgedDreams.MOD_ID)
                .versioned("1.0.0")
                .optional();

        registrar.playToClient(
                EpochSyncPacket.TYPE,
                EpochSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        NetworkHandler::handleEpochSyncClient,
                        null
                )
        );
    }

    /**
     * Client-side handler for epoch sync packets
     */
    private static void handleEpochSyncClient(EpochSyncPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            // Update the client cache with synced data
            ClientEpochCache.update(packet.getCurrentEpoch(), packet.getNextEpoch(), packet.progress());
        });
    }

    /**
     * Sends epoch sync packet to a specific player
     */
    public static void sendEpochSyncToPlayer(ServerPlayer player, EpochSyncPacket packet) {
        player.connection.send(packet);
    }

    /**
     * Sends epoch sync packet to all players
     */
    public static void sendEpochSyncToAll(Iterable<ServerPlayer> players, EpochSyncPacket packet) {
        for (ServerPlayer player : players) {
            sendEpochSyncToPlayer(player, packet);
        }
    }
}
