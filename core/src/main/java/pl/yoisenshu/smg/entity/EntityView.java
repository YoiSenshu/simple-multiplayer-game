package pl.yoisenshu.smg.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.world.Position;
import pl.yoisenshu.smg.world.WorldView;

public interface EntityView {

    int getId();

    @NotNull WorldView getWorld();

    @NotNull Position getPosition();

    boolean isRemoved();
}
