package pl.yoisenshu.smg.network.packet.server;

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
public class ServerChatMessagePacket implements ClientboundPacket {

    private int senderId; // ID of the player sending the message, -1 for server messages
    private String message;

    public ServerChatMessagePacket(String message) {
        this.senderId = -1;
        this.message = message;
    }

    public boolean isServerMessage() {
        return senderId == -1;
    }

    @Override
    public void encode(@NotNull ByteBuf out) {
        out.writeInt(senderId);
        byte[] messageBytes = message.getBytes();
        out.writeInt(messageBytes.length);
        out.writeBytes(messageBytes);
    }

    @Override
    public void decode(@NotNull ByteBuf in) {
        senderId = in.readInt();
        int length = in.readInt();
        byte[] messageBytes = new byte[length];
        in.readBytes(messageBytes);
        message = new String(messageBytes, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.CHAT_MESSAGE;
    }
}
