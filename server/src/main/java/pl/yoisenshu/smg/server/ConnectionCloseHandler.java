package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ConnectionCloseHandler extends ChannelInboundHandlerAdapter {

    private final SimpleMultiplayerGameServer server;

    public ConnectionCloseHandler(SimpleMultiplayerGameServer server) {
        this.server = server;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        var player = server.getPlayerByChannel(ctx.channel());

        if (player != null) {
            player.disconnect("Connection closed.");
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("[Server] Unexpected exception: " + cause.getMessage());
        ctx.close();
    }
}
