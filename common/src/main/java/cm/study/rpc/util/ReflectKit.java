package cm.study.rpc.util;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Method;

public class ReflectKit {

    public static String getMethodId(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append(".").append(method.getName()).append("(");
        Class<?>[] types = method.getParameterTypes();
        boolean first = true;
        for (Class<?> type : types) {
            if (!first) {
                sb.append(",");
                first = false;
            }
            sb.append(type.getTypeName());
        }
        sb.append(")");
        return sb.toString();
    }

    public static Object parseValue(Object originValue, Class<?> type) {
//        return originValue;
        if (type.isPrimitive()) {
            return originValue;
        } else if (type == String.class) {
            return originValue;
        } else {
            return JSON.parseObject(JSON.toJSONString(originValue), type);
        }
    }
}
