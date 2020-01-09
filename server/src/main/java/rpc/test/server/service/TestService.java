package rpc.test.server.service;

import org.aidework.rpc.core.annotation.RPCImplements;
import rpc.test.core.bean.TestBean;
import rpc.test.core.inte.TestRPC;

@RPCImplements
public class TestService implements TestRPC {
    public String testValue(String value) {
        return "hello client";
    }

    public String testException(String value) {
        throw new NullPointerException("test exception");
    }

    public TestBean[] testBaenList(TestBean bean) {
        TestBean[] list=new TestBean[5];
        for(int i=0;i<list.length;i++){
            bean.setValue("server has reset the value "+i);
            list[i]=bean;
        }
        return list;
    }

    public TestBean testBaen(TestBean bean) {
        bean.setValue("server has reset the value");
        return bean;
    }
}
