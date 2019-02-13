package de.trawizardsOfJava.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerfuegbarkeitTest {
    Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();

    @Test
    public void toVerfuegbarkeit() {
        String s = "02/01/2018 - 05/31/2019";
        verfuegbarkeit.toVerfuegbarkeit(s);
        assertEquals("2019-05-31", verfuegbarkeit.endDate.toString());
        assertEquals("2018-02-01", verfuegbarkeit.startDate.toString());
    }
}