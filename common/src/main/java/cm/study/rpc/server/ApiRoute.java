package cm.study.rpc.server;

import cm.study.rpc.util.ReflectKit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.IXOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRoute {

    private static Logger ILOG = LoggerFactory.getLogger(ApiRoute.class);

    Map<String, Method> apiMethodMap = new HashMap<>();
    Map<String, Object> apiInstanceMap = new HashMap<>();

    public void init(Object serviceInstance) {
        Method[] methods = serviceInstance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodId = ReflectKit.getMethodId(method);
            Method oldValue = apiMethodMap.putIfAbsent(methodId, method);
            if (oldValue != null) {
                ILOG.error("服务注册冲突: {}", methodId);
                throw new RpcException("服务注册冲突: " + methodId);
            }

            apiInstanceMap.putIfAbsent(methodId, serviceInstance);
        }
    }

    public <T> void bind(Class<T> iface, T impl) {
//        T wrapper = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iface}, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                return impl;
//            }
//        });

        Method[] methods = iface.getDeclaredMethods();
        for (Method method : methods) {
            String methodId = ReflectKit.getMethodId(method);
            Method oldValue = apiMethodMap.putIfAbsent(methodId, method);
            if (oldValue != null) {
                ILOG.error("服务注册冲突: {}", methodId);
                throw new RpcException("服务注册冲突: " + methodId);
            }

            apiInstanceMap.putIfAbsent(methodId, impl);
            ILOG.info("api注册成功, api: {}", methodId);
        }
    }

    public Object call(String apiName, List<Object> args) {
        Method targetMethod = apiMethodMap.get(apiName);
        if (null == targetMethod) {
            throw new RpcException("API不存在: " + apiName);
        }

        try {
            Object instance = apiInstanceMap.get(apiName);
            Object[] params = args.toArray(new Object[args.size()]);
//            Object[] params = new Object[args.size()];
//            Class<?>[] paramTypes = targetMethod.getParameterTypes();
//            for (int idx = 0; idx < args.size(); idx++) {
//                Class<?> paramType = paramTypes[idx];
//                Object originArg = args.get(idx);
//                if (paramType.isPrimitive()) {
//                    params[idx] = originArg;
//                } else {
//                    params[idx] = JSON.parseObject(JSON.toJSONString(originArg), paramType);
//                }
//            }

            Object result = targetMethod.invoke(instance, params);
            return result;
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

}
