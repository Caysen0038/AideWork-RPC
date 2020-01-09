package rpc.test.server.handler;

import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.server.request.handler.RequestHandler;
import rpc.test.core.protocol.CustomResponseProtocol;

import java.util.Arrays;

public class CustomDataHandler implements RequestHandler {
    @Override
    public String getName() {
        return "CUSTOM";
    }
    int i=0;
    @Override
    public byte[] execute(byte[] bytes) {
        RPCResponse response=new RPCResponse(200,"收到:"+i++);
        byte[] data=new CustomResponseProtocol().generate(response);
        return data;
    }
}
