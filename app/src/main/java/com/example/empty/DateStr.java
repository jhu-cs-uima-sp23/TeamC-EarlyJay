package com.example.empty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateStr {

    String dateStr;

    int year;

    int month;

    int day;

    int dayOfTheWeek;

    public DateStr() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateStr = dt.format(date);

        String[] dateInfo = dateStr.split("-");
        year = Integer.parseInt(dateInfo[0]);
        month = Integer.parseInt(dateInfo[1]);
        day = Integer.parseInt(dateInfo[2]);
        dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dateStr += "-" + dayOfTheWeek;

    }

    public DateStr(String dateStr) {
        this.dateStr = dateStr;

        String[] dateInfo = dateStr.split("-");
        year = Integer.parseInt(dateInfo[0]);
        month = Integer.parseInt(dateInfo[1]);
        day = Integer.parseInt(dateInfo[2]);
        dayOfTheWeek = Integer.parseInt(dateInfo[3]);

    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getDayOfTheWeek() { return dayOfTheWeek; }

    public String getDateStr() {
        return dateStr;
    }

    private int getYearDays(int year) {
        if (year % 400 == 0 || year / 4 != 0) {
            return 365;
        }
        return 366;
    }

    private int getMonthDays(int year, int month) {
        int[] daysInAMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (month != 2) {
            return daysInAMonth[month - 1];
        }
        if (getYearDays(year) == 365) {
            return daysInAMonth[1];
        }
        return daysInAMonth[1] + 1;
    }

    public int getMonthDays() {
        int[] daysInAMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (month != 2) {
            return daysInAMonth[month - 1];
        }
        if (getYearDays(year) == 365) {
            return daysInAMonth[1];
        }
        return daysInAMonth[1] + 1;
    }


    public String getPastDay(int dayNum) {
        int startYear;
        int startMonth;
        int startDay;
        int startDayOfTheWeek = (dayOfTheWeek - 1 - dayNum + 7 * dayNum) % 7 + 1;
        if (day > dayNum) {
            startDay = day - dayNum;
            startMonth = month;
            startYear = year;
        } else if (month != 1) {
            startDay = getMonthDays(year, month - 1) + day - dayNum;
            startMonth = month - 1;
            startYear = year;
        } else {
            startDay = getMonthDays(year - 1, 12) + day - dayNum;
            startMonth = 12;
            startYear = year - 1;
        }
        String startMonthStr = (startMonth < 10) ? "0" + startMonth : Integer.toString(startMonth);
        String startDayStr = (startDay < 10) ? "0" + startDay : Integer.toString(startDay);
        return startYear + "-" + startMonthStr + "-" + startDayStr + "-" + startDayOfTheWeek;
    }

    public String getFutureDay(int dayNum) {
        int startYear;
        int startMonth;
        int startDay;
        int startDayOfTheWeek = (dayOfTheWeek - 1+ dayNum ) % 7 + 1;
        if (day + dayNum <= getMonthDays(year, month)) {
            startYear = year;
            startMonth = month;
            startDay = day + dayNum;
        } else if (month != 12) {
            startMonth = month + 1;
            startYear = year;
            startDay = day + dayNum - getMonthDays(year, month);
        } else {
            startYear = year + 1;
            startMonth = 1;
            startDay = day + dayNum - getMonthDays(year, month);
        }
        String startMonthStr = (startMonth < 10) ? "0" + startMonth : Integer.toString(startMonth);
        String startDayStr = (startDay < 10) ? "0" + startDay : Integer.toString(startDay);
        return startYear + "-" + startMonthStr + "-" + startDayStr + "-" + startDayOfTheWeek;
    }

    public int comp(DateStr other) {
        int largerMonth = (month > other.getMonth()) ? 1 : -1;
        int largerYear = (year > other.getYear()) ? 1 : -1;
        int lMon = (largerMonth == 1) ? month : other.getMonth();
        int sMon = (largerMonth == 1) ? other.getMonth(): month;
        int lYear = (largerYear == 1) ? year: other.getYear();
        int sYear = (largerYear == 1) ? other.getYear() : year;
        int sumYear = 0;
        int sumMonth = 0;
        for (int y = sYear; y < lYear; ++y) {
            sumYear += getYearDays(y);
        }
        for (int m = sMon; m < lMon; ++m) {
            sumMonth += (largerMonth == largerYear) ? getMonthDays(lYear, m) : getMonthDays(sYear, m) ;
        }

        return largerMonth * sumMonth + largerYear * sumYear + (day - other.getDay());
    }

    public boolean isDaily(String dateStr) {
        DateStr dateStrObj = new DateStr(dateStr);
        return (year == dateStrObj.getYear() && month == dateStrObj.getMonth()
                && day == dateStrObj.getDay());
    }

    public boolean isWeekly(String dateStr) {
        int difference = comp(new DateStr(dateStr));

        return (difference < 0 && difference * -1 < 8 - dayOfTheWeek) ||
                (difference > 0 && difference < dayOfTheWeek || difference == 0);
    }

    public boolean isMonthly(String dateStr) {
        DateStr dateStrObj = new DateStr(dateStr);
        return (year == dateStrObj.getYear() && month == dateStrObj.getMonth());
    }


    public boolean isPastDay(String dateStr) {
        DateStr yesterdayStrObj = new DateStr(getPastDay(1));
        DateStr dateStrObj = new DateStr(dateStr);
        return (yesterdayStrObj.getYear() == dateStrObj.getYear() &&
                yesterdayStrObj.getMonth() == dateStrObj.getMonth()
                && yesterdayStrObj.getDay() == dateStrObj.getDay());
    }

    public boolean isPastWeek(String dateStr) {
        DateStr lastWeekStrObj = new DateStr(getPastDay(dayOfTheWeek));
        return lastWeekStrObj.isWeekly(dateStr);
    }

    public boolean isPastMonth(String dateStr) {
        DateStr lastMonthStrObj = new DateStr(getPastDay(day));
        return lastMonthStrObj.isMonthly(dateStr);
    }
}
