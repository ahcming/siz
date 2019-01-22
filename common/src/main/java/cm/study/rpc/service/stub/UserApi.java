package cm.study.rpc.service.stub;

public interface UserApi {

    User getUser(String id);

    int saveUser(User user);

    void updateName(String id, String newName);
}
