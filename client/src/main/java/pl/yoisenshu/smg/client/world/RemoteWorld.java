package pl.yoisenshu.smg.client.world;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.client.entity.RemoteEntity;
import pl.yoisenshu.smg.client.player.ClientPlayer;
import pl.yoisenshu.smg.client.player.ClientPlayerData;
import pl.yoisenshu.smg.client.player.RemotePlayer;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteWorld implements World {

    private ServerConnection connection;
    private String name;
    @Getter private final ClientPlayer clientPlayer;
    @Getter private final Map<Integer, RemoteEntity> entities = new HashMap<>();
    @Getter private final Map<Integer, RemotePlayer> players = new HashMap<>();
    @Getter List<String> chatHistory = new ArrayList<>();

    public RemoteWorld(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull ServerConnection connection,
        @NotNull RemoteWorldData initialWorldData,
        @NotNull ClientPlayerData playerData
    ) {
        this.connection = connection;
        this.name = initialWorldData.worldName();
        this.clientPlayer = new ClientPlayer(
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
            players.put(player.entityId(), new RemotePlayer(
                player.entityId(),
                player.username(),
                player.position(),
                player.skinColor()
            ));
        }
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    public void updatePlayerPosition(int entityId, @NotNull Position position) {
        RemotePlayer player = players.get(entityId);
        if (player != null) {
            player.move(position);
        }
    }

    public void addChatMessage(@NotNull String message) {
        chatHistory.add(message);
    }

    public void addPlayer(
        int entityId,
        @NotNull String username,
        @NotNull Position position,
        @NotNull Player.SkinColor skinColor
    ) {
        RemotePlayer remotePlayer = new RemotePlayer(
            entityId,
            username,
            position,
            skinColor
        );
        entities.put(entityId, remotePlayer);
        players.put(entityId, remotePlayer);
    }

    public void removePlayer(int entityId) {
        entities.remove(entityId);
        players.remove(entityId);
    }

    public void addEntity(RemoteEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(int entityId) {
        entities.remove(entityId);
    }
}
