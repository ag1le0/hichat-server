package com.foxconn.fii.common.utils;

import com.foxconn.fii.common.ShiftType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class TestUtils {

    public static <T> Map<String, T> getHourlyMap(Date startDate, Date endDate, String pattern) {
        Map<String, T> byTimeMap = new LinkedHashMap<>();

        SimpleDateFormat df = new SimpleDateFormat(pattern);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.MINUTE, 30);
        Date tmp = calendar.getTime();

        Date now = new Date();
        while (tmp.before(now) && tmp.before(endDate)) {
            String key = df.format(tmp);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            tmp = calendar.getTime();
            key += " - " + df.format(tmp);
            byTimeMap.put(key, null);
        }

        return byTimeMap;
    }

    public static <T> Map<String, T> getWeeklyMap(Date startDate, Date endDate, String pattern) {
        Map<String, T> byTimeMap = new LinkedHashMap<>();

        SimpleDateFormat df = new SimpleDateFormat(pattern);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Date tmp = calendar.getTime();

        Date now = new Date();
        while (tmp.before(now) && tmp.before(endDate)) {
            String key = df.format(tmp);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            tmp = calendar.getTime();
            key += " - " + df.format(tmp);
            byTimeMap.put(key, null);
        }

        return byTimeMap;
    }

    public static String getWorkSectionCondition(ShiftType shiftType) {
        if (shiftType == null) {
            return "(work_date || trim(to_char(work_section,'00'))) >= (:current || '08') and (work_date || trim(to_char(work_section,'00'))) < (:next || '08') ";
        } else if (shiftType == ShiftType.DAY) {
            return "(work_date || trim(to_char(work_section,'00'))) >= (:current || '08') and (work_date || trim(to_char(work_section,'00'))) < (:current || '20') ";
        } else {
            return "(work_date || trim(to_char(work_section,'00'))) >= (:current || '20') and (work_date || trim(to_char(work_section,'00'))) < (:next || '08') ";
        }
    }

    public static String getWorkDateAndWorkSectionBetween() {
        return "(work_date || trim(to_char(work_section,'00'))) >= (:current || trim(to_char(:current_work_section,'00'))) and (work_date || trim(to_char(work_section,'00'))) < (:next || trim(to_char(:next_work_section,'00'))) ";
    }

}
