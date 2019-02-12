package cm.study.rpc.client;

import cm.study.rpc.ConfigOptions;
import cm.study.rpc.codec.RequestPbEncoder;
import cm.study.rpc.codec.ResponsePbDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RCP通信模块
 */
public class RpcConnections {

    private static Logger ILOG = LoggerFactory.getLogger(RpcConnections.class);

    private EventLoopGroup workGroup = new NioEventLoopGroup(4);

    // serviceId -> [serverNode: ClientHandler]
    private volatile ConcurrentMap<String, List<ClientHandler>> handlerConcurrentMap = new ConcurrentHashMap<>();

    private ExecutorService exec = Executors.newFixedThreadPool(2);

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();

    private static RpcConnections instance = null;

    public static RpcConnections custom() {
        if (instance == null) {
            synchronized (RpcConnections.class) {
                if (instance == null) {
                    instance = new RpcConnections();
                }
            }
        }

        return instance;
    }

    public void connect(String serverName, String endpoint, int port, ConfigOptions.DataFormats format) {
//        exec.submit(() -> {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    if (format == ConfigOptions.DataFormats.Default) {
                        ch.pipeline()
                                .addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                                .addLast(new ObjectEncoder());
                    } else if (format == ConfigOptions.DataFormats.PB) {
                        ch.pipeline()
                                .addLast(new ResponsePbDecoder())
                                .addLast(new RequestPbEncoder());
                    }

                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(endpoint, port);

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ChannelPipeline pipeline = future.channel().pipeline();
                        ClientHandler handler = pipeline.get(ClientHandler.class);
                        addHandler(serverName, handler);
                        ILOG.info("build long link to server complete!!! {}", future.channel().remoteAddress());
                    }
                }
            });

//        });
    }

    void addHandler(String serverName, ClientHandler channelHandler) {
        List<ClientHandler> handlers = handlerConcurrentMap.get(serverName);
        if (null == handlers) {
            handlers = new ArrayList<>();
            List<ClientHandler> oldHandlers = handlerConcurrentMap.putIfAbsent(serverName, handlers);
            if (oldHandlers != null) {
                handlers = oldHandlers;
            }
        }

        handlers.add(channelHandler);
        lock.lock();
        try {
            connected.signal();
        } finally {
            lock.unlock();
        }
    }

    public ClientHandler choose(String serviceId) {
        ILOG.info("start choose client handler for {}", serviceId);
        List<ClientHandler> handlers = handlerConcurrentMap.get(serviceId);
        if (null == handlers || handlers.size() == 0) {
            // wait
            lock.lock();
            try {
                connected.await(6000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                ILOG.error("condition wait error", e);
            } finally {
                lock.unlock();
            }

            handlers = handlerConcurrentMap.get(serviceId);
        }

        ILOG.info("finish choose client handler for {}", serviceId);
        int idx = RandomUtils.nextInt(0, handlers.size());  // 随便取一个可用的节点
        return handlers.get(idx);
    }

    public void shutdown() {
        workGroup.shutdownGracefully();
    }
}
