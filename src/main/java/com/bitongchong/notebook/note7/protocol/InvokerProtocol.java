package com.bitongchong.notebook.note7.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuyuehe
 * @date 2020/3/27 15:36
 *
 * 这儿主要要实现Serializable接口，否则netty会报错：UnsupportedOperationException
 */
@Data
public class InvokerProtocol implements Serializable {
    private static final long serialVersionUID = -5429760965904971178L;
    /**
     * 访问的类名
     */
    private String className;
    /**
     * 访问的方法名
     */
    private String methodName;
    /**
     * 访问的实参列表
     */
    private Object[] values;
    /**
     * 访问的实参列表
     */
    private Class<?>[] params;
}
