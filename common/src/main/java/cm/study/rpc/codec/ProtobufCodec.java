package cm.study.rpc.codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 数据流转时序图
 *
 * Client                                               Server
 *    |                                                   |
 *    +>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+
 *    |                      Request                      |
 *    | RequestPbEncoder                 RequestPbDecoder |
 *    +---------------------------------------------------+
 *    |                      Response                     |
 *    | ResponsePbDecoder               ResponsePbEncoder |
 *    +<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<+
 *
 * Client端需要对request进行encode, response进行decode, Client要注册ResponsePbDecoder, RequestPbEncoder处理器
 * Server端需要对request进行decode, response进行encode, Server要注册RequestPbDecoder, ResponsePbEncoder处理器
 *
 */
public class ProtobufCodec {

    public static <T> byte[] serializer(T instance) {
        Class<T> clazz = (Class<T>) instance.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            return ProtobufIOUtil.toByteArray(instance, schema, buffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserializer(byte[] data, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            ProtobufIOUtil.mergeFrom(data, obj, schema);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
