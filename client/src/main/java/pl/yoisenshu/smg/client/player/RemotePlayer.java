package pl.yoisenshu.smg.client.player;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.entity.RemoteEntity;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;

public class RemotePlayer extends RemoteEntity implements Player {

    protected final String username;
    protected final SkinColor skinColor;

    public RemotePlayer (
        int entityId,
        @NotNull String username,
        @NotNull Position position,
        @NotNull SkinColor skinColor
    ) {
        super(entityId, position);
        this.username = username;
        this.skinColor = skinColor;
    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull SkinColor getSkinColor() {
        return skinColor;
    }

    @Override
    public @NotNull Position getPosition() {
        return position;
    }

    @Override
    public void move(@NotNull Position newPosition) {
        position = newPosition;
    }
}
