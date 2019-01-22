package cm.study.rpc.server.v2;

import cm.study.rpc.server.RpcServers;
import cm.study.rpc.service.impl.UserApiImpl;
import cm.study.rpc.service.stub.UserApi;

public class ServerDemo {

    public static void main(String[] args) {
        RpcServers servers = new RpcServers(8080);
        servers.registry(UserApi.class, new UserApiImpl());

        servers.run();
    }
}
