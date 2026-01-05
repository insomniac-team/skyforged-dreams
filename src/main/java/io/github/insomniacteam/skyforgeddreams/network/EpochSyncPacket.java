package io.github.insomniacteam.skyforgeddreams.network;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Packet to sync epoch state from server to client.
 * Sends current epoch, next epoch, and progress percentage.
 */
public record EpochSyncPacket(int currentEpochOrdinal, int nextEpochOrdinal, int progress) implements CustomPacketPayload {

    public static final Type<EpochSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "epoch_sync")
    );

    public static final StreamCodec<ByteBuf, EpochSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            EpochSyncPacket::currentEpochOrdinal,
            ByteBufCodecs.VAR_INT,
            EpochSyncPacket::nextEpochOrdinal,
            ByteBufCodecs.VAR_INT,
            EpochSyncPacket::progress,
            EpochSyncPacket::new
    );

    public WorldEpoch getCurrentEpoch() {
        WorldEpoch[] values = WorldEpoch.values();
        if (currentEpochOrdinal >= 0 && currentEpochOrdinal < values.length) {
            return values[currentEpochOrdinal];
        }
        return WorldEpoch.WONDERS; // Fallback
    }

    public WorldEpoch getNextEpoch() {
        WorldEpoch[] values = WorldEpoch.values();
        if (nextEpochOrdinal >= 0 && nextEpochOrdinal < values.length) {
            return values[nextEpochOrdinal];
        }
        return WorldEpoch.SHADOWS; // Fallback
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
