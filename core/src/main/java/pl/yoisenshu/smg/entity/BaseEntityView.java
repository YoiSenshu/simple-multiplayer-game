package pl.yoisenshu.smg.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;

public class BaseEntityView implements EntityView {

    protected final int entityId;
    protected Position position;
    protected boolean removed;

    public BaseEntityView(int entityId, @NotNull Position position) {
        this.entityId = entityId;
        this.position = position;
    }

    @Override
    public int getId() {
        return entityId;
    }

    @Override
    public @NotNull Position getPosition() {
        return position;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }
}
