package org.aidework.rpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC接口定义注解
 * 所有RPC接口必须实现该注解，否则将不被注册进容器
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RPCInterface {
    public String value();
}
