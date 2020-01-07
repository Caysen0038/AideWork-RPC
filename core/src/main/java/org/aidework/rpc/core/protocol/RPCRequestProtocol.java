package org.aidework.rpc.core.protocol;

import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.helper.ByteHelper;

import java.io.*;

/**
 * RPC请求协议
 */
public class RPCRequestProtocol implements RequestProtocol {
    public static final String TARGET_EXECUTOR="RPC";
    private byte[] header;

    public static Protocol Builde(){
        return RPCRequestProtocol.ProtocolHolder.instance;
    }
    /**
     * 利用内部类的惰性加载实现懒汉单例
     * 同时static由JVM保证线程安全，实例单一
     */
    private static class ProtocolHolder{
        private static RPCRequestProtocol instance=new RPCRequestProtocol();
    }

    private RPCRequestProtocol(){
        header=new byte[HEADER_LENGTH];
        int n=5;
        for(byte b:TARGET_EXECUTOR.getBytes()){
            header[n++]=b;
        }
        for(;n<HEADER_LENGTH;n++){
            header[n]=0;
        }
    }
    @Override
    public String getTargetExecutor() {
        return TARGET_EXECUTOR;
    }

    @Override
    public RPCRequest analysis(byte[] temp) {
        byte[] data=new byte[temp.length-HEADER_LENGTH];
        for(int i=HEADER_LENGTH,n=0;i<temp.length;i++,n++){
            data[n]=temp[i];
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            return (RPCRequest) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public byte[] generate(RPCRequest bean) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(bean);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] temp=byteArrayOutputStream.toByteArray();

        byte[] data=new byte[HEADER_LENGTH+temp.length];
        int n=0;
        data[n++]=bean.isReturn()?(byte)1:0;
        for(byte b: ByteHelper.int2Byte(data.length)){
            data[n++]=b;
        }
        for(;n<HEADER_LENGTH;n++){
            data[n]=header[n];
        }
        for(byte b:temp){
            data[n++]=b;
        }
        return data;
    }

}
