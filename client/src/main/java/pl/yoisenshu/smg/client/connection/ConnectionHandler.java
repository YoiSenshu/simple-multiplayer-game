package pl.yoisenshu.smg.client.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Deprecated
public class ConnectionHandler extends SimpleChannelInboundHandler<Object> {

    private final CompletableFuture<ServerConnection> connectionFuture;
    @Nullable private ServerConnection connection;

    public ConnectionHandler(CompletableFuture<ServerConnection> connectionFuture) {
        this.connectionFuture = connectionFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connection = new ServerConnection(ctx.channel());
        connectionFuture.complete(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("[Client] Disconnected from server.");
        /*if(connection != null) {
            connection.notifyDisconnection();
        }*/
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectionFuture.completeExceptionally(cause);
    }
}
