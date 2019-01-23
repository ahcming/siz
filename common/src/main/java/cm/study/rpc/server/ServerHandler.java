package cm.study.rpc.server;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static Logger ILOG = LoggerFactory.getLogger(ServerHandler.class);

    private ApiRoute apiRoute;

    private ExecutorService exec = Executors.newFixedThreadPool(4);

    public ServerHandler(ApiRoute apiRoute) {
        this.apiRoute = apiRoute;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ILOG.warn("client connect, {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
        ILOG.warn("client disconnect, {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 多线程处理各个请求
        exec.submit(() -> {
            long s = System.currentTimeMillis();
            ILOG.debug("receive request params: {}", request);
            try {
                RpcResponse response = new RpcResponse();
                response.setAck(request.getSeq());

                try {
                    Object result = apiRoute.call(request.getApiName(), request.getParams());
                    response.setSuccess(true);
                    response.setResult(result);

                } catch (Exception e) {
                    response.setSuccess(false);
                    response.setThrowable(e);
                }

                ctx.writeAndFlush(response);
                long cost = System.currentTimeMillis() - s;
                ILOG.debug("request handler complete, req: {}, resp: {}, cost: {} ms", request, response, cost);

            } finally {
                ReferenceCountUtil.release(request);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ILOG.info("client exception:", cause);
        ctx.close();
    }


}
