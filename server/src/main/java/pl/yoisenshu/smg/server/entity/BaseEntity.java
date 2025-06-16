package pl.yoisenshu.smg.server.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.BaseEntityView;
import pl.yoisenshu.smg.world.Position;

public abstract class BaseEntity extends BaseEntityView implements Entity {

    public BaseEntity(int id, @NotNull Position position) {
        super(id, position);
    }

    public void remove() {
        removed = true;
    }

    @Override
    public void move(@NotNull Position newPosition) {
        position = newPosition;
    }
}
