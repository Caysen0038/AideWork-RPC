package org.aidework.rpc.client;

import org.aidework.rpc.client.exception.ExceptionDispatcher;
import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.protocol.Protocol;
import org.aidework.rpc.core.protocol.RPCRequestProtocol;
import org.aidework.rpc.core.protocol.RPCResponseProtocol;

public class RPCClient {
    private String ip;
    private int port;
    private SocketClient client;
    private RPCResponseProtocol protocol;
    public RPCClient(String serverIp,int port){
        this.ip=serverIp;
        this.port=port;
        init();
    }
    private void init(){

    }
    public void launch(){

    }

    public Object request(RPCRequest request){
        return request(request,RPCClientContext.getRequestProtocol());
    }

    public <T> Object request(RPCRequest request,Protocol protocol){
        byte[] data=protocol.generate(request);
        byte[] res= RPCClientContext.getRpcClient().sendMessage(data);
        if(!request.isReturn()){
            return null;
        }
        Protocol<RPCResponse> resProto= RPCClientContext.getResponseProtocol();
        RPCResponse response=resProto.analysis(res);
        if(response.getCode()!=200){
            ExceptionDispatcher.dispatch(String.valueOf(response.getCode()));
            return null;
        }
        return response.getValue();
    }

    public void close(){
        if(client!=null){
            client.close();
        }
        client=null;
    }

    private byte[] sendMessage(byte[] data){
        client=new SocketClient(ip,port);
        byte[] res=client.send(data);
        client.close();
        return res;
    }


}
