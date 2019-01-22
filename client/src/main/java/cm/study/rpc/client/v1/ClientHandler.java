package cm.study.rpc.client.v1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger ILOG = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf req = ctx.alloc().buffer(4);
        req.writeCharSequence("Hello, rpc server!", Charset.defaultCharset());
        ctx.writeAndFlush(req);
        req = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf resp = (ByteBuf) msg;
        try {
            ILOG.info("read data from server: {}", resp.readLong());
            ctx.close();
        } finally {
            ReferenceCountUtil.release(resp);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ILOG.info("client exception:", cause);
        ctx.close();
    }
}
