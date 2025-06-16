package pl.yoisenshu.smg.network.packet.client;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.network.packet.util.PacketUtil;
import pl.yoisenshu.smg.player.PlayerView;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginPacket implements ServerboundPacket {

    private String username;
    private PlayerView.SkinColor skinColor;

    @NotNull
    @Override
    public PacketType getType() {
        return PacketType.Serverbound.LOGIN;
    }

    @Override
    public void encode(@NotNull ByteBuf out) {
        PacketUtil.writeString(out, username);
        out.writeInt(skinColor.ordinal());
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        this.username = PacketUtil.readString(in);
        this.skinColor = PlayerView.SkinColor.values()[in.readInt()];
    }
}
