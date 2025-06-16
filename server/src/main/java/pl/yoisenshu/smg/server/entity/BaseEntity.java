package pl.yoisenshu.smg.server.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.BaseEntityView;
import pl.yoisenshu.smg.server.world.World;
import pl.yoisenshu.smg.world.Position;

public abstract class BaseEntity extends BaseEntityView implements Entity {

    private static int nextId = 1;

    public BaseEntity(@NotNull World world, @NotNull Position position) {
        super(nextId++, world, position);
    }

    @Override
    public void remove() {
        removed = true;
    }

    @Override
    public void move(@NotNull Position newPosition) {
        position = newPosition;
    }

    @NotNull
    @Override
    public World getWorld() {
        return (World) super.getWorld();
    }
}
