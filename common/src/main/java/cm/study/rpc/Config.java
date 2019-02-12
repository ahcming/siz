package cm.study.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个API服务的配置
 */
public class Config {

    public static String getServiceZkPath(String nameSpace) {
        return String.format("/api/registry/%s", nameSpace);
    }

    private Map<ConfigOptions, Object> settings = new HashMap<>();

    public Config set(ConfigOptions key, Object value) {
        settings.put(key, value);
        return this;
    }

    public <T> T get(ConfigOptions key) {
        return (T) settings.get(key);
    }

    /**
     * 默认的客户端配置
     */
    public static Config defaultClient() {
        Config config = new Config()
                .set(ConfigOptions.ZkPath, "localhost:2181")
                .set(ConfigOptions.ApiNameSpace, "rpc-service")
                .set(ConfigOptions.ServerHost, "127.0.0.1")
                .set(ConfigOptions.ServerPort, 8080)
                .set(ConfigOptions.ClientTimeout, 400)
                .set(ConfigOptions.DataFormat, ConfigOptions.DataFormats.PB)
                ;
        return config;
    }

    /**
     * 默认的服务端配置
     */
    public static Config defaultServer() {
        Config config = new Config()
                .set(ConfigOptions.ZkPath, "localhost:2181")
                .set(ConfigOptions.ApiNameSpace, "rpc-service")
                .set(ConfigOptions.ServerPort, 8080)
                .set(ConfigOptions.ServerParallel, 8)
                .set(ConfigOptions.DataFormat, ConfigOptions.DataFormats.PB)
                ;
        return config;
    }

}
