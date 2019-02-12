package com.example.trawizardsOfJava.model;

import java.time.LocalDate;

public class Zeitraum {

    private LocalDate startDate;

    private LocalDate endDate;

    public Zeitraum(LocalDate startDate, LocalDate endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean inTimespan(LocalDate day){
        return(day.isAfter(startDate) && day.isBefore(endDate));
    }
}
