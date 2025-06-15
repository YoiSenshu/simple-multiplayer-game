package pl.yoisenshu.smg.client.player;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;

public record ClientPlayerData (
    @NotNull String username,
    @NotNull Player.SkinColor skinColor,
    int playerEntityId,
    @NotNull Position playerPosition
) { }
