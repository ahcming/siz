package cm.study.rpc.client;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static Logger ILOG = LoggerFactory.getLogger(ClientHandler.class);

    private ChannelHandlerContext context;

    // seq -> request
    public static ConcurrentMap<Long, RpcRequest> reqHolder = new ConcurrentHashMap<>();
    // seq -> response
    public static ConcurrentMap<Long, RpcResponse> respHolder = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.context = ctx;
        ILOG.warn("server connect, {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ILOG.warn("server disconnect, {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        RpcRequest request = reqHolder.get(msg.getAck());
        if(request != null) {
            respHolder.putIfAbsent(msg.getAck(), msg);
            synchronized (request) {
                request.notify();
            }
        }
    }

    // 同步
    public RpcResponse sendRequestSync(RpcRequest request, int timeout) {
        reqHolder.putIfAbsent(request.getSeq(), request);

        context.writeAndFlush(request);

        if (!respHolder.containsKey(request.getSeq())) {
            synchronized (request) {
                try {
                    ILOG.debug("wait server response...");
                    request.wait(timeout); // max wait time
                } catch (Exception e) {
                    ILOG.error("wait response error: {}", e);
                }
            }
        }

        // 删除掉request, response
        RpcResponse response = respHolder.remove(request.getSeq());
        reqHolder.remove(request.getSeq());

        return response == null? RpcResponse.Default : response;
    }

    // 异步
    public void sendRequestAsync(RpcRequest request) {
        context.writeAndFlush(request);
    }
}
