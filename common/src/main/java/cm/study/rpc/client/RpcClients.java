package cm.study.rpc.client;

import cm.study.rpc.Config;
import cm.study.rpc.ConfigOptions;
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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class RpcClients {

    private static Logger ILOG = LoggerFactory.getLogger(RpcClients.class);

    private Config clientConfig;

    public RpcClients(Config config) {
        clientConfig = config;
    }

    /**
     * 单例
     */
    public <T> T getService(Class<T> clazz) {
        T instance = (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clazz},
                (Object proxy, Method method, Object[] args) -> {
                    Object result = rpcInvoke(method, args);
                    return result;
                }
        );

        return instance;
    }

    public Object rpcInvoke(Method method, Object[] args) throws Throwable {
        ILOG.info("invoke params, method: {}, args: {}", method, args);

        List<Object> params = new ArrayList<>();
        for (Object object : args) {
            params.add(object);
        }

        RpcRequest request = new RpcRequest(ReflectKit.getMethodId(method), params);
        RpcResponse response = invoke(
                clientConfig.get(ConfigOptions.ServerHost),
                clientConfig.get(ConfigOptions.ServerPort),
                request,
                clientConfig.get(ConfigOptions.DataFormat),
                clientConfig.get(ConfigOptions.ClientTimeout));
        if (response.isSuccess()) {
            return response.getResult();
        } else {
            throw response.getThrowable();
        }
    }

    public static RpcResponse invoke(String endpoint, int port, RpcRequest request, ConfigOptions.DataFormats format, int timeout) {
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
                    if(format == ConfigOptions.DataFormats.Default) {
                        ch.pipeline()
                                .addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                                .addLast(new ObjectEncoder());
                    } else if(format == ConfigOptions.DataFormats.PB) {
                        ch.pipeline()
                                .addLast(new ResponsePbDecoder())
                                .addLast(new RequestPbEncoder());
                    }

                    ch.pipeline().addLast(invokeHandler);
                }
            });

            ChannelFuture future = bootstrap.connect(endpoint, port).sync();
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
