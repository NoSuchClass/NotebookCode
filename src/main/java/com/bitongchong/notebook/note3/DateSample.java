package com.bitongchong.notebook.note3;

import java.util.Date;

/**
 * @author liuyuehe
 * @date 2020/1/14 10:04
 */
public class DateSample {
    public static void main(String[] args) {
        Date date = new Date();
        // 获取Date毫秒数
        System.out.println(date.getTime());
        // 设置Date毫秒数
        date.setTime(1);
        System.out.println(date);
    }
}