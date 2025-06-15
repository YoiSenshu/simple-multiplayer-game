package pl.yoisenshu.smg.network.packet;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet {

    void encode(@NotNull ByteBuf out);
    void decode(@NotNull ByteBuf in);
    @NotNull PacketType getType();
}
