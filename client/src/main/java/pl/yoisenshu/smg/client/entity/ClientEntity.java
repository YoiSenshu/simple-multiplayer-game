package pl.yoisenshu.smg.client.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.BaseEntityView;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

public abstract class ClientEntity extends BaseEntityView {

    public ClientEntity(int id, @NotNull WorldView world, @NotNull Position position) {
        super(id, world, position);
    }

    public void onRemove() {
        removed = true;
    }

    public void onMove(@NotNull Position newPosition) {
        position = newPosition;
    }
}
