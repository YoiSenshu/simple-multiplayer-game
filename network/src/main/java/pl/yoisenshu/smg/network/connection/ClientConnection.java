package pl.yoisenshu.smg.network.connection;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;

public class ClientConnection extends Connection {

    public ClientConnection(@NotNull Channel channel) {
        super(channel);
    }

    public void sendPacket(@NotNull ClientboundPacket packet) {
        super.sendPacket(packet);
    }

    @Override
    public void close() {
        System.out.println("[Server] Closing client connection to server.");
        super.close();
    }

    public Channel getChannel() {
        return channel;
    }
}
