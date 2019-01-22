package cm.study.rpc.server.v1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger ILOG = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            ILOG.info("receive message: {}", buf.toString(Charset.defaultCharset()));

            final ByteBuf resp = buf.alloc().buffer(16);
            long current = System.currentTimeMillis();
            ILOG.info("resp message: {}", current);
            resp.writeLong(current);
            ctx.writeAndFlush(resp);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ILOG.error("server exception: ", cause);
        ctx.close();
    }
}
