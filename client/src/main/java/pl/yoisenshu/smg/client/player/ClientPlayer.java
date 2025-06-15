package pl.yoisenshu.smg.client.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.network.packet.client.ClientChatMessagePacket;
import pl.yoisenshu.smg.network.packet.client.ClientDisconnectPacket;
import pl.yoisenshu.smg.network.packet.client.ClientMovePacket;
import pl.yoisenshu.smg.network.packet.client.ClientPlaceBombPacket;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;

public class ClientPlayer extends RemotePlayer implements Player {

    private final SimpleMultiplayerGameClient client;
    private final ServerConnection connection;

    public ClientPlayer(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull ServerConnection connection,
        @NotNull ClientPlayerData playerData
    ) {
        super(
            playerData.playerEntityId(),
            playerData.username(),
            playerData.playerPosition(),
            playerData.skinColor()
        );
        this.client = client;
        this.connection = connection;
        this.position = playerData.playerPosition();
    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull SkinColor getSkinColor() {
        return skinColor;
    }

    @Override
    public @NotNull Position getPosition() {
        return position;
    }

    @Override
    public boolean isRemoved() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(@NotNull Position newPosition) {
        position = newPosition;
        connection.sendPacket(new ClientMovePacket(newPosition));
    }

    public void sendPacket(@NotNull ServerboundPacket packet) {
        connection.sendPacket(packet);
    }

    public void disconnect(@Nullable String reason) {
        sendPacket(new ClientDisconnectPacket(reason));
        if(connection.isActive()) {
            connection.close();
            client.setConnection(null);
        }
    }

    public void sendMessage(@NotNull String message) {
        sendPacket(new ClientChatMessagePacket(message));
    }

    public void placeBomb() {
        sendPacket(new ClientPlaceBombPacket(position));
    }
}
