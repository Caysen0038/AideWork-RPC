package org.aidework.rpc.client.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aidework.core.helper.ReflectionHelper;
import org.aidework.rpc.client.RPCClientContext;
import org.aidework.rpc.client.RPCClient;
import org.aidework.rpc.core.annotation.RPCInterface;
import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.core.protocol.Protocol;
import org.aidework.rpc.core.protocol.RPCRequestProtocol;
import org.aidework.rpc.core.protocol.RPCResponseProtocol;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage service proxy
 * Container will take over all proxy's life cycle
 */
public class ProxyContainer {
    private Map<String,Object> proxyMap;
    public static final String ANNOTATION_NAME= RPCInterface.class.getName();
    private static String packagePath;
    private ProxyContainer(){
        proxyMap=new HashMap<>();
        init();
    }
    public static ProxyContainer builde(){
        return ProxyContainer.RPCServiceManagerHolder.instance;
    }

    private static class RPCServiceManagerHolder{
        private static ProxyContainer instance=new ProxyContainer();
    }
    private void init(){
    }

    /**
     * Register proxy to container and container will takc over proxy's life cycle
     * @param name
     * @param service
     */
    public void registerProxy(String name,Object service){
        registerProxy(name,service.getClass());
    }
    /**
     * Register proxy to container and container will takc over proxy's life cycle
     * @param name
     * @param service
     */
    public void registerProxy(String name,Class<?> service){
        // build dynamic proxy object
        proxyMap.put(name.toLowerCase(),getDynamicProxy(service));
    }

    /**
     * Remove specified proxy from container by name/
     * @param name
     */
    public void removeProxy(String name){
        proxyMap.remove(name.toLowerCase());
    }

    /**
     * Get specified proxy instance from container by name
     * @param name the proxy instance's name
     * @return the specified proxy instance if it exists or return null
     */
    public Object getProxy(String name){
        return proxyMap.get(name.toLowerCase());
    }

    public void destroy(){
        proxyMap.clear();
        proxyMap=null;
        ProxyContainer.RPCServiceManagerHolder.instance=null;
    }
    /**
     * Dynamic proxy object
     */
    private Object getDynamicProxy(Class<?> clazz){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor(){
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                RPCRequest bean=new RPCRequest();
                bean.setTargetService(clazz.getName());
                bean.setTargetMethod(method.getName());
                bean.setParams(objects);
                Type t=method.getReturnType();
                bean.setReturn(!t.getTypeName().equals("void"));
                return RPCClientContext.getRpcClient().request(bean);
            }
        });
        return enhancer.create();
    }
}
