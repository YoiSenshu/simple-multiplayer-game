package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.world.RemoteWorld;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerJoinedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerMovePacket;

@AllArgsConstructor
public class PlayerJoinHandler extends SimpleChannelInboundHandler<ServerPlayerJoinedPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerPlayerJoinedPacket packet) {
        RemoteWorld world = client.getCurrentWorld();
        if(world != null) {
            world.addPlayer(
                packet.getEntityId(),
                packet.getUsername(),
                packet.getPosition(),
                packet.getSkinColor()
            );
        }
    }
}
