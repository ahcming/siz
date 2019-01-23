package cm.study.rpc.service.stub;

import cm.study.rpc.Config;

public interface UserApi {

    User getUser(String id);

    int saveUser(User user);

    void updateName(String id, String newName);

    Config.Client clientConfig = new Config.Client();

    Config.Server serverConfig = new Config.Server();
}
