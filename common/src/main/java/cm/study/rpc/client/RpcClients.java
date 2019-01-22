package cm.study.rpc.client;

import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import cm.study.rpc.codec.RequestPbEncoder;
import cm.study.rpc.codec.ResponsePbDecoder;
import cm.study.rpc.util.ReflectKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class RpcClients {

    private static Logger ILOG = LoggerFactory.getLogger(RpcClients.class);

    private String endpoint;
    private int port;
    private int timeout;

    public RpcClients(String endpoint, int port, int timeout) {
        this.endpoint = endpoint;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * 单例
     */
    public <T> T getService(Class<T> clazz) {
        T instance = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcResponse response = rpcInvoke(method, args);
                if (response.isSuccess()) {
//                    Class<?> returnType = method.getReturnType();
//                    return ReflectKit.parseValue(response.getResult(), returnType);
                    return response.getResult();
                } else {
                    throw response.getThrowable();
                }
            }

        });

        return instance;
    }

    public RpcResponse rpcInvoke(Method method, Object[] args) {
        ILOG.info("invoke params, method: {}, args: {}", method, args);

        List<Object> params = new ArrayList<>();
        for (Object object : args) {
            params.add(object);
        }

        RpcRequest request = new RpcRequest(ReflectKit.getMethodId(method), params);
        RpcResponse response = null;
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ClientInvokeHandler invokeHandler = new ClientInvokeHandler(request);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
//                            .addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
//                            .addLast(new ObjectEncoder())
                            .addLast(new ResponsePbDecoder())
                            .addLast(new RequestPbEncoder())
                            .addLast(invokeHandler);
                }
            });

            ChannelFuture future = bootstrap.connect(this.endpoint, this.port).sync();
            future.channel().closeFuture().sync();

            response = invokeHandler.getResponse(timeout);
            ILOG.info("client request complete, {}", response);

        } catch (Exception e) {
            ILOG.error("client error:", e);
        } finally {
            workGroup.shutdownGracefully();
        }

        return response;
    }


}
