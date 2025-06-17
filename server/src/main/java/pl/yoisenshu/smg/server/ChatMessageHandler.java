package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.server.entity.Player;

@Slf4j
@AllArgsConstructor
class ChatMessageHandler extends SimpleChannelInboundHandler<ClientChatMessagePacket> {

    private final SimpleMultiplayerGameServer server;

    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ClientChatMessagePacket packet) {
        Player player = server.getPlayerByChannel(ctx.channel());
        if(player == null) {
            return;
        }
        log.info("[{}] > {}", player.getUsername(), packet.getMessage());
        server.getOnlinePlayers().forEach(p -> p.sendPacket(new ServerChatMessagePacket(player.getId(), player.getUsername() + " > " + packet.getMessage())));
    }
}
