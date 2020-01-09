package org.aidework.rpc.server;

import org.aidework.rpc.core.helper.ByteHelper;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.server.request.RequestDispatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * socket服务端包装类
 */
public class SocketServer {
    // 监听端口
    private int port;
    // 服务端socket
    private ServerSocket server;
    // 服务端运行状态
    private int status=0;
    // 日志记录
    private SystemLogger logger;

    private Thread mainThread;
    public SocketServer(int port){
        this.port=port;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        try{
            server=new ServerSocket(port);
        }catch (IOException e){
            status=-1;
            e.printStackTrace();
            logger.logError("server init error");
            return;
        }

    }

    /**
     * 开始监听指定端口
     */
    public void listen(){
        status=1;
        mainThread=new Thread(){
            @Override
            public void run(){
                Socket temp;
                try{
                    while(status==1){
                        temp=server.accept();
                        receive(temp);
                    }
                }catch(IOException e){
                    status=-1;
                    logger.logError("server running error");
                    e.printStackTrace();
                }
            };
        };
        mainThread.start();
        logger.logNormal("网络监听服务启动成功");
    }

    public void close(){
        status=0;
    }

    /**
     * 接收socket信息
     * @param socket
     * @throws IOException
     */
    private void receive(Socket socket) throws IOException {
                boolean isReturn=false;
                try{
                    InputStream input=socket.getInputStream();

                    byte[] buffer=new byte[1];
                    if(input.read(buffer)==-1){
                        return;
                    }
                    isReturn=!(buffer[0]==0);
                    buffer=new byte[4];
                    input.read(buffer);
                    int length= ByteHelper.byte2Int(buffer);
                    ByteArrayOutputStream byteOutput=new ByteArrayOutputStream(length);
                    byteOutput.write(isReturn?1:0);
                    byteOutput.write(buffer);
                    int i=5;
                    buffer=new byte[1];
                    while(i++<length && input.read(buffer)!=-1){
                        byteOutput.write(buffer[0]);
                    }
                    byte[] data= RequestDispatcher.dispatch(byteOutput.toByteArray());
                    // 检查是否需要回送信息
                    if(isReturn){
                        OutputStream out=socket.getOutputStream();
                        out.write(data);
                        out.flush();
                        out.close();
                    }
                    byteOutput.close();
                    input.close();
                }catch (IOException e){
                    logger.logError("socket read error");
                    logger.logError(e.getMessage());
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        logger.logError("socket close error");
                    }
                }

    }

}
