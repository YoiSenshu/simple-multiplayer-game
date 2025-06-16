package pl.yoisenshu.smg.network.packet.client;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientChatMessagePacket implements ServerboundPacket {

    private String message;

    @Override
    public void encode(@NotNull ByteBuf out) {
        byte[] messageBytes = message.getBytes();
        out.writeInt(messageBytes.length);
        out.writeBytes(messageBytes);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        int messageLength = in.readInt();
        byte[] reasonBytes = new byte[messageLength];
        in.readBytes(reasonBytes);
        this.message = new String(reasonBytes, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Serverbound.CHAT_MESSAGE;
    }
}
