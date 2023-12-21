package com.example.exam;

import android.app.Application;

public class MyApplication extends Application {
    // 전역으로 사용할 변수 선언
    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedDay = -1;

    // Getter 및 Setter 메서드
    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public int getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(int selectedDay) {
        this.selectedDay = selectedDay;
    }
}
