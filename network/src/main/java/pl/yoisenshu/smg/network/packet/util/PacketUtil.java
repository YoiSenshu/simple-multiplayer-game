package pl.yoisenshu.smg.network.packet.util;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public final class PacketUtil {

    public static void writeString(@NotNull ByteBuf out, @NotNull String value) {
        byte[] bytes = value.getBytes();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    @NotNull
    public static String readString(@NotNull ByteBuf in) {
        int length = in.readInt();
        if (length < 0) {
            throw new IllegalArgumentException("String length cannot be negative: " + length);
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
