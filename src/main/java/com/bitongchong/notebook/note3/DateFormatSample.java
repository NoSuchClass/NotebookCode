package com.bitongchong.notebook.note3;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author liuyuehe
 * @date 2020/1/14 10:16
 */
public class DateFormatSample {
    public static void main(String[] args) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.DECEMBER, 29);
        // 错误的解析方式---YYYY-MM-dd HH:mm:ss
        // 此处的YYYY是week-base-year，凡是跨年的那个week（上周日到本周六），均算在第二年
        DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String formatDateString = format.format(calendar.getTime());
        System.out.println(formatDateString);

        // 正确的解析方式---yyyy-MM-dd HH:mm:ss
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatDateString = format.format(calendar.getTime());
        System.out.println(formatDateString);

        // 解析文本为Date对象
        String dateString = "2020年12月31日 12时23分44秒";
        format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Date parseDate = format.parse(dateString);
        System.out.println(parseDate.toString());
    }
}
