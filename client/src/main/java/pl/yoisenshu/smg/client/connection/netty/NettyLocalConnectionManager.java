package pl.yoisenshu.smg.client.connection.netty;

import io.netty.channel.local.LocalAddress;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// TODO
class NettyLocalConnectionManager extends NettyConnectionManager {

    private final LocalAddress localAddress;

    public NettyLocalConnectionManager(@NotNull LocalAddress localAddress) {
        this.localAddress = localAddress;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> connect() throws IllegalStateException {
        return null;
    }
}
