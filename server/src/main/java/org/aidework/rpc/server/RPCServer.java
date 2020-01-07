package org.aidework.rpc.server;

public class RPCServer {
    private int port;
    private SocketServer server;
    public RPCServer(int port){
        this.port=port;
        init();
    }
    private void init(){
        server=new SocketServer(port);
    }
    public void listen(){
        server.listen();
    }
    public void close(){
        server.close();
    }
}
