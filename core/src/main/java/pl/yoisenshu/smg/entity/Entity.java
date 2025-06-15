package pl.yoisenshu.smg.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;

public interface Entity {

    int getId();

    @NotNull Position getPosition();

    boolean isRemoved();

    void remove();

    void move(@NotNull Position newPosition);
}
