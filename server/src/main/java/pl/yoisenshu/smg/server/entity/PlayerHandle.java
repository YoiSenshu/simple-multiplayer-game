package pl.yoisenshu.smg.server.entity;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.entity.BaseEntity;
import pl.yoisenshu.smg.network.connection.ClientConnection;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerDisconnectPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerMovePacket;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;

public class PlayerHandle extends BaseEntity implements Player {

    private final ClientConnection connection;
    private final String username;
    private final SkinColor skinColor;

    @Getter
    @Setter
    private int bombPlaceCooldown = 0;

    public PlayerHandle(
        int entityId,
        @NotNull Position position,
        @NotNull ClientConnection connection,
        @NotNull String username,
        @NotNull SkinColor skinColor
    ) {
        super(entityId, position);
        this.connection = connection;
        this.username = username;
        this.skinColor = skinColor;
    }

    public void sendPacket(@NotNull ClientboundPacket packet) {
        connection.sendPacket(packet);
    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull SkinColor getSkinColor() {
        return skinColor;
    }

    public boolean isOnline() {
        return connection.isActive();
    }

    public void disconnect(@Nullable String reason) {
        sendPacket(new ServerDisconnectPacket(reason));
        connection.close();
    }

    public void sendServerMessage(@NotNull String message) {
        sendPacket(new ServerChatMessagePacket(message));
    }

    public void sendPlayerMessage(@NotNull Player sender, @NotNull String message) {
        sendPacket(new ServerChatMessagePacket(sender.getId(), message));
    }

    @Override
    public void move(@NotNull Position newPosition) {
        super.move(newPosition);
        sendPacket(new ServerPlayerMovePacket(getId(), newPosition));
    }

    @Override
    public void tick() {
        bombPlaceCooldown--;
    }

    public void updatePosition(@NotNull Position newPosition) {
        super.move(newPosition);
    }

    public boolean canPlaceBomb() {
        return bombPlaceCooldown <= 0;
    }
}
