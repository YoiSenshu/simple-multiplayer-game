package pl.yoisenshu.smg.client.connection;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;

public interface ConnectionListener {

    void onConnected();

    void onDisconnected(@NotNull String reason);

    void onPacket(@NotNull ClientboundPacket packet);
}
