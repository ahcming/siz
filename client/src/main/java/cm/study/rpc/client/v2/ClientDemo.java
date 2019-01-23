package cm.study.rpc.client.v2;

import cm.study.rpc.Config;
import cm.study.rpc.client.RpcClients;
import cm.study.rpc.service.stub.User;
import cm.study.rpc.service.stub.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDemo {

    private static Logger ILOG = LoggerFactory.getLogger(ClientDemo.class);

    public static void main(String[] args) {
        Config.Client config = new Config.Client();
        config.endpoint = "127.0.0.1";
        config.port = 8080;
        config.format = Config.DataFormat.PB;
        config.timeout = 300;
        RpcClients rpcClients = new RpcClients(config);

        UserApi userApi = rpcClients.getService(UserApi.class);
        User user = new User();
        user.setName("cm");
        user.setAge(30);
        user.setMarry(false);
        user.setSex('M');
        user.setId("123456789");

//        for(int i = 0; i < 10; i++) {
//            int rt = userApi.saveUser(user);
//            ILOG.info("save user complete, rt: {}", rt);
//        }

        userApi.updateName(user.getId(), "ljx");
//
//        User fromRemote = userApi.getUser("123456789");
//        ILOG.info("get user complete, rt: {}", fromRemote);
    }
}
