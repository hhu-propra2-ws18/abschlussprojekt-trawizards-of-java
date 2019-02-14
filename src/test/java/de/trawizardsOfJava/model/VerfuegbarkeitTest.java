package de.trawizardsOfJava.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerfuegbarkeitTest {
    Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();

    @Test
    public void toVerfuegbarkeit() {
        String s = "01/02/2018 - 31/05/2019";
        verfuegbarkeit.toVerfuegbarkeit(s);
        assertEquals("2019-05-31", verfuegbarkeit.endDate.toString());
        assertEquals("2018-02-01", verfuegbarkeit.startDate.toString());
    }
}