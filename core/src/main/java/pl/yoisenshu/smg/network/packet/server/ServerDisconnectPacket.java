package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.network.packet.PacketType;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisconnectPacket implements ClientboundPacket {

    @Nullable private String reason;

    @Override
    public void encode(@NotNull ByteBuf out) {
        if (reason == null) {
            reason = "Disconnected.";
        }
        byte[] reasonBytes = reason.getBytes();
        out.writeInt(reasonBytes.length);
        out.writeBytes(reasonBytes);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        int reasonLength = in.readInt();
        byte[] reasonBytes = new byte[reasonLength];
        in.readBytes(reasonBytes);
        reason = new String(reasonBytes, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.DISCONNECT;
    }
}
