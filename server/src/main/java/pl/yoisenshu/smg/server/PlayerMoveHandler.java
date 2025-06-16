package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientMovePacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerMovePacket;
import pl.yoisenshu.smg.server.entity.PlayerHandle;

@AllArgsConstructor
class PlayerMoveHandler extends SimpleChannelInboundHandler<ClientMovePacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientMovePacket packet) {
        PlayerHandle player = server.players.get(ctx.channel());
        if(player == null) {
            return;
        }
        player.updatePosition(packet.getPosition());
        server.players.forEach((c, p) -> {
            if(p.getId() != player.getId()) {
                p.sendPacket(new ServerPlayerMovePacket(player.getId(), player.getPosition()));
            }
        });
    }
}
