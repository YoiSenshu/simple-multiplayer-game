package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerLeftPacket;

class ConnectionCloseHandler extends ChannelInboundHandlerAdapter {

    private final SimpleMultiplayerGameServer server;

    public ConnectionCloseHandler(SimpleMultiplayerGameServer server) {
        this.server = server;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        var channel = ctx.channel();
        var player = server.players.remove(channel);

        if (player != null) {
            server.entities.remove(player.getId());

            server.players.forEach((c, p) -> {
                p.sendPacket(new ServerPlayerLeftPacket(player.getId()));
            });

            server.broadcastMessage("Player " + player.getUsername() + " has left the game.");
            System.out.println("[Server] Player disconnected (without disconnect packet): " + player.getUsername());
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("[Server] Unexpected exception: " + cause.getMessage());
        ctx.close();
    }
}
