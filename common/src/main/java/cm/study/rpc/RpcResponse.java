package cm.study.rpc;

import cm.study.rpc.server.RpcException;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Rpc调用响应对象
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -7188276988309514802L;

    public static RpcResponse Default = new RpcResponse(0, false, null, new RpcException("请求超时"));

    // 响应号, 与请求号对应
    private long ack;

    private boolean success;

    private Object result;

    private Throwable throwable;

    public RpcResponse() {
    }

    public RpcResponse(long ack, boolean success, Object result, Throwable throwable) {
        this.ack = ack;
        this.success = success;
        this.result = result;
        this.throwable = throwable;
    }

    public long getAck() {
        return ack;
    }

    public void setAck(long ack) {
        this.ack = ack;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
               "ack=" + ack +
               ", success=" + success +
               ", result=" + result +
               ", throwable=" + throwable +
               '}';
    }
}
