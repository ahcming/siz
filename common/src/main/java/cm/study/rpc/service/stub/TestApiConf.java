package cm.study.rpc.service.stub;

import cm.study.rpc.Config;
import cm.study.rpc.ConfigOptions;

public class TestApiConf {

    public static final Config clientConfig = Config.defaultClient()
            .set(ConfigOptions.ZkPath, "localhost:2181")
            .set(ConfigOptions.ApiNameSpace, "xxoo")
            .set(ConfigOptions.ServerPort, 8989)
            ;

    public static final Config serverConfig = Config.defaultServer()
            .set(ConfigOptions.ZkPath, "localhost:2181")
            .set(ConfigOptions.ApiNameSpace, "xxoo")
            .set(ConfigOptions.ServerPort, 8989)
            ;
}
