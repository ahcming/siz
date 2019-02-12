package cm.study.rpc;

/**
 * 服务配置选项
 * <p>
 * ServerXXXX: 服务端相关的配置
 * ClientXXXX: 客户端相关的配置
 * other: 公共配置
 */
public enum ConfigOptions {
    ServerHost,             // 服务提供者地址
    ServerPort,             // 服务提供者端口
    ServerParallel,         // 服务提供者并行处理数

    ClientTimeout,          // 客户端超时时间

    /**
     * 服务提供者注册的zk集群地址
     */
    ZkPath,

    /**
     * API的命名空间
     * 每个API提供者都对应一个唯一的命名空间,
     * API服务启动时, 会在zk注册一个节点, 节点名字就是apiNameSpace, 子结点就是每个服务提供者的host:port
     * 而服务对外公布就是ApiNameSpace
     * 客户端服务发现就是通过ZkPath+ApiNameSpace来获取当前可用的服务提供者列表
     */
    ApiNameSpace,

    /**
     * client与server之间的数据格式, 值参见<code>DataFormats</code>
     */
    DataFormat,

    ;

    /**
     * 数据传输格式
     */
    public enum DataFormats {
        PB,         // google protobuf format
        JSON,       // json format
        Default,    // Java Object serializer
        ;
    }


}
