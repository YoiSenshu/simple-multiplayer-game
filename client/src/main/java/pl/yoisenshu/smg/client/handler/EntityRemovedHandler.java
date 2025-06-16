package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.world.ClientWorld;
import pl.yoisenshu.smg.network.packet.server.ServerEntityRemovedPacket;

@AllArgsConstructor
public class EntityRemovedHandler extends SimpleChannelInboundHandler<ServerEntityRemovedPacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerEntityRemovedPacket packet) {
        ClientWorld world = client.getCurrentWorld();
        System.out.println("[Remove] Got entity removed packet for entity ID: " + packet.getEntityId());
        if(world != null) {
            System.out.println("[Remove] Calling remove: " + packet.getEntityId());
            world.removeEntity(packet.getEntityId());
        }
    }
}
