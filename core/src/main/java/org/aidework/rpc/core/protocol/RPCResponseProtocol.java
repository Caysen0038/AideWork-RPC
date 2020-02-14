package org.aidework.rpc.core.protocol;

import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.helper.ByteHelper;
import java.io.*;

/**
 * 消息回送协议
 */
public class RPCResponseProtocol extends ResponseProtocol {

    private RPCResponseProtocol(){
    }
    public static ResponseProtocol Builde(){
        return RPCResponseProtocol.ProtocolHolder.instance;
    }

    private static class ProtocolHolder{
        private static RPCResponseProtocol instance=new RPCResponseProtocol();
    }


    @Override
    public RPCResponse analysis(byte[] temp) {
        byte[] data=new byte[temp.length-HEADER_LENGTH];
        for(int i=HEADER_LENGTH,n=0;i<temp.length;i++,n++){
            data[n]=temp[i];
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            return (RPCResponse) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] generate(RPCResponse bean) {
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
        data[n]=0;
        // set length in header
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
