package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.entity.RemoteBomb;
import pl.yoisenshu.smg.client.world.RemoteWorld;
import pl.yoisenshu.smg.network.packet.server.ServerBombExplodedPacket;

@AllArgsConstructor
public class BombExplodedHandler extends SimpleChannelInboundHandler<ServerBombExplodedPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerBombExplodedPacket packet) {
        RemoteWorld world = client.getCurrentWorld();
        if(world != null) {
            var entity = world.getEntities().get(packet.getEntityId());
            if(entity instanceof RemoteBomb remoteBomb) {
                remoteBomb.showExplosion();
            }
        }
    }
}
