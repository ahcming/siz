package cm.study.rpc.client.v2;

import cm.study.rpc.Config;
import cm.study.rpc.client.ClientHandler;
import cm.study.rpc.client.RpcLongKeepClients;
import cm.study.rpc.service.stub.User;
import cm.study.rpc.service.stub.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongKeepClientDemo {

    private static Logger ILOG = LoggerFactory.getLogger(LongKeepClientDemo.class);

    public static void main(String[] args) {
        Config.Client config = new Config.Client();
        config.endpoint = "127.0.0.1";
        config.port = 8080;
        config.format = Config.DataFormat.PB;
        config.timeout = 3000;
        // client
        RpcLongKeepClients rpcClients = new RpcLongKeepClients(config);

        UserApi userApi = rpcClients.of(UserApi.class);
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
