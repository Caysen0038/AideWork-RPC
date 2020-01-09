package rpc.test.core.inte;

import org.aidework.rpc.core.annotation.RPCInterface;
import rpc.test.core.bean.TestBean;

@RPCInterface("test")
public interface TestRPC {
    String testValue(String value);
    String testException(String value);
    TestBean[] testBaenList(TestBean bean);
    TestBean testBaen(TestBean bean);
}
