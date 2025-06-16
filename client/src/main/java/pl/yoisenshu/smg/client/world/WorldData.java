package pl.yoisenshu.smg.client.world;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;
import pl.yoisenshu.smg.world.Position;

import java.util.Set;

public record WorldData(
    @NotNull String worldName,
    @NotNull Set<ServerWorldDataPacket.PlayerData> players,
    int playerEntityId,
    @NotNull Position playerPosition
) {}
