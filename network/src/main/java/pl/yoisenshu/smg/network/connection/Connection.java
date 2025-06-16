package pl.yoisenshu.smg.network.connection;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.Packet;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;

public abstract class Connection {

    protected final Channel channel;

    public Connection(@NotNull Channel channel) {
        this.channel = channel;
    }

    public void close() {
        System.out.println("Closing connection...");
        channel.close();
    }

    protected void sendPacket(@NotNull Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void sendPacketSync(@NotNull Packet packet) throws InterruptedException {
        channel.writeAndFlush(packet).sync();
    }

    public boolean isActive() {
        return channel.isActive();
    }
}
