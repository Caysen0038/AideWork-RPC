package org.aidework.rpc.core.protocol;

import org.aidework.rpc.core.bean.RPCRequest;

public abstract class RequestProtocol implements Protocol<RPCRequest> {
    public static final int HEADER_LENGTH=15;
    protected byte[] header=null;

    public RequestProtocol(){
        buildHeader();
    }
    /**
     * 请求数据包头
     * 0：标记是否由返回值，0为无，1为有
     * 1-4：标记数据包总大小，包含包头
     * 5-14：标记数据处理器标识
     * 无论继承实现何种协议，数据头都应按此实现
     * 请求分配器将按此包头识别并分配处理器
     */
    @Override
    public final byte[] getHeader() {
        synchronized (this){
            if(header==null){
                buildHeader();
            }
        }
        return header;
    }

    public abstract byte[] getTarget();

    private final void buildHeader(){
        header=new byte[HEADER_LENGTH];
        int n=5,m=15;
        byte[] target=getTarget();
        if(target.length<10){
            m=n+target.length;
        }
        for(;n<m;n++){
            header[n]=target[n-5];
        }
        for(;n<HEADER_LENGTH;n++){
            header[n]=0;
        }
    }
}
