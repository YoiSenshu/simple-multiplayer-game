package pl.yoisenshu.smg.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientLoginPacket;

class LoginHandler extends SimpleChannelInboundHandler<ClientLoginPacket> {

    private final PlayerLoginListener playerLoginListener;

    public LoginHandler(@NotNull PlayerLoginListener playerLoginListener) {
        this.playerLoginListener = playerLoginListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientLoginPacket msg) {
        playerLoginListener.onPlayerLogin(ctx, msg);
    }

    public interface PlayerLoginListener {
        void onPlayerLogin(ChannelHandlerContext ctx, ClientLoginPacket packet);
    }
}
