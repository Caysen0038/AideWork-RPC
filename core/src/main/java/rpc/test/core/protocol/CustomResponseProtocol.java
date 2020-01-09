package rpc.test.core.protocol;

import org.aidework.rpc.core.bean.RPCResponse;
import org.aidework.rpc.core.helper.ByteHelper;
import org.aidework.rpc.core.protocol.ResponseProtocol;
import java.io.*;

/**
 * 消息回送协议
 */
public class CustomResponseProtocol extends ResponseProtocol {
    /**
     * 消息头
     * 在此协议中，0位（返回值标识）作废，5-14位（处理器标识）作废
     */

    public CustomResponseProtocol(){
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
