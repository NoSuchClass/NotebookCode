package com.bitongchong.notebook.note2;

/**
 * @author liuyuehe
 * @description
 * @date 2019/12/8
 */
public class Load {
    public static void main(String[] args) throws Exception {
        ClassLoader aclassLoader = Load.class.getClassLoader();
        //       aclassLoader.loadClass("com.bitongchong.notebook.note2.Person");
       Class<Load> forName = (Class<Load>)  Class.forName("com.bitongchong.notebook.note2.Person");
    }
}
