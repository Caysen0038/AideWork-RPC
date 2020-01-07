package org.aidework.rpc.server.request.handler;

import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.protocol.RPCResponseProtocol;

/**
 * Exception data handler.
 * Including error data,incomplete data,illegal data.
 */
public class ExceptionHandler implements RequestHandler {
    public static final String NAME="Exception";
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] execute(byte[] data) {
        RPCResponse res = new RPCResponse(502,"Request gateway exception");
        byte[] temp = RPCResponseProtocol.Builde().generate(res);
        return temp;
    }
}
