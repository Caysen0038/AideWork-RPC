package org.aidework.rpc.server.request.handler;


import org.aidework.rpc.core.annotation.RPCInterface;
import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.protocol.Protocol;
import org.aidework.rpc.core.protocol.RPCRequestProtocol;
import org.aidework.rpc.core.protocol.RPCResponseProtocol;
import org.aidework.rpc.server.RPCServerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC data executor
 * Execute data which target is RPC
 */
public class RPCHandler implements RequestHandler {
    public static final String NAME = "RPC";
    private static Protocol<RPCRequest> rpcProtocol;
    private static Protocol<RPCResponse> resProtocol;

    public RPCHandler() {
        rpcProtocol = RPCRequestProtocol.Builde();
        resProtocol = RPCResponseProtocol.Builde();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Standard execute data
     * @param data data from client
     * @return
     */
    @Override
    public byte[] execute(byte[] data) {
        RPCRequest bean = rpcProtocol.analysis(data);
        System.out.println("Request:["+bean.getTargetService()+"->"+bean.getTargetMethod()+"]");
        Class c = null;
        try {
            // get class instance by java reflection
            c = Class.forName(bean.getTargetService());
            // get name on the annotation by class instance
            String name = ((RPCInterface) c.getAnnotation(RPCInterface.class)).value();
            // get specified service from service container if it exists
            Object service = RPCServerContext.getService(name);
            Object res = null;
            // find and execute specified method
            for (Method m : c.getMethods()) {
                if (m.getName().equals(bean.getTargetMethod())) {
                    try{
                        if(m.getParameters().length==0){
                            res=m.invoke(service);
                        }else{
                            res = m.invoke(service, bean.getParams());
                        }
                    }catch (Exception e){
                        RPCResponse response = new RPCResponse(500,"Server exception");
                        byte[] result= RPCResponseProtocol.Builde().generate(response);
                        return result;
                    }
                }
            }
            // return null if the method has not return value
            if (!bean.isReturn()) {
                return null;
            }
            // build data by protocol and return data if the method has return value
            RPCResponse response=new RPCResponse(200,res);
            byte[] temp=resProtocol.generate(response);
            return temp;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            RPCResponse res = new RPCResponse(500,e.getMessage());
            byte[] temp = resProtocol.generate(res);
            return temp;
        }
        RPCResponse res = new RPCResponse(500,"server error unknown");
        byte[] temp = resProtocol.generate(res);
        return temp;
    }
}

