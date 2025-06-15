package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.entity.RemoteBomb;
import pl.yoisenshu.smg.client.world.RemoteWorld;
import pl.yoisenshu.smg.network.packet.server.ServerBombPlacedPacket;

@AllArgsConstructor
public class BombPlacedHandler extends SimpleChannelInboundHandler<ServerBombPlacedPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerBombPlacedPacket packet) {
        RemoteWorld world = client.getCurrentWorld();
        if(world != null) {
            world.addEntity(new RemoteBomb(
                packet.getEntityId(),
                packet.getPosition(),
                packet.getMillisToExplode()
            ));
        }
    }
}
