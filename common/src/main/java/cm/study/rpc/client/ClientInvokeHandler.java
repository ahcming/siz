package cm.study.rpc.client;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInvokeHandler extends ChannelInboundHandlerAdapter {

    private static Logger ILOG = LoggerFactory.getLogger(ClientInvokeHandler.class);

    private RpcRequest request;

    private RpcResponse response;

    private boolean complete;

    public ClientInvokeHandler(RpcRequest request) {
        this.request = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(request);
        ILOG.info("send message to server: {}", request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            synchronized (this) {
                response = (RpcResponse) msg;
                ILOG.info("read data from server: {}", response);
                complete = true;
                this.notify();
            }

            ctx.close();
        } finally {
            ReferenceCountUtil.release(response);
        }
    }

    public RpcResponse getResponse(int timeout) {
        long s = System.currentTimeMillis();
        synchronized (this) {
            if (this.complete == false) {
                try {
                    this.wait(timeout);
                } catch (Exception e) {
                    ILOG.error("wait error: ", e);
                }
            }
        }

        long cost = System.currentTimeMillis() - s;
        ILOG.debug("client wait server response, timeout: {}, cost: {}", timeout, cost);
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ILOG.info("client exception:", cause);
        ctx.close();
    }

}
