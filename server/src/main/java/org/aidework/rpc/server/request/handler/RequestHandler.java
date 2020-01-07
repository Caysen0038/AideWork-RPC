package org.aidework.rpc.server.request.handler;

/**
 * Data handler interface
 * Class while implements this interface can be registered to handler's container.
 * Handler's container will take over handler's life cycle when handler be registered.
 */
public interface RequestHandler {
    /**
     * Get handler's name
     * @return handler's name
     */
    String getName();

    /**
     * Handler data
     * @param data
     * @return
     */
    byte[] execute(byte[] data);
}
