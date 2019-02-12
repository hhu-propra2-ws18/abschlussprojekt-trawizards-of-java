package de.trawizardsOfJava.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;

@Data
public class Verfuegbarkeit implements Serializable {
    LocalDate startDate;
    LocalDate endDate;

    public boolean inTimeSpan(LocalDate date){
        return (date.isAfter(startDate) && date.isBefore(endDate));
    }

}
