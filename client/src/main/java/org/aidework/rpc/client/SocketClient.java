package org.aidework.rpc.client;

import org.aidework.rpc.core.helper.ByteHelper;
import org.aidework.rpc.core.helper.SystemLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
     * 接收socket回送信息
     * @throws IOException
     */
    private byte[] receive() throws IOException {
        try{
            InputStream input=client.getInputStream();
            byte[] buffer=new byte[5];
            input.read(buffer);
            byte[] temp=new byte[4];
            temp[0]=buffer[1];
            temp[1]=buffer[2];
            temp[2]=buffer[3];
            temp[3]=buffer[4];
            int length= ByteHelper.byte2Int(temp);
            ByteArrayOutputStream byteOutput=new ByteArrayOutputStream(length);
            byteOutput.write(buffer);
            int i=5;
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
     * 关闭连接
     */
    public void close(){
        try {
            client.close();
        } catch (IOException e) {
            logger.logError("client close error");
        }
    }
}
