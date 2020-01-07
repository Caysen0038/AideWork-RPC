package org.aidework.rpc.core.protocol;

/**
 * 协议模板接口
 * 针对不同数据类型，框架使用者可自定义协议及对应处理器
 * 一套完整的数据处理方案由协议+处理器构成
 */
public interface Protocol<T> {
    /**
     * 标准数据头
     * 0：标记是否由返回值，0为无，1为有
     * 1-4：标记数据包总大小，包含包头
     * 5-14：标记数据处理器标识
     * 无论继承实现何种协议，数据头都应按此实现
     * 请求分配器将按此包头识别并分配处理器
     */
    int HEADER_LENGTH=15;

    /**
     * 获取目标数处理器标识
     * @return
     */
    String getTargetExecutor();

    /**
     * 将二进制数据解析为数据模型
     * @param data
     * @return
     */
    T analysis(byte[] data);

    /**
     * 将对象调制成二进制数据
     * @param bean
     * @return
     */
    byte[] generate(T bean);
}
