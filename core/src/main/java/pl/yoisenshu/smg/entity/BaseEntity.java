package pl.yoisenshu.smg.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;

public abstract class BaseEntity implements Entity {

    protected int id;
    protected Position position;
    @Getter private boolean removed = false;

    public BaseEntity(int id, @NotNull Position position) {
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

    public void remove() {
        removed = true;
    }

    @Override
    public void move(@NotNull Position newPosition) {
        position = newPosition;
    }

    public abstract void tick();
}
