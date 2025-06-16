package pl.yoisenshu.smg.server.world;

import io.netty.util.collection.IntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.server.Tickable;
import pl.yoisenshu.smg.server.entity.Entity;
import pl.yoisenshu.smg.server.entity.Player;
import pl.yoisenshu.smg.world.WorldView;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
                // TODO send a remove packet to all players
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

    @Override
    public @NotNull Set<Player> getPlayers() {
        return Set.copyOf(players.values());
    }
}
