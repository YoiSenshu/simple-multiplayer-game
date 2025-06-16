package pl.yoisenshu.smg.client.player;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;

public record ClientPlayerData (
    @NotNull String username,
    @NotNull PlayerView.SkinColor skinColor,
    int playerEntityId,
    @NotNull Position playerPosition
) { }
