package com.bitongchong.notebook.note4.deepClone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author liuyuehe
 * @date 2020/3/24 13:22
 * 要实现一下Cloneable接口，否则会报CloneNotSupportedException异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Cloneable {
    private String name;
    private int age;
    private Timestamp time;

    @Override
    protected User clone() throws CloneNotSupportedException {
        User clone = (User) super.clone();
        clone.setTime((Timestamp) this.time.clone());
        return clone;
    }
}
