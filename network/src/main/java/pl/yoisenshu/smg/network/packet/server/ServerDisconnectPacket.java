package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.network.packet.util.PacketUtil;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisconnectPacket implements ClientboundPacket {

    private String reason = "Disconnected.";

    @Override
    public void encode(@NotNull ByteBuf out) {
        PacketUtil.writeString(out, reason);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        reason = PacketUtil.readString(in);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.DISCONNECT;
    }
}
