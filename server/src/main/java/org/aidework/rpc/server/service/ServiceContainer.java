package org.aidework.rpc.server.service;

import org.aidework.core.helper.ReflectionHelper;
import org.aidework.rpc.core.annotation.RPCImplements;
import org.aidework.rpc.core.annotation.RPCInterface;
import org.aidework.rpc.core.helper.SystemLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC service's container.
 * Whatever any class be marked RPCImplements and implements RPCInterface will be registered this container.
 * Container will take over all service's life cycle
 */
public class ServiceContainer {
    private Map<String,Object> serviceMap;
    private ServiceContainer(){
        serviceMap=new HashMap<>();
        init();
    }

    /**
     * Builder container's singleton
     * @return
     */
    public static ServiceContainer builde(){
        return ServiceManagerHolder.instance;
    }
    private static class ServiceManagerHolder{
        private static ServiceContainer instance=new ServiceContainer();
    }

    private void init(){

    }

    /**
     * Register service's instance with specified name into container
     * @param name service's name
     * @param service services's instance
     */
    public void registerService(String name,Object service){
        serviceMap.put(name.toLowerCase(),service);
    }

    /**
     * Remove specified services's instance from container
     * @param name
     */
    public void removeService(String name){
        serviceMap.remove(name);
    }

    /**
     * Get all service's instance
     * @return
     */
    public Map<String,Object> getAllService(){
        return serviceMap;
    }

    /**
     * Get specified services's instance
     * @param name
     * @return
     */
    public Object getService(String name){
        return serviceMap.get(name.toLowerCase());
    }


}
