package pl.yoisenshu.smg.client.connection;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.connection.Connection;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;

/**
 * Represents a connection to the server or an integrated server.
 */
public final class ServerConnection extends Connection {

    public ServerConnection(@NotNull Channel channel) {
        super(channel);
    }

    public void sendPacket(@NotNull ServerboundPacket packet) {
        super.sendPacket(packet);
    }
}
