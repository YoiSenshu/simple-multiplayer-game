package pl.yoisenshu.smg.network.packet.client;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.network.packet.PacketType;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
public class ClientDisconnectPacket implements ServerboundPacket {

    private String reason;

    public ClientDisconnectPacket(@Nullable String reason) {
        this.reason = reason;
        if(reason == null) {
            this.reason = "Disconnected.";
        }
    }

    @Override
    public void encode(@NotNull ByteBuf out) {
        byte[] reasonBytes = reason.getBytes();
        out.writeInt(reasonBytes.length);
        out.writeBytes(reasonBytes);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        int reasonLength = in.readInt();
        byte[] reasonBytes = new byte[reasonLength];
        in.readBytes(reasonBytes);
        this.reason = new String(reasonBytes, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Serverbound.DISCONNECT;
    }
}
