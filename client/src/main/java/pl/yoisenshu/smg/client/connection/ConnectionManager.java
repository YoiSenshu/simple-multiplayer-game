package pl.yoisenshu.smg.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Manages client connections to remote game servers.
 */
public class ConnectionManager {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final HandlerInitializer handlerInitializer;

    public ConnectionManager(@NotNull HandlerInitializer handlerInitializer) {
        this.group = new NioEventLoopGroup();
        this.handlerInitializer = handlerInitializer;

        this.bootstrap = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    }

    /**
     * Attempts to connect to a remote server at the given host and port.
     *
     * @param host remote address
     * @param port remote port
     * @return CompletableFuture that completes with a Connection or exceptionally on failure
     */
    public CompletableFuture<ServerConnection> connect(@NotNull String host, int port) {
        CompletableFuture<ServerConnection> future = new CompletableFuture<>();

        bootstrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                var pip = ch.pipeline();
                pip.addLast(new ConnectionHandler(future));
                handlerInitializer.initChannel(ch);
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(host, port);

        channelFuture.addListener((ChannelFutureListener) f -> {
            if (!f.isSuccess()) {
                future.completeExceptionally(f.cause());
            }
        });

        return future;
    }

    /**
     * Gracefully shuts down the connection manager and its event loop.
     */
    public void shutdown() {
        group.shutdownGracefully();
    }


    public interface HandlerInitializer {
        void initChannel(Channel channel);
    }
}

