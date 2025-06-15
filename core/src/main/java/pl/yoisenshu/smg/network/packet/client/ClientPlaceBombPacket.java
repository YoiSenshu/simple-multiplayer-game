package pl.yoisenshu.smg.network.packet.client;

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
public class ClientPlaceBombPacket implements ServerboundPacket {

    private Position position;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(position.x());
        out.writeInt(position.y());
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        int x = in.readInt();
        int y = in.readInt();
        this.position = new Position(x, y);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Serverbound.PLACE_BOMB;
    }
}
