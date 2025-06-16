package pl.yoisenshu.smg.client.world;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.client.entity.ClientEntity;
import pl.yoisenshu.smg.client.player.ControllablePlayer;
import pl.yoisenshu.smg.client.player.ClientPlayerData;
import pl.yoisenshu.smg.client.player.ClientPlayer;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientWorld implements WorldView {

    private final ServerConnection connection;
    private final String worldName;
    @Getter private final ControllablePlayer clientPlayer;
    @Getter private final Map<Integer, ClientEntity> entities = new HashMap<>();
    @Getter private final Map<Integer, ClientPlayer> players = new HashMap<>();
    @Getter List<String> chatHistory = new ArrayList<>();

    public ClientWorld(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull ServerConnection connection,
        @NotNull RemoteWorldData initialWorldData,
        @NotNull ClientPlayerData playerData
    ) {
        this.connection = connection;
        this.worldName = initialWorldData.worldName();
        this.clientPlayer = new ControllablePlayer(
            client,
            connection,
            playerData
        );
        players.put(clientPlayer.getId(), clientPlayer);

        for (var player : initialWorldData.players()) {
            if(player.entityId() == clientPlayer.getId()) {
                players.put(player.entityId(), clientPlayer);
                continue;
            }
            players.put(player.entityId(), new ClientPlayer(
                player.entityId(),
                player.username(),
                player.position(),
                player.skinColor()
            ));
        }
    }

    public void updatePlayerPosition(int entityId, @NotNull Position position) {
        ClientPlayer player = players.get(entityId);
        if (player != null) {
            player.onMove(position);
        }
    }

    public void addChatMessage(@NotNull String message) {
        chatHistory.add(message);
    }

    public void addPlayer(
        int entityId,
        @NotNull String username,
        @NotNull Position position,
        @NotNull PlayerView.SkinColor skinColor
    ) {
        ClientPlayer clientPlayer = new ClientPlayer(
            entityId,
            username,
            position,
            skinColor
        );
        entities.put(entityId, clientPlayer);
        players.put(entityId, clientPlayer);
    }

    public void removePlayer(int entityId) {
        entities.remove(entityId);
        players.remove(entityId);
    }

    public void addEntity(ClientEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(int entityId) {
        entities.remove(entityId);
    }

    @Override
    public @NotNull String getName() {
        return worldName;
    }
}
