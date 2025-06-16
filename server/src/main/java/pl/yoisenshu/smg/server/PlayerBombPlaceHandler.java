package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.server.entity.Bomb;
import pl.yoisenshu.smg.network.packet.client.ClientPlaceBombPacket;
import pl.yoisenshu.smg.network.packet.server.ServerBombPlacedPacket;
import pl.yoisenshu.smg.server.entity.Player;

@AllArgsConstructor
class PlayerBombPlaceHandler extends SimpleChannelInboundHandler<ClientPlaceBombPacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientPlaceBombPacket packet) {
        Player player = server.players.get(ctx.channel());
        if(player == null) {
            return;
        }

        if(!player.canPlaceBomb()) {
            return;
        }

        player.setBombPlaceCooldown(20);
        int bombId = server.getNewEntityId();

        var bomb = new Bomb(
            bombId,
            packet.getPosition(),
            60,
            server
        );
        server.entities.put(bombId, bomb);

        server.players.forEach((c, p) -> p.sendPacket(new ServerBombPlacedPacket(bombId, bomb.getPosition(), bomb.getFuseTime() * 50)));
    }
}
