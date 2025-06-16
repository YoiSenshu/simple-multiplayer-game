package pl.yoisenshu.smg.client.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

import java.time.Instant;

@Getter
public class ClientBomb extends ClientEntity {

    private final Instant explosionTime;
    private boolean exploding = false;

    public ClientBomb(int id, @NotNull WorldView world, @NotNull Position position, int countdownMillis) {
        super(id, world, position);
        this.explosionTime = Instant.now().plusMillis(countdownMillis);
    }

    boolean isExploded() {
        return Instant.now().isAfter(explosionTime);
    }

    public void showExplosion() {
        exploding = true;
    }
}
