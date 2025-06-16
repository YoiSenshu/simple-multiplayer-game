package pl.yoisenshu.smg.client.connection.netty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.connection.ConnectionManager;
import pl.yoisenshu.smg.client.connection.ConnectionListener;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class NettyConnectionManager implements ConnectionManager {

    protected final List<ConnectionListener> listeners = new CopyOnWriteArrayList<>();

    protected Channel channel;
    protected EventLoopGroup group;

    @Override
    public void disconnect(@NotNull String reason) throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected.");
        }

        listeners.forEach(l -> l.onDisconnected(reason));
        try {
            if (channel != null) {
                channel.close().sync();
                channel = null;
            }
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void sendPacket(@NotNull ServerboundPacket packet) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected.");
        }
        channel.writeAndFlush(packet);
    }

    @Override
    public void sendPacketSync(@NotNull ServerboundPacket packet) throws InterruptedException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected.");
        }
        channel.writeAndFlush(packet).sync();
    }

    @Override
    public @NotNull CompletableFuture<Void> sendPacketAsync(@NotNull ServerboundPacket packet) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (!isConnected()) {
            future.completeExceptionally(new IllegalStateException("Not connected."));
            return future;
        }

        channel.writeAndFlush(packet).addListener((ChannelFutureListener) nettyFuture -> {
            if (nettyFuture.isSuccess()) {
                future.complete(null);
            } else {
                future.completeExceptionally(nettyFuture.cause());
            }
        });

        return future;
    }
    @Override
    public boolean isConnected() {
        return channel != null && channel.isOpen();
    }

    @Override
    public void addListener(@NotNull ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull ConnectionListener listener) {
        listeners.remove(listener);
    }

    protected class ClientHandler extends SimpleChannelInboundHandler<ClientboundPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ClientboundPacket msg) {
            listeners.forEach(l -> l.onPacket(msg));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            disconnect("Disconnected from server");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            disconnect("Exception: " + cause.getMessage());
        }
    }
}
