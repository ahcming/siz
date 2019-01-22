package cm.study.rpc.service.impl;

import cm.study.rpc.service.stub.User;
import cm.study.rpc.service.stub.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserApiImpl implements UserApi {

    private static Logger ILOG = LoggerFactory.getLogger(UserApiImpl.class);

    private ConcurrentMap<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public User getUser(String id) {
        User rt = userMap.get(id);
        ILOG.info("get user, id: {}, result: {}", id, rt);
        return rt;
    }

    @Override
    public int saveUser(User user) {
        User rt = userMap.putIfAbsent(user.getId(), user);
        ILOG.info("save user, user: {}, rt: {}", user, rt);
        return rt == null ? 1 : 0;
    }

    @Override
    public void updateName(String id, String newName) {
        User user = userMap.get(id);
        if (null != user) {
            user.setName(newName);
            saveUser(user);
        }
    }

}
