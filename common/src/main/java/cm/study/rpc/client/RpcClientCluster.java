package cm.study.rpc.client;

import cm.study.rpc.Config;
import cm.study.rpc.ConfigOptions;
import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import cm.study.rpc.util.IDGen;
import cm.study.rpc.util.ReflectKit;
import cm.study.rpc.util.ZkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcClientCluster {

    private static Logger ILOG = LoggerFactory.getLogger(RpcClientCluster.class);

    private Map<Class<?>, Config> clientConfigs;

    public RpcClientCluster() {
        clientConfigs = new HashMap<>();

    }

    // 添加服务
    public void appendApi(Config apiConf, Class<?>[] apis) {
        // 通过zkPath + namespace 找到所有服务可以结点, 添加到RpcConnections里
        for(Class<?> clazz : apis) {
            clientConfigs.putIfAbsent(clazz, apiConf);
        }

    }

    /**
     * 服务发现
     */
    public void serverDiscovery() {
        for (Config config : clientConfigs.values()) {
            String nameSpace = config.get(ConfigOptions.ApiNameSpace);
            String nodePath = Config.getServiceZkPath(nameSpace);
            List<String> serverNodes = ZkUtil.getNodeValues(config.get(ConfigOptions.ZkPath), nodePath);
            ILOG.info("server discovery, {} -> {}", nameSpace, serverNodes);

            for (String serverNode : serverNodes) {
                String[] info = StringUtils.split(serverNode, ":");
                RpcConnections.custom().connect(nameSpace, info[0], NumberUtils.toInt(info[1]), config.get(ConfigOptions.DataFormat));
            }
        }
    }

    /**
     * 单例
     */
    public <T> T of(Class<T> clazz) {
        T instance = (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clazz},
                (Object proxy, Method method, Object[] args) -> {
                    Config config = clientConfigs.get(clazz);
                    Object result = rpcInvoke(method, args, config);
                    return result;
                }
        );

        return instance;
    }

    public Object rpcInvoke(Method method, Object[] args, Config config) throws Throwable {
        long s = System.currentTimeMillis();

        List<Object> params = new ArrayList<>();
        for (Object object : args) {
            params.add(object);
        }

        RpcRequest request = new RpcRequest(ReflectKit.getMethodId(method), params);
        String apiNameSpace = config.get(ConfigOptions.ApiNameSpace);
        request.setSeq(IDGen.next(apiNameSpace));

        ClientHandler clientHandler = RpcConnections.custom().choose(apiNameSpace);
        ILOG.debug("get socket channel: {}", clientHandler);

        Class<?> returnType = method.getReturnType();
        if(void.class == returnType) {
            clientHandler.sendRequestAsync(request);
            ILOG.debug("async invoke params, method: {}, args: {}, cost: {} ms", method, args, (System.currentTimeMillis()-s));
            return null;

        } else {
            RpcResponse response = clientHandler.sendRequestSync(request, config.get(ConfigOptions.ClientTimeout));
            ILOG.debug("sync invoke params, method: {}, args: {}, cost: {} ms", method, args, (System.currentTimeMillis()-s));
            if (response.isSuccess()) {
                return response.getResult();
            } else {
                throw response.getThrowable();
            }
        }
    }

}
