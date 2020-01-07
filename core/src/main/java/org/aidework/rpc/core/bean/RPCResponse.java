package org.aidework.rpc.core.bean;

import java.io.Serializable;

/**
 * 消息回送数据模型
 */
public class RPCResponse implements Serializable {
    /**
     * 状态码
     * 为节省时间采用http状态码，不再单独设计
     */
    private int code;
    /**
     * 返回数据
     * 数据具体类型由调用方自行转换并控制类型安全
     */
    private Object value;
    public RPCResponse(int code, Object value){
        this.value=value;
        this.code=code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
