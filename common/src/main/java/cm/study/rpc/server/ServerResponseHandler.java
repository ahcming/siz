package cm.study.rpc.server;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerResponseHandler extends ChannelInboundHandlerAdapter {

    private static Logger ILOG = LoggerFactory.getLogger(ServerResponseHandler.class);

    private ApiRoute apiRoute;

    public ServerResponseHandler(ApiRoute apiRoute) {
        this.apiRoute = apiRoute;
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
////        super.channelActive(ctx);
//        ILOG.info("channel active: {}", ctx);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
//        ILOG.info("channel inactive: {}", ctx);
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
////        super.channelReadComplete(ctx);
//        ctx.flush();
//        ILOG.info("channel read complete: {}", ctx.isRemoved());
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ILOG.info("receive request params: {}", msg);
        RpcRequest request = (RpcRequest) msg;
        try {
//            RpcRequest request = JSON.parseObject(reqData, RpcRequest.class);
            RpcResponse response = new RpcResponse();
            response.setAck(request.getSeq());

            try {
                Object result = apiRoute.call(request.getApiName(), request.getParams());
                response.setSuccess(true);
                response.setResult(result);

            } catch (Exception e) {
                ILOG.error("request invoke error, req: {}", request, e);
                response.setSuccess(false);
                response.setThrowable(e);
            }

            ctx.writeAndFlush(response);
            ILOG.warn("request handler complete, req: {}, resp: {}", request, response);

            ctx.close();

        } finally {
            ReferenceCountUtil.release(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ILOG.info("client exception:", cause);
        ctx.close();
    }


}
