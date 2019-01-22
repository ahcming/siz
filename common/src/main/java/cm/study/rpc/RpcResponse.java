package cm.study.rpc;

import java.io.Serializable;

/**
 * Rpc调用响应对象
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -7188276988309514802L;

    private boolean success;

    private Object result;

    private Throwable throwable;

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
               "success=" + success +
               ", result=" + result +
               ", throwable=" + throwable +
               '}';
    }
}
