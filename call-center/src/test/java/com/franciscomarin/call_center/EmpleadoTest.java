package com.franciscomarin.call_center;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.franciscomarin.call_center.Empleado.Estado;
import com.franciscomarin.call_center.Empleado.Tipo;

public class EmpleadoTest {

	/**
	 * Prueba crear Empleado con Tipo NULL
	 */
    @Test(expected = NullPointerException.class)
    public void testEmpleadoInvalido() {
        new Empleado(null);
    }

    /**
     * Prueba crear un Empleado Tipo OPERADOR
     */
    @Test
    public void testEmpleadoCrearOperador() {
        Empleado e = Empleado.nuevoOperador();

        assertNotNull(e);
        assertEquals(Tipo.OPERADOR, e.getTipo());
        assertEquals(Estado.DISPONIBLE, e.getEstado());
    }

    /**
     * Prueba crear un OPERADOR y que atienda una llamada
     * @throws InterruptedException
     */
    @Test
    public void testEmpleadoAtiendeLlamada() throws InterruptedException {
        Empleado e = Empleado.nuevoOperador();
        ExecutorService execService = Executors.newSingleThreadExecutor();

        execService.execute(e);
        e.atender(Llamada.crearAleatoria(0, 1).get());

        execService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, e.getAtendidas().size());
    }

    /**
     * Prueba crear un OPERADOR que este atienda una Llamada mientras esta OCUPADO
     * @throws InterruptedException
     */
    @Test
    public void testEmpleadoAtiendeOcupado() throws InterruptedException {
        Empleado e = Empleado.nuevoOperador();
        ExecutorService execService = Executors.newSingleThreadExecutor();

        execService.execute(e);
        assertEquals(Estado.DISPONIBLE, e.getEstado());
        TimeUnit.SECONDS.sleep(1);
        e.atender(Llamada.crearAleatoria(2, 3).get());
        e.atender(Llamada.crearAleatoria(0, 1).get());
        TimeUnit.SECONDS.sleep(1);
        assertEquals(Estado.OCUPADO, e.getEstado());

        execService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(2, e.getAtendidas().size());
    }

}
