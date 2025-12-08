package com.my;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;

public class WeeklyDateRange {
    public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 7, 30);

        // 确保起始日期是周一
        startDate = startDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY));

        // 遍历日期直到（但不包括）结束日期的下一个周一
        while (!startDate.isAfter(endDate)) {
            LocalDate endDateOfWeek = startDate.plusDays(6);

            // 如果结束日期所在的周被截断（即结束日期不是周日），则使用实际的结束日期
            if (endDateOfWeek.isAfter(endDate)) {
                endDateOfWeek = endDate;
            }

            // 打印每周的日期范围
            printWeeklyRange(startDate, endDateOfWeek);

            // 移动到下一周的周一
            startDate = startDate.plusWeeks(1);
        }
    }

    private static void printWeeklyRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        System.out.println(start.format(formatter) + "-" + end.format(formatter));
    }
}