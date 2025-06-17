package pl.yoisenshu.smg.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j
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

        log.debug("Decoding packet: {} (length: {})", typeId, length);

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        //ByteBuf payload = in.readRetainedSlice(length);
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
    }

    @SneakyThrows
    private Packet createPacket(@NotNull PacketType type) {
        return type.getSupplier().get();
    }

    public enum PacketBound {
        SERVERBOUND,
        CLIENTBOUND
    }
}
