package cm.study.rpc.client.v3;

import cm.study.rpc.client.ClientHandler;
import cm.study.rpc.client.RpcClientCluster;
import cm.study.rpc.service.stub.TestApiConf;
import cm.study.rpc.service.stub.User;
import cm.study.rpc.service.stub.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientClusterDemo {

    private static Logger ILOG = LoggerFactory.getLogger(ClientClusterDemo.class);

    public static void main(String[] args) {
        RpcClientCluster rpcClientCluster = new RpcClientCluster();

        // 添加服务
        rpcClientCluster.appendApi(TestApiConf.clientConfig, new Class[]{UserApi.class});
        // 其它服务...
        // rpcClientCluster.appendApi(TestApiConf.clientConfig, new Class[]{XXXApi.class});

        rpcClientCluster.serverDiscovery();

        UserApi userApi = rpcClientCluster.of(UserApi.class);
        User user = new User();
        user.setName("cm");
        user.setAge(30);
        user.setMarry(false);
        user.setSex('M');
        user.setId("123456789");

        int rt = userApi.saveUser(user);
        ILOG.info("save user complete, rt: {}", rt);

        User fromRemote = userApi.getUser("123456789");
        ILOG.info("get user complete, rt: {}", fromRemote);

        userApi.updateName("123456789", "ljx");

        fromRemote = userApi.getUser("123456789");
        ILOG.info("get user complete, rt: {}", fromRemote);

        ILOG.info("holder size, {}, {}", ClientHandler.reqHolder.size(), ClientHandler.respHolder.size());
    }
}
