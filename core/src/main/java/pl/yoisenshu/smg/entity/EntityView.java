package pl.yoisenshu.smg.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;

public interface EntityView {

    int getId();

    @NotNull Position getPosition();

    boolean isRemoved();
}
