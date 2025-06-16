package pl.yoisenshu.smg.client.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.BaseEntityView;
import pl.yoisenshu.smg.world.Position;

public abstract class ClientEntity extends BaseEntityView {

    public ClientEntity(int id, @NotNull Position position) {
        super(id, position);
    }

    public void onRemove() {
        removed = true;
    }

    public void onMove(@NotNull Position newPosition) {
        position = newPosition;
    }
}
