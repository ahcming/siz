package cm.study.rpc;

public class Config {

    /**
     * API的命名空间
     * 每个API服务都对应一个唯一的命名空间
     * 比如order, user
     * API服务启动时, 会在zk注册一个节点, 节点名字就是apiNameSpace, 子结点就是每个服务提供者的host:port
     */
    public enum ApiNameSpace {

    }

    /**
     * 数据传输格式
     */
    public enum DataFormat {
        PB,         // google protobuf format
        JSON,       // json format
        Default,    // Java Object serializer
        ;
    }

    public static class Client {
        /**服务命名空间*/
        public String namespace = "rpc-service";

        public String endpoint = "127.0.0.1";

        public int port = 8080;

        public DataFormat format = DataFormat.Default;

        // 单位: 毫秒
        public int timeout = 300;
    }

    public static class Server {
        /**服务命名空间*/
        public String namespace = "rpc-service";

        public int port = 8080;

        public DataFormat format = DataFormat.Default;

        /**并行处理数*/
        public int parallel = 8;
    }

}
