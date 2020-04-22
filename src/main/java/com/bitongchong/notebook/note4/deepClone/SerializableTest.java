package com.bitongchong.notebook.note4.deepClone;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author liuyuehe
 * @date 2020/3/24 13:25
 */
public class SerializableTest implements Serializable {
    private static final long serialVersionUID = -5685132349000933949L;

    public static void main(String[] args) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        User user1 = new User("张三", 12, Timestamp.valueOf(LocalDateTime.now()));
        User user2 = user1.clone();
        user2.setTime(Timestamp.valueOf(LocalDateTime.now()));
        user2.setAge(325346);
        //User(name=张三, age=12, time=2020-03-24 14:09:21.025)
        //User(name=张三, age=325346, time=2020-03-24 14:09:21.029)
        System.out.println(user1.toString());
        System.out.println(user2.toString());

        // UserX(name=李四, age=12, time=2020-03-24 14:09:21.031)
        // UserX(name=李四, age=344, time=2020-03-24 14:09:21.055)
        UserX userX1 = new UserX("李四", 12, Timestamp.valueOf(LocalDateTime.now()));
        UserX userX2 = userX1.cloneObject(userX1);
        userX2.setTime(Timestamp.valueOf(LocalDateTime.now()));
        userX2.setAge(344);
        System.out.println(userX1.toString());
        System.out.println(userX2.toString());
    }
}
