package com.franciscomarin.call_center;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LlamadaTest {

	/**
	 * Prueba crear una Llamada de duracion invalida
	 */
    @Test(expected = IllegalArgumentException.class)
    public void testLlamadaCrearInvalida() {
        new Llamada(-1);
    }

    /**
     * Prueba crear una Llamada de duracion null
     */
    @Test(expected = NullPointerException.class)
    public void testLlamadaCrearNull() {
        new Llamada(null);
    }

    /**
     * Prueba crear una Llamada de duracion Aleatoria con valores errados.
     * minimaDuracion errada
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLlamadaCrearAleatoriaInvalida() {
        Llamada.crearAleatoria(-1, 1);
    }

    /**
     * Prueba crear una Llamada de duracion Aleatoria con valores errados.
     * maximaDuracion errada
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLlamadaCrearAleatoriaInvalida2() {
        Llamada.crearAleatoria(1, -1);
    }

    /**
     * Prueba crear una Llamada de duracion Aleatoria con valores errados.
     * maximaDuracion inferior a minimaDuracion
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLlamadaCrearAleatoriaInvalida3() {
        Llamada.crearAleatoria(2, 1);
    }

    /**
     * Prueba crear una Llamada con valores validoss
     */
    @Test
    public void testLlamadaCrearValida() {
        int min = 5;
        int max = 10;
        Llamada l = Llamada.crearAleatoria(min, max).get();

        assertNotNull(l);
        assertTrue(min <= l.getDuracion());
        assertTrue(l.getDuracion() <= max);
    }
}
