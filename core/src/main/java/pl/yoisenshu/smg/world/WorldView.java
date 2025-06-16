package pl.yoisenshu.smg.world;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.EntityView;
import pl.yoisenshu.smg.player.PlayerView;

import java.util.Set;

public interface WorldView {

    @NotNull String getName();

    @NotNull Set<? extends EntityView> getEntities();

    @NotNull Set<? extends PlayerView> getPlayers();
}
