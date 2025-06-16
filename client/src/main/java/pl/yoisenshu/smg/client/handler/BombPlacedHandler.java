package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.entity.ClientBomb;
import pl.yoisenshu.smg.client.world.ClientWorld;
import pl.yoisenshu.smg.network.packet.server.ServerBombPlacedPacket;

@AllArgsConstructor
public class BombPlacedHandler extends SimpleChannelInboundHandler<ServerBombPlacedPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerBombPlacedPacket packet) {
        ClientWorld world = client.getCurrentWorld();
        if(world != null) {
            world.addEntity(new ClientBomb(
                packet.getEntityId(),
                world,
                packet.getPosition(),
                packet.getFuseTime()
            ));
        }
    }
}
