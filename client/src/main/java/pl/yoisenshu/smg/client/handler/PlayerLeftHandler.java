package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.world.ClientWorld;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerLeftPacket;

@AllArgsConstructor
public class PlayerLeftHandler extends SimpleChannelInboundHandler<ServerPlayerLeftPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerPlayerLeftPacket packet) {
        ClientWorld world = client.getCurrentWorld();
        if(world != null) {
            world.removePlayer(packet.getEntityId());
        }
    }
}
