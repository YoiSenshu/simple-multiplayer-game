package pl.yoisenshu.smg.network.connection;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;

@Slf4j
public class ClientConnection extends Connection {

    public ClientConnection(@NotNull Channel channel) {
        super(channel);
    }

    public void sendPacket(@NotNull ClientboundPacket packet) {
        super.sendPacket(packet);
    }

    @Override
    public void close() {
        log.debug("Closing client connection to server.");
        super.close();
    }

    public Channel getChannel() {
        return channel;
    }
}
