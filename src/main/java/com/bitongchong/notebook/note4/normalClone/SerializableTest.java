package com.bitongchong.notebook.note4.normalClone;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author liuyuehe
 * @date 2020/3/24 13:25
 */
public class SerializableTest implements Serializable {
    public static void main(String[] args) throws CloneNotSupportedException {
        User user1 = new User("张三", 12, Timestamp.valueOf(LocalDateTime.now()));
        User user2 = user1.clone();
        user2.setTime(Timestamp.valueOf(LocalDateTime.now()));
        user2.setAge(325346);
        // User(name=张三, age=12, time=2020-03-24 13:44:49.751)
        // User(name=张三, age=325346, time=2020-03-24 13:44:49.752)
        System.out.println(user1.toString());
        System.out.println(user2.toString());
    }
}
