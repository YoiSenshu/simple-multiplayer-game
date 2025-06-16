package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientDisconnectPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerLeftPacket;
import pl.yoisenshu.smg.player.Player;

@AllArgsConstructor
class DisconnectHandler extends SimpleChannelInboundHandler<ClientDisconnectPacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientDisconnectPacket msg) {
        var channel = ctx.channel();
        if(!server.players.containsKey(channel)) {
            return;
        }
        Player player = server.players.get(channel);
        server.players.remove(channel);
        server.entities.remove(player.getId());
        server.players.forEach((c, p) -> p.sendPacket(new ServerPlayerLeftPacket(player.getId())));

        System.out.println("[Server] Player " + player.getUsername() + " has disconnected. Reason:" + msg.getReason());
        server.broadcastMessage(player.getUsername() + " has disconnected.");
    }
}
