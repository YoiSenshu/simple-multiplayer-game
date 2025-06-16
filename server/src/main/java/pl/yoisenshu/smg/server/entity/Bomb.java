package pl.yoisenshu.smg.server.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import pl.yoisenshu.smg.entity.BombView;
import pl.yoisenshu.smg.network.packet.server.ServerBombExplodedPacket;
import pl.yoisenshu.smg.server.SimpleMultiplayerGameServer;
import pl.yoisenshu.smg.world.Position;

public class Bomb extends BaseEntity implements BombView {

    private static final float EXPLOSION_RADIUS = 250.0f;
    private static final float EXPLOSION_KNOCKBACK = 150.0f;

    private final SimpleMultiplayerGameServer server;
    @Getter private int fuseTime;
    private boolean exploded;

    public Bomb(
        int id,
        @NotNull Position position,
        @Range(from = 0, to = Integer.MAX_VALUE) int fuseTime,
        @NotNull SimpleMultiplayerGameServer server
    ) {
        super(id, position);
        this.fuseTime = fuseTime;
        this.server = server;
    }

    @Override
    public void tick() {
        if(!exploded) {
            fuseTime--;
            if(fuseTime <= 0) {
                explode();
            }
        }
    }

    private void explode() {
        exploded = true;
        for (Player player : server.getPlayers().values()) {
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

                player.move(newPosition);
            }
        }
        this.remove();
    }

    @Override
    public boolean isExploded() {
        return false;
    }
}
