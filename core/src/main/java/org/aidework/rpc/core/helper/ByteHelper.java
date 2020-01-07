package org.aidework.rpc.core.helper;

/**
 * 字节辅助工具
 */
public class ByteHelper {
    /**
     * byte转int，仅支持4字节转换
     * @param bytes
     * @return
     */
    public static int byte2Int(byte[] bytes) {
        int result = 0;
        //将每个byte依次搬运到int相应的位置
        result = bytes[0] & 0xff;
        result = result << 8 | bytes[1] & 0xff;
        result = result << 8 | bytes[2] & 0xff;
        result = result << 8 | bytes[3] & 0xff;
        return result;
    }

    /**
     * int转byte,转出为4byte
     * @param num
     * @return
     */
    public static byte[] int2Byte(int num) {
        byte[] bytes = new byte[4];
        //通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte)(num >>> 24);
        bytes[1] = (byte)(num >>> 16);
        bytes[2] = (byte)(num >>> 8);
        bytes[3] = (byte)num;
        return bytes;
    }
}
