package com.expensetracker.reports;

import java.time.LocalDate;

public abstract class Report {
    protected String title;
    protected LocalDate generatedDate;

    public abstract String getReportContent();

    public String getTitle() {
        return title;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void printReport() {
        System.out.println(getReportContent());
    }
}