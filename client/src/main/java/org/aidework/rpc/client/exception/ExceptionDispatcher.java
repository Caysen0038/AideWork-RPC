package org.aidework.rpc.client.exception;

import org.aidework.rpc.core.helper.SystemLogger;

import java.util.ArrayList;
import java.util.List;

public class ExceptionDispatcher {

    private List<ExceptionHandler> handlerList;
    private static ExceptionDispatcher instance;
    private ExceptionDispatcher(){
        handlerList=new ArrayList<>();
    }
    public static ExceptionDispatcher build(){
        instance=ExceptionDispatcherHolder.instance;
        return instance;
    }
    private static class ExceptionDispatcherHolder{
        private static ExceptionDispatcher instance=new ExceptionDispatcher();
    }

    public static void dispatch(String code){
        for(ExceptionHandler h:instance.handlerList){
            h.handle(code);
        }
    }

    public void registerHandler(ExceptionHandler handler){
        if(handler==null){
            return;
        }
        handlerList.add(handler);
    }
}
