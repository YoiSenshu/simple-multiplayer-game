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
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

public class ControllablePlayer extends ClientPlayer {

    private final SimpleMultiplayerGameClient client;
    private final ServerConnection connection;

    public ControllablePlayer(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull WorldView world,
        @NotNull ServerConnection connection,
        @NotNull ClientPlayerData playerData
    ) {
        super(
            playerData.playerEntityId(),
            world,
            playerData.playerPosition(),
            playerData.username(),
            playerData.skinColor()
        );
        this.client = client;
        this.connection = connection;
        this.position = playerData.playerPosition();
    }


    public void move(@NotNull Position newPosition) {
        position = newPosition;
        connection.sendPacket(new ClientMovePacket(newPosition));
    }

    private void sendPacket(@NotNull ServerboundPacket packet) {
        connection.sendPacket(packet);
    }

    private void sendPacketSync(@NotNull ServerboundPacket packet) throws InterruptedException {
        connection.sendPacketSync(packet);
    }

    public void disconnect(@Nullable String reason) {
        try {
            sendPacketSync(new ClientDisconnectPacket(reason));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if(connection.isActive()) {
                connection.close();
                client.setConnection(null);
            }
        }
    }

    public void sendMessage(@NotNull String message) {
        sendPacket(new ClientChatMessagePacket(message));
    }

    public void placeBomb() {
        sendPacket(new ClientPlaceBombPacket(position));
    }
}
