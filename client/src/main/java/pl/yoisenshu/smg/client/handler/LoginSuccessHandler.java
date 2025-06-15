package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.ConnectingGameState;
import pl.yoisenshu.smg.client.DisconnectedGameState;
import pl.yoisenshu.smg.client.GameState;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.network.packet.server.ServerLoginSuccessPacket;

public class LoginSuccessHandler extends SimpleChannelInboundHandler<ServerLoginSuccessPacket> {

    private final SimpleMultiplayerGameClient client;

    public LoginSuccessHandler(@NotNull SimpleMultiplayerGameClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLoginSuccessPacket msg) {
        ServerConnection connection = client.getConnection();
        if(connection == null || !connection.isActive()) {
            System.out.println("ServerLoginSuccessPacket received, but connection is not active.");
            return;
        }

        GameState gameState = client.getCurrentGameState();

        if (!(gameState instanceof ConnectingGameState connectingGameState)) {
            System.out.println("ServerLoginSuccessPacket received, but current game state is not ConnectingGameState. Current state: " + gameState.getClass().getSimpleName());
            return;
        }

        connectingGameState.notifyLogged(msg.getPlayerEntityId(), msg.getPosition());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(!(client.getCurrentGameState() instanceof DisconnectedGameState)) {
            System.out.println("Connection active: " + client.getConnection().isActive());
            client.changeGameState(new DisconnectedGameState(client, "Error: " + cause.getMessage()));
        }
        super.exceptionCaught(ctx, cause);
    }
}
