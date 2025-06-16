package pl.yoisenshu.smg.client.world;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.client.SimpleMultiplayerGameClient;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.client.entity.ClientEntity;
import pl.yoisenshu.smg.client.player.ControllablePlayer;
import pl.yoisenshu.smg.client.player.ClientPlayerData;
import pl.yoisenshu.smg.client.player.ClientPlayer;
import pl.yoisenshu.smg.entity.EntityView;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientWorld implements WorldView {

    private final ServerConnection connection;
    private final String worldName;
    @Getter private final ControllablePlayer controllablePlayer;
    private final Map<Integer, ClientEntity> entities = new HashMap<>();
    private final Map<Integer, ClientPlayer> players = new HashMap<>();
    @Getter List<String> chatHistory = new ArrayList<>();

    public ClientWorld(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull ServerConnection connection,
        @NotNull WorldData worldData,
        @NotNull ClientPlayerData playerData
    ) {
        this.connection = connection;
        this.worldName = worldData.worldName();
        this.controllablePlayer = new ControllablePlayer(client, this, connection, playerData);
        players.put(controllablePlayer.getId(), controllablePlayer);
        addEntity(controllablePlayer);

        for (var player : worldData.players()) {
            if(player.entityId() == controllablePlayer.getId()) {
                continue;
            }
            addPlayer(
                player.entityId(),
                player.username(),
                player.position(),
                player.skinColor()
            );
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
            this,
            position,
            username,
            skinColor
        );
        addEntity(clientPlayer);
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
        var entity = entities.remove(entityId);
        if(entity instanceof PlayerView player && controllablePlayer != player) {
            players.remove(player.getId());
        }
    }

    @Override
    public @NotNull String getName() {
        return worldName;
    }

    @Override
    public @NotNull Set<ClientEntity> getEntities() {
        return Set.copyOf(entities.values());
    }

    @Nullable
    @Override
    public EntityView getEntityById(int entityId) {
        return entities.get(entityId);
    }

    @NotNull
    @Override
    public Set<ClientPlayer> getPlayers() {
        return Set.copyOf(players.values());
    }
}
