package rpc.test.client;

import org.aidework.rpc.client.RPCClientContext;
import rpc.test.core.bean.TestBean;
import rpc.test.core.inte.TestRPC;

import java.util.Arrays;

public class Application {

    public static void main(String[] args){
        RPCClientContext.start();
        TestRPC test= (TestRPC) RPCClientContext.getProxy("test");
        TestBean bean=new TestBean();
        for(int i=0;i<100;i++){
            for(TestBean b:test.testBaenList(bean)){
                System.out.println(i+"   "+b.getValue());
            }
        }

//        bean.setValue("client set value");
//        for(int i=0;i<100;i++){
//            System.out.println(Arrays.asList(test.testBaenList(bean)));
//        }

    }
}
