package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientMovePacket;
import pl.yoisenshu.smg.server.entity.Player;

@AllArgsConstructor
class PlayerMoveHandler extends SimpleChannelInboundHandler<ClientMovePacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientMovePacket packet) {
        Player player = server.getPlayerByChannel(ctx.channel());
        if(player == null) {
            return;
        }
        player.updatePosition(packet.getPosition());
    }
}
