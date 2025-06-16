package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.server.entity.Bomb;
import pl.yoisenshu.smg.network.packet.client.ClientPlaceBombPacket;
import pl.yoisenshu.smg.server.entity.Player;

@AllArgsConstructor
class PlayerBombPlaceHandler extends SimpleChannelInboundHandler<ClientPlaceBombPacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientPlaceBombPacket packet) {
        Player player = server.getPlayerByChannel(ctx.channel());
        if(player == null) {
            return;
        }

        if(!player.canPlaceBomb()) {
            return;
        }

        player.setBombPlaceCooldown(20);

        var bomb = new Bomb (
            player.getWorld(),
            packet.getPosition(),
            60,
            server
        );
        player.getWorld().addEntity(bomb);
    }
}
