package pl.yoisenshu.smg.client;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.connection.ConnectionListener;
import pl.yoisenshu.smg.client.connection.ConnectionManager;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;

@Slf4j
class ClientGameManager implements ConnectionListener {

    private final ConnectionManager connectionManager;

    public ClientGameManager(@NotNull ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.connectionManager.addListener(this);
        log.debug("ClientGameManager initialized.");
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(@NotNull String reason) {

    }

    @Override
    public void onPacket(@NotNull ClientboundPacket packet) {

    }
}
