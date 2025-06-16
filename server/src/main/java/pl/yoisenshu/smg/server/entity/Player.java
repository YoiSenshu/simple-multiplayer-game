package pl.yoisenshu.smg.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.connection.ClientConnection;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerDisconnectPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerMovePacket;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.server.world.World;
import pl.yoisenshu.smg.world.Position;

@Slf4j
public class Player extends BaseEntity implements PlayerView {

    @Getter
    private final ClientConnection connection;
    private final String username;
    private final SkinColor skinColor;

    @Getter
    @Setter
    private int bombPlaceCooldown = 0;

    public Player(
        @NotNull World world,
        @NotNull Position position,
        @NotNull ClientConnection connection,
        @NotNull String username,
        @NotNull SkinColor skinColor
    ) {
        super(world, position);
        this.connection = connection;
        this.username = username;
        this.skinColor = skinColor;
        log.debug("Player created: {} (ID: {})", username, getId());
    }

    public void sendPacket(@NotNull ClientboundPacket packet) {
        connection.sendPacket(packet);
    }

    @Override
    public void remove() {
        log.debug("Removing player: {} (ID: {})", username, getId());
        if(connection.isActive()) {
            connection.close();
        }
        super.remove();
    }

    @Override
    public void tick(long tick) {
        bombPlaceCooldown--;
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
    public void move(@NotNull Position newPosition) {
        updatePosition(newPosition);
        sendPacket(new ServerPlayerMovePacket(getId(), newPosition));
    }

    public boolean isOnline() {
        return connection.isActive();
    }

    public void disconnect(@NotNull String reason) {
        log.info("Player {} (ID: {}) disconnected: {}", username, getId(), reason);
        if(connection.isActive()) {
            sendPacket(new ServerDisconnectPacket(reason));
            connection.close();
        }
        remove();
    }

    public void sendServerMessage(@NotNull String message) {
        log.info("Server message to {} (ID: {}): {}", username, getId(), message);
        sendPacket(new ServerChatMessagePacket(message));
    }

    public void sendPlayerMessage(@NotNull PlayerView sender, @NotNull String message) {
        log.info("Message from {} (ID: {}) to {} (ID: {}): {}", sender.getUsername(), sender.getId(), username, getId(), message);
        sendPacket(new ServerChatMessagePacket(sender.getId(), message));
    }

    public void updatePosition(@NotNull Position newPosition) {
        super.move(newPosition);
        for (Player player : getWorld().getPlayers()) {
            player.sendPacket(new ServerPlayerMovePacket(entityId, position));
        }
    }

    public boolean canPlaceBomb() {
        return bombPlaceCooldown <= 0;
    }
}
