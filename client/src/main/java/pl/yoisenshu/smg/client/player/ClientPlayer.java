package pl.yoisenshu.smg.client.player;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.entity.ClientEntity;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;

public class ClientPlayer extends ClientEntity implements PlayerView {

    protected final String username;
    protected final SkinColor skinColor;

    public ClientPlayer(
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
}
