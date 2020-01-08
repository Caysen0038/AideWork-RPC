package org.aidework.rpc.client;

import org.aidework.core.helper.PropertiesHelper;
import org.aidework.core.helper.ReflectionHelper;
import org.aidework.rpc.client.exception.ExceptionDispatcher;
import org.aidework.rpc.client.exception.ExceptionHandler;
import org.aidework.rpc.client.proxy.ProxyContainer;
import org.aidework.rpc.core.annotation.RPCInterface;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.core.protocol.*;
import org.aidework.rpc.core.util.ClassScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC client context container
 */
public class RPCClientContext {
    private Map<String,String> configMap;
    private ProxyContainer proxyContainer=null;
    private ExceptionDispatcher exceptionDispatcher;
    private RPCClient rpcClient;
    private RequestProtocol requestProtocol;
    private ResponseProtocol responseProtocol;
    private static SystemLogger logger;
    private static RPCClientContext instance;

    /**
     * Get the unique instance from the inner class
     * Use singleton mode by inner class character
     * One of character of inner class: Every field just be initialized when class be used
     */
    private static class RPCClientContextHolder{
        private static RPCClientContext instance=new RPCClientContext();
    }

    /**
     * Constructors are not allowed
     */
    private RPCClientContext(){

    }


    /**
     * Get unique RPCClient instance
     * @return
     */
    public static RPCClient getRpcClient() {
        return instance.rpcClient;
    }

    /**
     * Start RPC client
     */
    public static void start(){
        instance=RPCClientContextHolder.instance;
        logger.logNormal("Start RPC client");
        instance.initContext();
        instance.load();
        instance.rpcClient.launch();
        logger.logNormal("Start RPC client successful");
    }

    /**
     * Start RPC client by custom config's map
     */
    public static void start(Map<String,String> map){
        instance=RPCClientContextHolder.instance;
        logger.logNormal("Start RPC client");
        instance.initContext();
        instance.load();
        instance.rpcClient.launch();
        logger.logNormal("Start RPC client successful");
    }

    /**
     * Start RPC client by custom config's file path
     */
    public static void start(String path){
        instance=RPCClientContextHolder.instance;
        logger.logNormal("Start RPC client");
        instance.initContext(path);
        instance.load();
        instance.rpcClient.launch();
        logger.logNormal("Start RPC client successful");
    }

    /**
     * Close RPC client context
     */
    public static void shutdown(){
        instance.destroy();
    }

    /**
     * Get a specified service proxy from proxy container by name
     * @param name the proxy's name
     * @return return the specified proxy if it exists or return null
     */
    public static Object getProxy(String name)
    {
        return instance.proxyContainer.getProxy(name);
    }

    /**
     * Get specified request protocol to resolve data
     */
    public static RequestProtocol getRequestProtocol(){
        return instance.requestProtocol;
    }

    /**
     * Get specified response protocol to resolve data
     */
    public static ResponseProtocol getResponseProtocol(){
        return instance.responseProtocol;
    }

    /**
     * Init context by default config
     */
    private void initContext(){
        initContext("");
    }

    /**
     * Init context by costom config's path
     * @param path coder custom config's file path
     */
    private void initContext(String path){
        logger.logNormal("RPC context is being init");
        // load init config
        if(path==null || path.length()==0){
            path=RPCClientContext
                    .class
                    .getResource("/")
                    .getPath()
                    .replace("%20"," ")
                    +"aidework-config.properties";
        }
        Map<String,String> map=null;
        configMap=buildDefaultConfig();
        try{
            map= PropertiesHelper.read(path);
            if(map!=null){
                coverConfig(configMap,map);
            }
        }catch (Exception e){
            logger.logNormal("RPC context loaded default config because the config file is not found");
        }

    }

    /**
     * Init context by custom config's map
     * @param map coder custom config's map
     */
    private void initContext(Map<String,String> map){
        logger.logNormal("RPC context is being init");
        // load init config
        this.configMap=buildDefaultConfig();
        if(map!=null){
            coverConfig(configMap,map);
        }else{
            initContext();
        }

    }

    /**
     * Load context components
     */
    private void load(){
        proxyContainer= proxyContainer.builde();
        exceptionDispatcher=ExceptionDispatcher.build();
        rpcClient=new RPCClient(
                configMap.get("rpc.client.serverIp"),
                Integer.parseInt(configMap.get("rpc.client.port")));

        loadDependenceClass();
        loadProtocol();

    }

    /**
     * Load context necessary dependence from current classloader
     */
    private void loadDependenceClass(){
        SystemLogger.logNormal("Beginning to scan necessary dependent class");
        ClassScanner.scan();
        List<String> list = ClassScanner.getClassList();
        List<String> proxyList=new ArrayList<>();
        List<String> handlerList=new ArrayList<>();
        for(String str:list){
            Class c= null;
            try {
                c = Class.forName(str);
            } catch (ClassNotFoundException e) {
                continue;
            }catch (Exception e){
                continue;
            }
            RPCInterface s= (RPCInterface) c.getAnnotation(RPCInterface.class);
            if(s!=null){
                proxyContainer.registerProxy(s.value(),c);
                proxyList.add(s.value());
            }
            for(Class<?> c2:c.getInterfaces()){
                if(c2.getName().equals(ExceptionHandler.class.getName())){
                    try {
                        ExceptionHandler temp=(ExceptionHandler) c.newInstance();
                        exceptionDispatcher.registerHandler(temp);
                        handlerList.add(temp.getName());

                    } catch (InstantiationException e) {

                    } catch (IllegalAccessException e) {

                    }
                    break;
                }
            }

        }
        SystemLogger.logNormal("Register proxy:"+proxyList);
        SystemLogger.logNormal("Register exception handler:"+handlerList);
        SystemLogger.logNormal("Scan necessary dependent class has finished");
        ClassScanner.clear();
    }


    private void loadProtocol(){
        String proStr=configMap.get("rpc.request.protocol.class");
        if(proStr==null || proStr.length()==0){
            requestProtocol= RPCRequestProtocol.Builde();
        }else{
            try{
                Class c=Class.forName(proStr);
                requestProtocol= (RequestProtocol) c.newInstance();
            }catch (Exception e){
                requestProtocol=RPCRequestProtocol.Builde();
            }
        }
        proStr=configMap.get("rpc.response.protocol.class");
        if(proStr==null || proStr.length()==0){
            responseProtocol= RPCResponseProtocol.Builde();
        }else{
            try{
                Class c=Class.forName(proStr);
                responseProtocol= (ResponseProtocol) c.newInstance();
            }catch (Exception e){
                responseProtocol=RPCResponseProtocol.Builde();
            }
        }
    }


    /**
     * Destroy RPC context
     * Must init again if want to use context next time
     */
    private void destroy(){
        logger.logNormal("正在关闭环境");
        proxyContainer=null;
        rpcClient.close();
        rpcClient=null;
    }

    /**
     * Use custom config's map to cover default config's map
     * @param def
     * @param cus
     */
    private void coverConfig(Map<String,String> def,Map<String,String> cus){
        for(Map.Entry<String,String> entry:cus.entrySet()){
            def.put(entry.getKey(),entry.getValue());
        }
    }

    /**
     * Build a map include default config param
     * @return default config's map
     */
    private Map<String,String> buildDefaultConfig(){
        Map<String,String> map=new HashMap<>();
        map.put("rpc.client.port","2057");
        map.put("rpc.client.serviceIp","127.0.0.1");
        map.put("rpc.request.protocol.class","");
        map.put("rpc.response.protocol.class","");
        return map;
    }


}
