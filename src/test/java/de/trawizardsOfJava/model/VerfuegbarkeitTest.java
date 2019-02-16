package de.trawizardsOfJava.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerfuegbarkeitTest {
    @Test
    public void testVerfuegbarkeit() {
        String s = "01/02/2018 - 31/05/2019";
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(s);
        assertEquals("2019-05-31", verfuegbarkeit.getEndDate().toString());
        assertEquals("2018-02-01", verfuegbarkeit.getStartDate().toString());
    }
}