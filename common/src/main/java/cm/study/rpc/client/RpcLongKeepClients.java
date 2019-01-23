package cm.study.rpc.client;

import cm.study.rpc.Config;
import cm.study.rpc.RpcRequest;
import cm.study.rpc.RpcResponse;
import cm.study.rpc.util.IDGen;
import cm.study.rpc.util.ReflectKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class RpcLongKeepClients {

    private static Logger ILOG = LoggerFactory.getLogger(RpcLongKeepClients.class);

    private Config.Client clientConfig;
    public RpcLongKeepClients(Config.Client config) {
        clientConfig = config;
        // 与服务器建立长连接
        RpcConnections.custom().connect("test", clientConfig.endpoint, clientConfig.port, clientConfig.format);
    }

    /**
     * 单例
     */
    public <T> T of(Class<T> clazz) {
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
        long s = System.currentTimeMillis();

        List<Object> params = new ArrayList<>();
        for (Object object : args) {
            params.add(object);
        }

        RpcRequest request = new RpcRequest(ReflectKit.getMethodId(method), params);
        request.setSeq(IDGen.next(this.clientConfig.namespace));

        ClientHandler clientHandler = RpcConnections.custom().choose("test");
        ILOG.info("get socket channel: {}", clientHandler);

        Class<?> returnType = method.getReturnType();
        if(void.class == returnType) {
            clientHandler.sendRequestAsync(request);
            ILOG.info("async invoke params, method: {}, args: {}, cost: {} ms", method, args, (System.currentTimeMillis()-s));
            return null;

        } else {
            RpcResponse response = clientHandler.sendRequestSync(request, this.clientConfig.timeout);
            ILOG.info("sync invoke params, method: {}, args: {}, cost: {} ms", method, args, (System.currentTimeMillis()-s));
            if (response.isSuccess()) {
                return response.getResult();
            } else {
                throw response.getThrowable();
            }
        }
    }

}
