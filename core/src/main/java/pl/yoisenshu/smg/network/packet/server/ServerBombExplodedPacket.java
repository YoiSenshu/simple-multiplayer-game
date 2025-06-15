package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerBombExplodedPacket implements ClientboundPacket {

    private int entityId;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(entityId);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        this.entityId = in.readInt();
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.BOMB_EXPLODED;
    }
}
