package com.example.trawizardsOfJava.model;

import lombok.Data;

import java.util.Calendar;

@Data
public class Verfügbarkeit {
    Calendar von;
    Calendar bis;
}
