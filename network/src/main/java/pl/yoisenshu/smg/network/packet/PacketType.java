package pl.yoisenshu.smg.network.packet;

import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.client.ClientChatMessagePacket;
import pl.yoisenshu.smg.network.packet.client.ClientDisconnectPacket;
import pl.yoisenshu.smg.network.packet.client.ClientLoginPacket;
import pl.yoisenshu.smg.network.packet.client.ClientMovePacket;
import pl.yoisenshu.smg.network.packet.client.ClientPingPacket;
import pl.yoisenshu.smg.network.packet.client.ClientPlaceBombPacket;
import pl.yoisenshu.smg.network.packet.client.ServerboundPacket;
import pl.yoisenshu.smg.network.packet.server.ClientboundPacket;
import pl.yoisenshu.smg.network.packet.server.ServerBombExplodedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerBombPlacedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerEntityRemovedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerDisconnectPacket;
import pl.yoisenshu.smg.network.packet.server.ServerLoginSuccessPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPingPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerJoinedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerLeftPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerMovePacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;

import java.util.function.Supplier;

public interface PacketType {

    byte getId();
    @NotNull Supplier<? extends Packet> getSupplier();

    enum Serverbound implements PacketType {
        LOGIN(1, ClientLoginPacket::new),
        PING(2, ClientPingPacket::new),
        DISCONNECT(3, ClientDisconnectPacket::new),
        CHAT_MESSAGE(4, ClientChatMessagePacket::new),
        PLAYER_MOVE(5, ClientMovePacket::new),
        PLACE_BOMB(6, ClientPlaceBombPacket::new);

        private final byte id;
        private final Supplier<ServerboundPacket> supplier;

        Serverbound(int id, @NotNull Supplier<ServerboundPacket> supplier) {
            this.id = (byte) id;
            this.supplier = supplier;
        }

        @NotNull
        public static Serverbound fromId(byte id) throws IllegalArgumentException {
            for (Serverbound packetType : values()) {
                if (packetType.id == id) {
                    return packetType;
                }
            }
            throw new IllegalArgumentException("Unknown packet id: " + id);
        }

        @Override
        public byte getId() {
            return id;
        }

        @Override
        public @NotNull Supplier<? extends Packet> getSupplier() {
            return supplier;
        }
    }

    enum Clientbound implements PacketType {
        LOGIN_SUCCESS(1, ServerLoginSuccessPacket::new),
        WORLD_DATA(2, ServerWorldDataPacket::new),
        PING(3, ServerPingPacket::new),
        DISCONNECT(4, ServerDisconnectPacket::new),
        CHAT_MESSAGE(5, ServerChatMessagePacket::new),
        PLAYER_JOINED(6, ServerPlayerJoinedPacket::new),
        PLAYER_LEFT(7, ServerPlayerLeftPacket::new),
        PLAYER_MOVE(8, ServerPlayerMovePacket::new),
        BOMB_PLACED(9, ServerBombPlacedPacket::new),
        BOMB_EXPLODED(10, ServerBombExplodedPacket::new),
        ENTITY_REMOVED(11, ServerEntityRemovedPacket::new);

        private final byte id;
        private final Supplier<ClientboundPacket> supplier;

        Clientbound(int id, @NotNull Supplier<ClientboundPacket> supplier) {
            this.id = (byte) id;
            this.supplier = supplier;
        }

        @NotNull
        public static Clientbound fromId(byte id) throws IllegalArgumentException {
            for (Clientbound packetType : values()) {
                if (packetType.id == id) {
                    return packetType;
                }
            }
            throw new IllegalArgumentException("Unknown packet id: " + id);
        }

        @Override
        public byte getId() {
            return id;
        }

        @Override
        public @NotNull Supplier<? extends Packet> getSupplier() {
            return supplier;
        }
    }
}
