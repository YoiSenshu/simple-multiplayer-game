package pl.yoisenshu.smg.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.server.ServerBombExplodedPacket;
import pl.yoisenshu.smg.player.PlayerHandle;
import pl.yoisenshu.smg.server.SimpleMultiplayerGameServer;
import pl.yoisenshu.smg.world.Position;

public class Bomb extends BaseEntity {

    private static final float EXPLOSION_RADIUS = 250.0f;
    private static final float EXPLOSION_KNOCKBACK = 150.0f;

    private final SimpleMultiplayerGameServer server;
    @Getter
    private int ticksToExplode;

    public Bomb(
        int id,
        @NotNull Position position,
        int ticksToExplode,
        @NotNull SimpleMultiplayerGameServer server
    ) {
        super(id, position);
        this.ticksToExplode = ticksToExplode;
        this.server = server;
    }

    @Override
    public void tick() {

        if(ticksToExplode > 0) {
            ticksToExplode--;
            if (ticksToExplode <= 0) {
                explode();
            }
        }
    }

    private void explode() {
        for (PlayerHandle player : server.getPlayers().values()) {
            player.sendPacket(new ServerBombExplodedPacket(id));
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
}
