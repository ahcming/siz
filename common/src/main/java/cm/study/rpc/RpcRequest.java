package cm.study.rpc;

import java.io.Serializable;
import java.util.List;

/**
 * Rpc调用请求对象
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 85352635439493737L;

    // 请求序号
    private long seq;

    // 接口的完全限定名
    private String apiName;

    // 接口请求参数
    private List<Object> params;

    public RpcRequest() {
    }

    public RpcRequest(String apiName, List<Object> params) {
        this.apiName = apiName;
        this.params = params;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
               "seq=" + seq +
               ", apiName='" + apiName + '\'' +
               ", params=" + params +
               '}';
    }
}
