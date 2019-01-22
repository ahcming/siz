package cm.study.rpc.util;

import cm.study.rpc.service.impl.UserApiImpl;
import cm.study.rpc.service.stub.User;
import cm.study.rpc.service.stub.UserApi;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.sql.Ref;

import static org.testng.Assert.*;

public class ReflectKitTest {

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testGetMethodId() throws Exception {
        Method saveUserMethod = UserApiImpl.class.getDeclaredMethod("saveUser", User.class);
        // cm.study.rpc.service.stub.UserApi.saveUser(cm.study.rpc.service.stub.User)
        // cm.study.rpc.service.stub.UserApi.saveUser(cm.study.rpc.service.stub.User)
        // cm.study.rpc.service.impl.UserApiImpl.saveUser(cm.study.rpc.service.stub.User)
        System.out.println("--> " + ReflectKit.getMethodId(saveUserMethod));
    }
}