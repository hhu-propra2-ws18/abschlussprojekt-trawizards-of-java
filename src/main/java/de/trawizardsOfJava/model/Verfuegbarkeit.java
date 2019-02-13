package de.trawizardsOfJava.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Data
public class Verfuegbarkeit implements Serializable {
    LocalDate startDate;
    LocalDate endDate;

    public void toVerfuegbarkeit(String s){
        String[] arr = s.split("-");
        DateTimeFormatter  formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        this.startDate = LocalDate.parse(arr[0].trim(), formatter);
        this.endDate = LocalDate.parse(arr[1].trim(), formatter);
    }

    public boolean inTimeSpan(LocalDate date){
        return (date.isAfter(startDate) && date.isBefore(endDate));
    }

}
