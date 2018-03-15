package com.helly.domotion.Misc;

// Created by helly on 2/3/18.

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeFormat {
    private static final int[] daysBeforeMonth = {-1, 0, 31, 59, 90, 120, 151, 181, 212, 242, 273, 303, 344};
    private static final int daysIn4Years = 4*365+1;
    private static final int daysIn100Years = 25*daysIn4Years-1;
    private static final int daysIn400Years = 4*daysIn100Years+1;

    public static String Mod2Asc(int Mod) {
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        int h = Mod/60;
        int m = Mod%60;
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1, h, m, 0);

        return timeFormat.format(cal.getTime());
    }

    public static int Asc2Mod(String TimeStr) {
        int h = 0;
        int m = 0;
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        try {
            Date date = timeFormat.parse(TimeStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            h = cal.get(Calendar.HOUR_OF_DAY);
            m = cal.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calcMod(h, m);
    }

    static int calcMod(int h, int m) {
        return (h*60+m);
    }

    static int getModHour(int Mod) {
        return (Mod/60);
    }

    static int getModMinute(int Mod) {
        return (Mod%60);
    }

    public static String Ord2Asc(int Ord) {
        int year;
        int month;
        int day;
        Ord -= 1;
        int Ord400 = Ord / daysIn400Years;
        Ord = Ord % daysIn400Years;

        year = Ord400 * 400 + 1;

        int Ord100 = Ord / daysIn100Years;
        Ord = Ord % daysIn100Years;

        int Ord4 = Ord / daysIn4Years;
        Ord = Ord % daysIn4Years;

        int Ord1 = Ord / 365;
        Ord = Ord % 365;

        year += Ord100 * 100 + Ord4 * 4 + Ord1;
        if ((Ord1 == 4) || (Ord100 == 4)) {
            year -= 1;
            month = 12;
            day = 31;
        } else {
            month = (Ord + 50) >> 5;
            int preceding = DaysBeforeMonth(year, month);
            if (preceding > Ord) {
                month -= 1;
                preceding = DaysBeforeMonth(year, month);
            }
            Ord -= preceding;
            day = Ord + 1;
        }
        SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT);
        String pattern = dateFormat.toPattern().replaceAll("y+","yyyy");
        dateFormat.applyPattern(pattern);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);

        return dateFormat.format(cal.getTime());
    }

    public static int Asc2Ord(String DateStr) {
        // "year, month, day -> ordinal, considering 01-Jan-0001 as day 1."
        int year = 1;
        int month = 1;
        int day = 1;
        Calendar cal = ParseString(DateStr);
        if (cal != null) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);
        }

        return calcOrd(year, month, day);
    }

    static int getAscDay(String DateStr) {
        int day = 0;
        Calendar cal = ParseString(DateStr);
        if (cal != null) {
            day = cal.get(Calendar.DAY_OF_MONTH);
        }

        return day;
    }

    static int getAscMonth(String DateStr) {
        int month = 0;
        Calendar cal = ParseString(DateStr);
        if (cal != null) {
            month = cal.get(Calendar.MONTH);
        }

        return month;
    }

    static int getAscYear(String DateStr) {
        int year = 0;
        Calendar cal = ParseString(DateStr);
        if (cal != null) {
            year = cal.get(Calendar.YEAR);
        }

        return year;
    }

    static int calcOrd(int year, int month, int day) {
        return (DaysBeforeYear(year) + DaysBeforeMonth(year, month) + day);
    }

    public static boolean IsToday(int Start, int End) {
        Calendar cal = getToday();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int Ord = calcOrd(year, month, day);

        return ((Ord >= Start) && (Ord <= End));
    }

    public static String getTodayAsc() {
        Calendar cal = getToday();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return Ord2Asc(calcOrd(year, month, day));
    }

    public static int getTodayOrd() {
        Calendar cal = getToday();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return calcOrd(year, month, day);
    }

    private static Calendar getToday() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;
    }

    private static Calendar ParseString(String DateStr) {
        Calendar cal = null;

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        try {
            Date date = dateFormat.parse(DateStr);
            cal = Calendar.getInstance();
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cal;
    }

    private static boolean IsLeap(int year) {
        // "year -> 1 if leap year, else 0."
        return ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0)));
    }

    private static int DaysBeforeYear(int year) {
        // "year -> number of days before January 1st of year."
        int y = year - 1;
        return (y*365 + y/4 - y/100 + y/400);
    }

    private static int DaysBeforeMonth(int year, int month) {
        // "year, month -> number of days in year preceding first day of month."
        return daysBeforeMonth[month] + (((month>2) && (IsLeap(year)))?1:0);
    }
}
