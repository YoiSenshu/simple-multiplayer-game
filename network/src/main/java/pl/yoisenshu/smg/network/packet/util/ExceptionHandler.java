package pl.yoisenshu.smg.network.packet.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;

public final class ExceptionHandler extends SimpleChannelInboundHandler<Object> {

    private final String prefix;

    public ExceptionHandler(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("[" + prefix + "] Exception caught: " + cause.getMessage());
        cause.printStackTrace(System.err);
    }
}
