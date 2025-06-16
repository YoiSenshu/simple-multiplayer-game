package pl.yoisenshu.smg.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        ByteBuf payload = Unpooled.buffer();
        packet.encode(payload);
        out.writeByte(packet.getType().getId()); // TYPE
        out.writeInt(payload.readableBytes());   // LENGTH
        out.writeBytes(payload);                 // PAYLOAD
    }
}
