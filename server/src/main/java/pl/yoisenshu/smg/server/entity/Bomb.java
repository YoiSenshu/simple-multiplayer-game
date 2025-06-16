package pl.yoisenshu.smg.server.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import pl.yoisenshu.smg.entity.BombView;
import pl.yoisenshu.smg.network.packet.server.ServerBombExplodedPacket;
import pl.yoisenshu.smg.server.SimpleMultiplayerGameServer;
import pl.yoisenshu.smg.server.world.World;
import pl.yoisenshu.smg.world.Position;

@Slf4j
public class Bomb extends BaseEntity implements BombView {

    private static final float EXPLOSION_RADIUS = 250.0f;
    private static final float EXPLOSION_KNOCKBACK = 150.0f;

    private final SimpleMultiplayerGameServer server;
    @Getter private int fuseTime;
    private boolean exploded;

    public Bomb(
        @NotNull World world,
        @NotNull Position position,
        @Range(from = 0, to = Integer.MAX_VALUE) int fuseTime,
        @NotNull SimpleMultiplayerGameServer server
    ) {
        super(world, position);
        this.fuseTime = fuseTime;
        this.server = server;
    }

    @Override
    public void tick(long tick) {
        if(!exploded) {
            fuseTime--;
            if(fuseTime <= 0) {
                explode();
            }
        }
    }

    private void explode() {
        exploded = true;
        log.debug("Bomb {} exploded at position {}", entityId, position);

        var packet = new ServerBombExplodedPacket(entityId);

        for (Player player : server.getWorld().getPlayers()) {
            player.sendPacket(new ServerBombExplodedPacket(entityId));
            Position playerPosition = player.getPosition();
            float distance = position.distanceTo(playerPosition);
            if(distance <= EXPLOSION_RADIUS && distance > 0) {

                float x = (playerPosition.x() - position.x()) / distance;
                float y = (playerPosition.y() - position.y()) / distance;
                float knockbackPower = EXPLOSION_KNOCKBACK * (1 - (distance / EXPLOSION_RADIUS));
                Position newPosition = new Position(
                    (int) (playerPosition.x() + x * knockbackPower),
                    (int) (playerPosition.y() + y * knockbackPower)
                );

                log.debug("Player {} knocked back to position {}", player.getUsername(), newPosition);
                player.move(newPosition);
                player.sendPacket(packet);
            }
        }
        this.remove();
    }

    @Override
    public boolean isExploded() {
        return exploded;
    }
}
