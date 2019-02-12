package cm.study.rpc.service.stub;

import cm.study.rpc.Config;
import cm.study.rpc.ConfigOptions;

public interface UserApi {

    User getUser(String id);

    int saveUser(User user);

    void updateName(String id, String newName);

}
