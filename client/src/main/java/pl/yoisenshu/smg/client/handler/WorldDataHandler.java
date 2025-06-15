package pl.yoisenshu.smg.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.ConnectingGameState;
import pl.yoisenshu.smg.client.GameState;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.network.packet.server.ServerLoginSuccessPacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;

public class WorldDataHandler extends SimpleChannelInboundHandler<ServerWorldDataPacket> {

    private final SimpleMultiplayerGameClient client;

    public WorldDataHandler(@NotNull SimpleMultiplayerGameClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerWorldDataPacket msg) {

        ServerConnection connection = client.getConnection();
        if(connection == null || !connection.isActive()) {
            throw new IllegalStateException("Connection is not active, cannot handle login success.");
        }

        GameState gameState = client.getCurrentGameState();

        if (!(gameState instanceof ConnectingGameState connectingGameState)) {
            System.err.println("Expected ConnectingGameState, but got: " + gameState.getClass().getSimpleName());
            throw new IllegalStateException("Expected ConnectingGameState, but got: " + gameState.getClass().getSimpleName());
        }

        connectingGameState.notifyWorldDownloaded(msg.getWorldName(), msg.getPlayers());
    }
}
