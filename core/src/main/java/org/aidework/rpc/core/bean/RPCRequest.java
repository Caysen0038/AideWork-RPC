package org.aidework.rpc.core.bean;

import java.io.Serializable;

/**
 * RPC请求数据模型
 */
public class RPCRequest implements Serializable {
    /**
     * 请求目标服务
     */
    private String targetService;
    /**
     * 请求目标方法
     */
    private String targetMethod;
    /**
     * 是否有返回值
     */
    private boolean isReturn;
    /**
     * 方法参数
     */
    private Object[] params;
    public RPCRequest(){}
    public RPCRequest(String targetClass, String targetMethod,
                      boolean isReturn, Object[] params){
        this.targetService=targetClass;
        this.targetMethod=targetMethod;
        this.isReturn=isReturn;
        this.params=params;
    }

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetClass) {
        this.targetService = targetClass;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public boolean isReturn() {
        return isReturn;
    }

    public void setReturn(boolean hasReturn) {
        this.isReturn = hasReturn;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
