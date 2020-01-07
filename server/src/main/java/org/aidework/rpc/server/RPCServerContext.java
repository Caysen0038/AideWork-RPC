package org.aidework.rpc.server;

import org.aidework.core.helper.PropertiesHelper;
import org.aidework.core.helper.ReflectionHelper;
import org.aidework.rpc.core.annotation.RPCImplements;
import org.aidework.rpc.core.annotation.RPCInterface;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.core.util.ClassScanner;
import org.aidework.rpc.server.request.handler.HandlerContainer;
import org.aidework.rpc.server.request.handler.RequestHandler;
import org.aidework.rpc.server.service.ServiceContainer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC server context container
 */
public class RPCServerContext {
    private Map<String,String> configMap;
    private HandlerContainer handlerContainer =null;
    private ServiceContainer serviceContainer=null;
    private RPCServer rpcServer;
    private static SystemLogger logger;
    private static RPCServerContext instance;

    /**
     * Get the unique instance from the inner class
     * Use singleton mode by inner class character
     * One of character of inner class: Every field just be initialized when class be used
     */
    private static class RPCAppContextHolder{
        private static RPCServerContext instance=new RPCServerContext();
    }

    /**
     * Constructors are not allowed
     */
    private RPCServerContext(){

    }

    /**
     * Start RPC server
     */
    public static void start(){
        instance=RPCAppContextHolder.instance;
        logger.logNormal("开始启动RPC服务端环境");
        // 启动环境
        instance.initContext();
        instance.load();
        instance.rpcServer.listen();
        logger.logNormal("RPC服务端环境启动成功");
    }

    /**
     * Start RPC server by custom config's map
     */
    public static void start(Map<String,String> map){
        instance=RPCAppContextHolder.instance;
        logger.logNormal("开始启动RPC服务端环境");
        // 启动环境
        instance.initContext(map);
        instance.load();
        instance.rpcServer.listen();
        logger.logNormal("RPC服务端环境启动成功");
    }

    /**
     * Start RPC server by custom config‘s path
     */
    public static void start(String path){
        instance=RPCAppContextHolder.instance;
        logger.logNormal("Start RPC server");
        // 启动环境
        instance.initContext(path);
        instance.load();
        instance.rpcServer.listen();
        logger.logNormal("Start RPC server successful");
    }

    /**
     * Close RPC server context
     */
    public static void shutdown(){
        logger.logNormal("正在关闭环境");
        instance.destroy();
    }

    /**
     * Get a Specified request handler by name
     * @param name the handler's name
     * @return
     */
    public static RequestHandler getHandler(String name)
    {
        return instance.handlerContainer.findHandler(name);
    }

    /**
     * Get a Specified request service from service container by name
     * @param name the service's name
     * @return return the specified service if it exists or return null
     */
    public static Object getService(String name) {
        return instance.serviceContainer.getService(name);
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
            path=RPCServerContext
                    .class
                    .getResource("/")
                    .getPath()
                    .replace("%20"," ")
                    +"aidework-config.properties";
        }
        Map<String,String> map=null;
        configMap=buildDefaultConfig();
        try{
            map=PropertiesHelper.read(path);
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
        // 加载数据处理服务
        serviceContainer= ServiceContainer.builde();
        // 加载数据请求处理器
        handlerContainer = HandlerContainer.builde();
        // 启动Socket服务
        rpcServer=new RPCServer(Integer.parseInt(instance.configMap.get("rpc.server.port")));
        loadDependenceClass();
    }

    private void loadDependenceClass(){
        SystemLogger.logNormal("Beginning to scan necessary dependent class");
        ClassScanner.scan();
        List<String> list = ClassScanner.getClassList();
        List<String> serviceList=new ArrayList<>();
        List<String> handlerList=new ArrayList<>();
        for(String str:list){
            Class c= null;
            try {
                c = Class.forName(str);
            } catch (ClassNotFoundException e) {
                continue;
            }
            // check clazz is or not RPCImplements
            RPCImplements s= (RPCImplements) c.getAnnotation(RPCImplements.class);
            if(s!=null){
                // register service to service container
                // container will take over service's life cycle
                RPCInterface rs=null;
                for(Class c2:c.getInterfaces()){
                    if((rs=(RPCInterface)c2.getAnnotation(RPCInterface.class))!=null){
                        try {
                            serviceContainer.registerService(rs.value(),c.newInstance());
                            serviceList.add(rs.value());
                        } catch (InstantiationException e) {

                        } catch (IllegalAccessException e) {

                        }
                        // should scanning other interface from this class after first scanned
                        // to prevent multiple implements
                    }
                }
            }

            // check class is or not RequestHandler
            for(Class<?> c2:c.getInterfaces()){
                if(c2.getName().equals(RequestHandler.class.getName())){
                    try {
                        RequestHandler handler=(RequestHandler) c.newInstance();
                        handlerContainer.registerHandler(handler);
                        handlerList.add(handler.getName());
                    } catch (InstantiationException e) {

                    } catch (IllegalAccessException e) {

                    }
                    break;
                }
            }
        }
        SystemLogger.logNormal("Register service:"+serviceList);
        SystemLogger.logNormal("Register custom data handler:"+handlerList);
        SystemLogger.logNormal("Scan necessary dependent class has finished");
    }
    /**
     * Find and load service if the service implement the interface named RPCImplement
     * @param pack service package's name
     */
//    private void loadService(String pack){
//        logger.logNormal("初始化服务管理器");
//        serviceContainer= ServiceContainer.builde(pack);
//    }

    /**
     * Find and load handler if it implement the interface named RequestHandler
     * @param pack custom handler's package name
     */
//    private void loadHandler(String pack){
//        logger.logNormal("初始化请求处理管理器");
//        handlerContainer = HandlerContainer.builde();
//    }

    /**
     * Destroy context and all container
     */
    private void destroy(){
        handlerContainer =null;
        serviceContainer=null;
        rpcServer.close();
        rpcServer=null;
    }

    /**
     * Use custom config's map to cover default config's map
     *
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
        map.put("rpc.server.port","2057");
        return map;
    }

}
