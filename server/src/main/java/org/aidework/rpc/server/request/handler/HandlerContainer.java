package org.aidework.rpc.server.request.handler;

import org.aidework.core.helper.ReflectionHelper;
import org.aidework.rpc.core.helper.SystemLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RequestHandler instance's container
 * Container will take over every instance's life cycle
 */
public class HandlerContainer {
    private Map<String, RequestHandler> executorMap;
    private HandlerContainer(){
        init();
    }

    private void init(){
        executorMap=new HashMap();
        RequestHandler exe=new RPCHandler();
        registerHandler(exe);
        exe=new ExceptionHandler();
        registerHandler(exe);
    }

    /**
     * Register RequestHandler instance
     * @param exe
     */
    public void registerHandler(RequestHandler exe){
        executorMap.put(exe.getName(),exe);
    }

    /**
     * Remove specified instance from container if it exists
     * @param name
     */
    public void removeHandler(String name){
        executorMap.remove(name);
    }

    /**
     * Find and return specified handler by name
     * @param name handler's name
     * @return the specified handler if it exists or return null
     */
    public RequestHandler findHandler(String name){
        return executorMap.get(name);
    }

    /**
     * Destroy container and all handler
     */
    public void destroy(){
        executorMap.clear();
        executorMap=null;
        HandlerContainerHolder.instance=null;
    }

    /**
     * Build a container instance.
     * Container is a singleton.
     * @return
     */
    public static HandlerContainer builde(){
        return HandlerContainerHolder.instance;
    }
    /**
     * Use java inner class character to build the singleton
     * One of inner class character:Inner class's field just be init when inner class first be invoked,
     *                              this character named [lazy load].
     *                              Not only that,this character make sure thread safe too.
     */
    private static class HandlerContainerHolder{
        private static HandlerContainer instance=new HandlerContainer();
    }


}
