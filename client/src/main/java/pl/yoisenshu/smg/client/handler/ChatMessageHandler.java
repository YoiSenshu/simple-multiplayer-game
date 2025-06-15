package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.world.RemoteWorld;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;

@AllArgsConstructor
public class ChatMessageHandler extends SimpleChannelInboundHandler<ServerChatMessagePacket> {

    private final SimpleMultiplayerGameClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerChatMessagePacket msg) {
        RemoteWorld world = client.getCurrentWorld();
        if(world != null) {
            world.addChatMessage(msg.getMessage());
        }
    }
}
