package cm.study.rpc.util;

import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class ZkUtil {

    /**
     * 添加节点值
     * 如果节点不存在, 则创建之
     *
     * nodePath: /api/registry/${apiNameSpace}
     * ${nodeValue}
     */
    public static int addNode(String zkPath, String nodePath, String nodeValue) {
        ZkClient zkClient = new ZkClient(zkPath);
        String finalNodePath = String.format("%s/%s", nodePath, nodeValue);
        boolean existPath = zkClient.exists(finalNodePath);
        if (!existPath) {
            zkClient.createPersistent(finalNodePath, true);
            return 1;

        } else {
            return 0;
        }

    }

    public static boolean delNode(String zkPath, String nodePath, String nodeValue) {
        ZkClient zkClient = new ZkClient(zkPath);
        String finalNodePath = String.format("%s/%s", nodePath, nodeValue);
        return zkClient.delete(finalNodePath);
    }

    public static List<String> getNodeValues(String zkPath, String nodePath) {
        ZkClient zkClient = new ZkClient(zkPath);
        return zkClient.getChildren(nodePath);
    }
}
