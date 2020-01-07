package org.aidework.rpc.server.request;

import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.core.protocol.Protocol;
import org.aidework.rpc.core.protocol.RPCResponseProtocol;
import org.aidework.rpc.core.protocol.ResponseProtocol;
import org.aidework.rpc.server.RPCServerContext;
import org.aidework.rpc.server.request.handler.RequestHandler;

/**
 * Client request dispatcher
 * Whatever any request will dispatch handler by this.
 * Whole data request handle scheme include data protocol,data handler and data model,
 *  protocol and handler are required in the scheme.
 * Protocol be invoke by handler,model just load data between client and server.
 */
public final class RequestDispatcher {
    public static final int HEADER_LENGTH= Protocol.HEADER_LENGTH;
    private static SystemLogger logger;

    /**
     * Resolve data and dispatch specified data handler normally.
     * If the specified data handler is not exists or data is illegal and so on,
     *  data will be ExceptionHandler execute and return to client error info.
     * @param data
     * @return
     */
    public static byte[] dispatch(byte[] data) {
        if(data.length<HEADER_LENGTH){
            RPCResponse res = new RPCResponse(404,"Not found such method");
            byte[] temp = RPCResponseProtocol.Builde().generate(res);
            return temp;
        }
        // get specified handler's name
        String name=new String(data,5,10).trim();
        RequestHandler requestHandler;
        if((requestHandler = RPCServerContext.getHandler(name))!=null){
            // invoke handler normally
            byte[] result=null;
            try{
                result=requestHandler.execute(data);
            }catch (Exception e){
                RPCResponse res = new RPCResponse(500,"Server exception");
                result= RPCResponseProtocol.Builde().generate(res);
            }finally {
                return result;
            }
        }else{
            // data is illegal or other situations,ExceptionHandler will take over data.
            requestHandler=RPCServerContext.getHandler("Exception");
            return requestHandler.execute(data);
        }
    }
}
