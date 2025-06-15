package pl.yoisenshu.smg.client.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.Entity;
import pl.yoisenshu.smg.world.Position;

public abstract class RemoteEntity implements Entity {

    protected final int id;
    @NotNull protected Position position;
    private boolean removed;

    public RemoteEntity(int id, @NotNull Position position) {
        this.id = id;
        this.position = position;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull Position getPosition() {
        return position;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(@NotNull Position newPosition) {
        throw new UnsupportedOperationException();
    }

    public void updatePosition(@NotNull Position newPosition) {
        position = newPosition;
    }
}
