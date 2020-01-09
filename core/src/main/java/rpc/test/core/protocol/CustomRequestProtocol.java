package rpc.test.core.protocol;

import org.aidework.rpc.core.bean.RPCRequest;
import org.aidework.rpc.core.helper.ByteHelper;
import org.aidework.rpc.core.protocol.RequestProtocol;

import java.io.*;

/**
 * RPC请求协议
 */
public class CustomRequestProtocol extends RequestProtocol {
    public static final String TARGET="CUSTOM";


    public CustomRequestProtocol(){

    }

    @Override
    public byte[] getTarget() {
        return TARGET.getBytes();
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
