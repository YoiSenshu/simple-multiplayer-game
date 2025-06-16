package pl.yoisenshu.smg.server.entity;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.EntityView;
import pl.yoisenshu.smg.server.Tickable;
import pl.yoisenshu.smg.world.Position;

public interface Entity extends EntityView, Tickable {

    void remove();

    void move(@NotNull Position newPosition);
}
