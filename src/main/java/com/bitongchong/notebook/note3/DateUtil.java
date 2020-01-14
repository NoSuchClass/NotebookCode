package com.bitongchong.notebook.note3;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuyuehe
 * @date 2020/1/14 10:30
 */
public class DateUtil {
    private DateUtil() {

    }

    public String dateToString(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public Date stringToDate(String pattern, String dateString) throws ParseException {
        return new SimpleDateFormat(pattern).parse(dateString);
    }

}
