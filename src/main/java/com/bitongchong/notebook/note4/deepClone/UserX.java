package com.bitongchong.notebook.note4.deepClone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.sql.Timestamp;

/**
 * @author liuyuehe
 * @date 2020/3/24 13:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserX implements Serializable {
    private static final long serialVersionUID = 5022563640219135448L;
    private String name;
    private int age;
    private Timestamp time;

    public UserX cloneObject(Object object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream outputStream = new ObjectInputStream(byteArrayInputStream);
        return (UserX) outputStream.readObject();
    }
}
