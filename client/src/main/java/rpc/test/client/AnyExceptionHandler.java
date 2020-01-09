package rpc.test.client;

import org.aidework.rpc.client.exception.ExceptionHandler;

public class AnyExceptionHandler implements ExceptionHandler {
    public String getName() {
        return "anyhandler";
    }

    public void handle(String s) {
        System.out.println("receive exception code:【"+s+"】");
    }
}
