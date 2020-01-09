package org.aidework.rpc.core.protocol;

import org.aidework.rpc.core.bean.RPCResponse;

public abstract class ResponseProtocol implements Protocol<RPCResponse> {
    public static final int HEADER_LENGTH=4;
    protected byte[] header=null;

    public ResponseProtocol(){
        buildHeader();
    }

    @Override
    public final byte[] getHeader(){
        synchronized (this){
            if(header==null){
                buildHeader();
            }
        }
        return header;
    }

    /**
     * 响应数据包头
     * 0-3：标识数据包大小，包含数据包头
     */
    private final void buildHeader(){
        header=new byte[HEADER_LENGTH];
    }
}
