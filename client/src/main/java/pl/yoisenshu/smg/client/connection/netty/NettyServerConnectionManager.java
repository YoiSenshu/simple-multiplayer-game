package pl.yoisenshu.smg.client.connection.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.connection.ConnectionListener;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyServerConnectionManager extends NettyConnectionManager {

    private final String host;
    private final int port;

    public NettyServerConnectionManager(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> connect() throws IllegalStateException {
        if(isConnected()) {
            throw new IllegalStateException("Already connected.");
        }

        CompletableFuture<Void> connectFuture = new CompletableFuture<>();
        group = new MultiThreadIoEventLoopGroup(0, NioIoHandler.newFactory());
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    ChannelPipeline p = channel.pipeline();
                    p.addLast(new ClientHandler());
                }
            });

        bootstrap.connect(new InetSocketAddress(host, port)).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()) {
                channel = future.channel();
                listeners.forEach(ConnectionListener::onConnected);
                connectFuture.complete(null);
            } else {
                group.shutdownGracefully();
                future.channel().close();
                connectFuture.completeExceptionally(future.cause());
            }
        });

        return connectFuture;
    }
}
