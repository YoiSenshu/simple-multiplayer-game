package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.world.Position;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerBombPlacedPacket implements ClientboundPacket {

    private int entityId;
    private Position position;
    private int millisToExplode;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(entityId);
        out.writeInt(position.x());
        out.writeInt(position.y());
        out.writeInt(millisToExplode);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        this.entityId = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        this.position = new Position(x, y);
        this.millisToExplode = in.readInt();
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.BOMB_PLACED;
    }
}
