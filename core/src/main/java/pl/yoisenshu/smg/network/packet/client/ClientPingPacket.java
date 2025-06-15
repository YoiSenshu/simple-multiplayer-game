package pl.yoisenshu.smg.network.packet.client;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientPingPacket implements ServerboundPacket {

    private int pingId;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(pingId);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        pingId = in.readInt();
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Serverbound.PING;
    }
}
