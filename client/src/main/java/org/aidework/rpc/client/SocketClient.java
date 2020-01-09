package org.aidework.rpc.client;

import org.aidework.rpc.core.helper.ByteHelper;
import org.aidework.rpc.core.helper.SystemLogger;
import org.aidework.rpc.core.protocol.ResponseProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * socket客户端包装类
 */
public class SocketClient {
    // ip地址
    private String ip;
    // 连接端口
    private int port;
    // 客户端socket
    private Socket client;
    // 日志记录
    private SystemLogger logger;

    public SocketClient(String ip, int port){
        this.ip=ip;
        this.port=port;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        try {
            client=new Socket(ip,port);
        } catch (IOException e) {
            logger.logError("client init error");
        }
    }

    /**
     * 发送消息,若消息带返回请求则返回结果
     * @param data
     */
    public byte[] send (String data){
        return send(data.getBytes());
    }
    /**
     * 发送消息,若消息带返回请求则返回结果
     * @param data
     */
    public byte[] send(byte[] data){
        OutputStream output=null;
        boolean isReturn=!(data[0]==0);
        try {
            output =client.getOutputStream();
            output.write(data);
            output.flush();
            if(isReturn){
                return receive();
            }
        } catch (IOException e) {
            logger.logError("data cannot be send with a error");
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Receive socket response data
     */
    private byte[] receive() {
        try{
            InputStream input=client.getInputStream();
            byte[] buffer=new byte[ResponseProtocol.HEADER_LENGTH];
            input.read(buffer);
            int length= ByteHelper.byte2Int(buffer);
            ByteArrayOutputStream byteOutput=new ByteArrayOutputStream(length);
            byteOutput.write(buffer);
            int i=ResponseProtocol.HEADER_LENGTH;
            buffer=new byte[1];
            while(i++<length && input.read(buffer)!=-1) {
                byteOutput.write(buffer[0]);
            }

            byte[] data=byteOutput.toByteArray();
            byteOutput.close();
            return data;
        }catch (IOException e){
            logger.logError("socket read error");
            logger.logError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Close socket connection
     */
    public void close(){
        try {
            client.close();
        } catch (IOException e) {
            logger.logError("client close error");
        }
    }
}
