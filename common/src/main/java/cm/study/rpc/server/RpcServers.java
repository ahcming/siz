package cm.study.rpc.server;

import cm.study.rpc.codec.RequestPbDecoder;
import cm.study.rpc.codec.ResponsePbEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServers {

    private static Logger ILOG = LoggerFactory.getLogger(RpcServers.class);

    private int port;

    private ApiRoute apiRoute;

    public RpcServers(int port) {
        this.port = port;
        this.apiRoute = new ApiRoute();
    }

    public void registry(Object serviceInstance) {
        apiRoute.init(serviceInstance);
    }

    public <T> void registry(Class<T> iface, T instance) {
        apiRoute.bind(iface, instance);
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            /**
                             * 可通过tcp抓包来确认每种协议的长度及内容
                             * 命令: sudo tcpdump -i lo0  port 8080 -vv -X
                             */
                            socketChannel.pipeline()
//                                    .addLast(new ObjectDecoder(1024 * 1024,
//                                            ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
//                                    .addLast(new ObjectEncoder())
                                    .addLast(new RequestPbDecoder())
                                    .addLast(new ResponsePbEncoder())
                                    .addLast(new ServerHandler(apiRoute));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            ILOG.error("rpc server exception: ", e);

        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
