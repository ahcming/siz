package cm.study.rpc.util;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class ZkUtilTest {

    private String zkPath = "localhost:2181";
    private String nodePath = "/api/registry/helloWorld";

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testAddNode() {
        String nodeValue = "127.0.0.1:8080";
        int rt = ZkUtil.addNode(zkPath, nodePath, nodeValue);
        System.out.println("--> " + rt);

        List<String> nodeValues = ZkUtil.getNodeValues(zkPath, nodePath);
        System.out.println(nodeValues);

    }

    @Test
    public void testGetNodeValues() {
        String nodeValue = "127.0.0.1:8081";
        boolean rt = ZkUtil.delNode(zkPath, nodePath, nodeValue);
        System.out.println("--> " + rt);
    }
}