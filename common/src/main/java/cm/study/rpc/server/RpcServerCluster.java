package cm.study.rpc.server;

import cm.study.rpc.Config;
import cm.study.rpc.ConfigOptions;
import cm.study.rpc.codec.RequestPbDecoder;
import cm.study.rpc.codec.ResponsePbEncoder;
import cm.study.rpc.util.NetUtil;
import cm.study.rpc.util.ZkUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerCluster {

    private static Logger ILOG = LoggerFactory.getLogger(RpcServerCluster.class);

    private Config serverConfig;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    private EventLoopGroup workGroup = new NioEventLoopGroup(4);

    private ApiRoute apiRoute;

    public RpcServerCluster(Config config) {
        serverConfig = config;
        apiRoute = new ApiRoute();
    }

    /**
     * 绑定服务
     */
    public <T> void bind(Class<T> iface, T instance) {
        apiRoute.bind(iface, instance);
    }

    /**
     * 服务注册到zk, 公布出去
     */
    public void registry() {
        // 连上zk
        // 创建一个在apiNameSpace下创建一个子结点
        String subNodeValue = String.format("%s:%s", NetUtil.localIp(), serverConfig.get(ConfigOptions.ServerPort));
        String nodePath = Config.getServiceZkPath(serverConfig.get(ConfigOptions.ApiNameSpace));
        ZkUtil.addNode(serverConfig.get(ConfigOptions.ZkPath), nodePath, subNodeValue);

        ILOG.info("service registry in zookeeper complete, zk: {}, node: {}, value: {}",
                serverConfig.get(ConfigOptions.ZkPath), nodePath, subNodeValue);
    }

    public void run() {
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
                            if(serverConfig.get(ConfigOptions.DataFormat) == ConfigOptions.DataFormats.Default) {
                                socketChannel.pipeline()
                                        .addLast(new ObjectDecoder(1024 * 1024,
                                                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                        .addLast(new ObjectEncoder());

                            } else if(serverConfig.get(ConfigOptions.DataFormat) == ConfigOptions.DataFormats.PB) {
                                socketChannel.pipeline()
                                        .addLast(new RequestPbDecoder())
                                        .addLast(new ResponsePbEncoder());
                            }

                            socketChannel.pipeline()
                                    .addLast(new ServerHandler(apiRoute));

                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            int bindPort = serverConfig.get(ConfigOptions.ServerPort);
            ChannelFuture future = bootstrap.bind(bindPort).sync();

            registry();

            future.channel().closeFuture().sync();

        } catch (Exception e) {
            ILOG.error("rpc server exception: ", e);
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
