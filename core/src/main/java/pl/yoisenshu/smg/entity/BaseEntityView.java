package pl.yoisenshu.smg.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

public class BaseEntityView implements EntityView {

    protected final int entityId;
    private WorldView world;
    protected Position position;
    protected boolean removed;

    public BaseEntityView(int entityId, @NotNull WorldView world, @NotNull Position position) {
        this.entityId = entityId;
        this.position = position;
        this.world = world;
    }

    @Override
    public int getId() {
        return entityId;
    }

    @NotNull
    @Override
    public WorldView getWorld() {
        return world;
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
