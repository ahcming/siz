package cm.study.rpc.server;

import cm.study.rpc.service.impl.UserApiImpl;
import cm.study.rpc.service.stub.User;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

public class ApiRouteTest {

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testInit() {
        ApiRoute route = new ApiRoute();
        route.init(new UserApiImpl());
        System.out.println(route.apiMethodMap);
        System.out.println(route.apiInstanceMap);
    }

    @Test
    public void testCall() {
        ApiRoute route = new ApiRoute();
        route.init(new UserApiImpl());

        User user = new User();
        user.setId("123456");
        user.setName("cm");

        Object result = route.call("public int cm.study.rpc.service.impl.UserApiImpl.saveUser(cm.study.rpc.service.stub.User)", Arrays.asList(user));
        System.out.println("--> " + result);

        result = route.call("public void cm.study.rpc.service.impl.UserApiImpl.updateName(java.lang.String,java.lang.String)", Arrays.asList("123456", "hello"));
        System.out.println("--> " + result);

        result = route.call("public cm.study.rpc.service.stub.User cm.study.rpc.service.impl.UserApiImpl.getUser(java.lang.String)", Arrays.asList("123456"));
        System.out.println("--> " + result);
    }
}