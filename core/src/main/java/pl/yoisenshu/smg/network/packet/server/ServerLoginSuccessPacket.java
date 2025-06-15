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
public class ServerLoginSuccessPacket implements ClientboundPacket {

    private int playerEntityId;
    private Position position;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(playerEntityId);
        out.writeInt(position.x());
        out.writeInt(position.y());
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        playerEntityId = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        position = new Position(x, y);
    }

    @NotNull
    @Override
    public PacketType getType() {
        return PacketType.Clientbound.LOGIN_SUCCESS;
    }
}
