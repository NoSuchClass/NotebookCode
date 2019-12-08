package com.bitongchong.notebook;

/**
 * @author liuyuehe
 * @description ClassLoader测试
 * @date 2019/12/8
 */
public class ClassLoaderCheck {
    public static void main(String[] args) throws  ClassNotFoundException, InstantiationException,  IllegalAccessException {
        MyClassLoader test = new MyClassLoader("D:\\","Test");
        Class class1 = test.loadClass("test");
        System.out.println(class1.getName());
        class1.newInstance();
    }
}
