package pl.yoisenshu.smg.network.packet.client;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.network.packet.util.PacketUtil;

@Getter
@NoArgsConstructor
public class ClientChatMessagePacket implements ServerboundPacket {

    private String message;

    public ClientChatMessagePacket(@NotNull String message) {
        this.message = message;
        if(message.length() > 100) {
            this.message = message.substring(0, 100);
        }
    }

    @Override
    public void encode(@NotNull ByteBuf out) {
        PacketUtil.writeString(out, message);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        this.message = PacketUtil.readString(in);
        if(message.length() > 100) {
            message = message.substring(0, 100);
        }
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Serverbound.CHAT_MESSAGE;
    }
}
