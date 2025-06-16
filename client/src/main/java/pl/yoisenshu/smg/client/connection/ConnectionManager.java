package pl.yoisenshu.smg.client.connection;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;

import java.util.concurrent.CompletableFuture;

public interface ConnectionManager {

    @NotNull CompletableFuture<Void> connect() throws IllegalStateException;

    void disconnect(@NotNull String reason) throws IllegalStateException;

    void sendPacket(@NotNull ServerboundPacket packet) throws IllegalStateException;

    void sendPacketSync(@NotNull ServerboundPacket packet) throws IllegalStateException, InterruptedException;

    @NotNull CompletableFuture<Void> sendPacketAsync(@NotNull ServerboundPacket packet) throws IllegalStateException;

    boolean isConnected();

    // void update(); ???

    void addListener(@NotNull ConnectionListener listener);

    void removeListener(@NotNull ConnectionListener listener);
}
