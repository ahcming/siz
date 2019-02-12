package cm.study.rpc.server.v3;

import cm.study.rpc.server.RpcServerCluster;
import cm.study.rpc.service.impl.UserApiImpl;
import cm.study.rpc.service.stub.TestApiConf;
import cm.study.rpc.service.stub.UserApi;

public class ServerClusterDemo {

    public static void main(String[] args) {
        RpcServerCluster serverCluster = new RpcServerCluster(TestApiConf.serverConfig);

        // 有多少服务,显示调用几次
        // 一般是通过注解来隐式调用
        serverCluster.bind(UserApi.class, new UserApiImpl());

        serverCluster.run();
    }
}
