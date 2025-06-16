package pl.yoisenshu.smg.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.entity.EntityView;
import pl.yoisenshu.smg.player.PlayerView;

import java.util.Set;

public interface WorldView {

    @NotNull String getName();

    @NotNull Set<? extends EntityView> getEntities();

    @Nullable EntityView getEntityById(int entityId);

    @NotNull Set<? extends PlayerView> getPlayers();
}
