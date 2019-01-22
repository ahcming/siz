package cm.study.rpc.server;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ServerResponseHandler extends ChannelInboundHandlerAdapter {

    private static Logger ILOG = LoggerFactory.getLogger(ServerResponseHandler.class);

    private ApiRoute apiRoute;

    public ServerResponseHandler(ApiRoute apiRoute) {
        this.apiRoute = apiRoute;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf req = (ByteBuf) msg;
//        String reqData = req.toString(Charset.defaultCharset());
        ILOG.info("receive request params: {}", msg);
        RpcRequest request = (RpcRequest) msg;
        try {

//            RpcRequest request = JSON.parseObject(reqData, RpcRequest.class);
            RpcResponse response = new RpcResponse();

            try {
                Object result = apiRoute.call(request.getApiName(), request.getParams());
                response.setSuccess(true);
                response.setResult(result);

            } catch (Exception e) {
                ILOG.error("request invoke error, req: {}", request, e);
                response.setSuccess(false);
                response.setThrowable(e);
            }

//            ByteBuf resp = ctx.alloc().buffer(16);
//            resp.writeCharSequence(JSON.toJSONString(response), Charset.defaultCharset());
//            ctx.writeAndFlush(resp);
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
