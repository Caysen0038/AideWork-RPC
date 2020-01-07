package org.aidework.rpc.client.exception;

public interface ExceptionHandler {
    String getName();
    void handle(String code);
}
