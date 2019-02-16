package de.trawizardsOfJava.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
public class Verfuegbarkeit implements Serializable {
	private LocalDate startDate;
	private LocalDate endDate;

	public Verfuegbarkeit(String s) {
		String[] arr = s.split("-");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.startDate = LocalDate.parse(arr[0].trim(), formatter);
		this.endDate = LocalDate.parse(arr[1].trim(), formatter);
	}

	public boolean inTimeSpan(LocalDate date) {
		return (date.isAfter(this.startDate) && date.isBefore(this.endDate));
	}
	
	public int berechneZwischenTage() {
		return (int)DAYS.between(this.startDate, this.endDate);
	}
}