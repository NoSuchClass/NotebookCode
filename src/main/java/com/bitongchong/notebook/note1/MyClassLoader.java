package com.bitongchong.notebook.note1;

import java.io.*;

/**
 * @author liuyuehe
 * @description 自定义ClassLoader
 * @date 2019/12/8
 */
public class MyClassLoader extends ClassLoader {
    private String path;
    private String className;

    MyClassLoader(String path, String className) {
        this.path = path;
        this.className = className;
    }

    @Override
    public Class findClass(String name) {
        byte[] b = null;
        try {
            b = loadClassData(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert b != null;
        return defineClass(className, b, 0, b.length);
    }

    private byte[] loadClassData(String name) throws IOException {
        String namePath = path + name + ".class";
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(new File(namePath));
            out = new ByteArrayOutputStream();
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert in != null;
            in.close();
            assert out != null;
            out.close();

        }
        return out.toByteArray();
    }
}
