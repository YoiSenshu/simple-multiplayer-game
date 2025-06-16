package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.server.entity.PlayerHandle;

@AllArgsConstructor
class ChatMessageHandler extends SimpleChannelInboundHandler<ClientChatMessagePacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientChatMessagePacket packet) {
        PlayerHandle player = server.players.get(ctx.channel());
        if(player == null) {
            return;
        }
        System.out.println("[" + player.getUsername() + "] > " + packet.getMessage());
        server.players.forEach((c, p) -> p.sendPacket(new ServerChatMessagePacket(player.getId(), player.getUsername() + " > " + packet.getMessage())));
    }
}
