package pl.yoisenshu.smg.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final PacketBound bound;

    public PacketDecoder(@NotNull PacketBound bound) {
        this.bound = bound;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 5) return; // type (1) + length (4)

        in.markReaderIndex();
        int typeId = in.readByte();
        int length = in.readInt();

        //System.out.println(prefix() + "Decoding packet: typeId=" + typeId + ", length=" + length + ", bound=" + bound);

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        /*ByteBuf payload = in.readBytes(length);
        PacketType type;
        if(bound == PacketBound.SERVERBOUND) {
            type = PacketType.Serverbound.fromId((byte) typeId);
        } else {
            type = PacketType.Clientbound.fromId((byte) typeId);
        }
        Packet packet = createPacket(type);
        packet.decode(payload);
        out.add(packet);*/

        ByteBuf payload = in.readRetainedSlice(length);
        PacketType type;
        if(bound == PacketBound.SERVERBOUND) {
            type = PacketType.Serverbound.fromId((byte) typeId);
        } else {
            type = PacketType.Clientbound.fromId((byte) typeId);
        }
        Packet packet = createPacket(type);
        packet.decode(payload);
        out.add(packet);

        //System.out.println(prefix() + "Decoded packet: " + packet.getClass().getSimpleName() + ", bound=" + bound);
    }

    @SneakyThrows
    private Packet createPacket(@NotNull PacketType type) {
        return type.getSupplier().get();
    }

    public enum PacketBound {
        SERVERBOUND,
        CLIENTBOUND
    }

    private String prefix() {
        if(bound == PacketBound.SERVERBOUND) {
            return "[Server] ";
        } else {
            return "[Client] ";
        }
    }
}
