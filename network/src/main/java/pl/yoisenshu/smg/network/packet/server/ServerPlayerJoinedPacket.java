package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.network.packet.util.PacketUtil;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerPlayerJoinedPacket implements ClientboundPacket {

    private int entityId;
    private Position position;
    private String username;
    private PlayerView.SkinColor skinColor;

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(entityId);
        out.writeInt(position.x());
        out.writeInt(position.y());
        PacketUtil.writeString(out, username);
        out.writeInt(skinColor.ordinal());
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        entityId = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        position = new Position(x, y);
        username = PacketUtil.readString(in);
        skinColor = PlayerView.SkinColor.values()[in.readInt()];
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.PLAYER_JOINED;
    }
}
