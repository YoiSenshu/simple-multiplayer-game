package pl.yoisenshu.smg.server.world;

import io.netty.util.collection.IntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.entity.BombView;
import pl.yoisenshu.smg.network.packet.server.ServerBombPlacedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerEntityRemovedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;
import pl.yoisenshu.smg.server.Tickable;
import pl.yoisenshu.smg.server.entity.Entity;
import pl.yoisenshu.smg.server.entity.Player;
import pl.yoisenshu.smg.world.WorldView;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class World implements WorldView, Tickable {

    @Getter
    @Setter
    private String name;
    private final IntObjectHashMap<Entity> entities = new IntObjectHashMap<>();
    private final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();

    public World(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void tick(long tick) {

        for (Entity entity : entities.values()) {
            if(entity.isRemoved()) {
                entities.remove(entity.getId());
                log.debug("Removed entity with {} of type: {}", entity.getId(), entity.getClass().getName());
                if(entity instanceof Player player) {
                    players.remove(player.getId());
                }
                for (Player player : players.values()) {
                    if(player != entity) {
                        log.debug("Sending entity removed packet to player {} for entity {}", player.getUsername(), entity.getId());
                        player.sendPacket(new ServerEntityRemovedPacket(entity.getId()));
                    }
                }
            }
        }

        for (Entity entity : entities.values()) {
            entity.tick(tick);
        }
    }

    @Override
    public @NotNull Set<Entity> getEntities() {
        return Set.copyOf(entities.values());
    }

    @Nullable
    @Override
    public Entity getEntityById(int entityId) {
        return entities.get(entityId);
    }

    @Override
    public @NotNull Set<Player> getPlayers() {
        return Set.copyOf(players.values());
    }

    public void addEntity(Entity entity) throws IllegalArgumentException {
        if(entities.containsValue(entity)) {
            throw new IllegalArgumentException("Entity is already present in the world.");
        }
        if(entity.isRemoved()) {
            throw new IllegalArgumentException("Entity is already removed.");
        }
        entities.put(entity.getId(), entity);
        log.debug("Added entity with id {} of type: {}", entity.getId(), entity.getClass().getName());

        // packet
        if(entity instanceof BombView bomb) {
            var packet = new ServerBombPlacedPacket(bomb.getId(), bomb.getPosition(), bomb.getFuseTime());
            for (Player player : players.values()) {
                log.debug("Sending bomb placed packet to player {} for bomb {}", player.getUsername(), bomb.getId());
                player.sendPacket(packet);
            }
        }
    }

    public void addPlayer(@NotNull Player player) throws IllegalArgumentException {
        addEntity(player);
        if(players.containsKey(player.getId())) {
            throw new IllegalArgumentException("Player is already present in the world.");
        }
        players.put(player.getId(), player);
        log.debug("Added player with id {} and username: {}", player.getId(), player.getUsername());
        player.sendPacket(new ServerWorldDataPacket(
            name,
            players.values()
                .stream()
                .map(worldPlayer -> new ServerWorldDataPacket.PlayerData(
                    worldPlayer.getId(),
                    worldPlayer.getUsername(),
                    worldPlayer.getPosition(),
                    worldPlayer.getSkinColor()
                ))
                .collect(Collectors.toSet())
        ));
        log.debug("Sent world data packet to player {} for world {}", player.getUsername(), name);
    }
}
