package pl.yoisenshu.smg.network.packet;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet {

    int MAX_PACKET_LENGTH = 1024 * 1024; // 1 MB

    void encode(@NotNull ByteBuf out);
    void decode(@NotNull ByteBuf in);
    @NotNull PacketType getType();
}
