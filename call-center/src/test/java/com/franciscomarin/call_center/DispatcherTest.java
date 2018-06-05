package com.franciscomarin.call_center;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DispatcherTest {
	private static final int CANT_EMPLEADOS = 10;
	private static final int CANT_LLAMADAS = 10;
	private static final int DURACION_MINIMA = 5;
	private static final int DURACION_MAXIMA = 10;

	/**
	 * Prueba crear el Dispatcher con un Lista Nula
	 */
	@Test(expected = NullPointerException.class)
	public void testDispatcherCrearEmpleadosNull() {
		new Dispatcher(null);
	}

	/**
	 * Prueba rcear el Dispatcher con una Lista Vacia
	 */
	@Test(expected = NullPointerException.class)
	public void testDispatcherCrearEmpleadosListaVacia() {
		new Dispatcher(new ArrayList<Empleado>());
	}

	/**
	 * Prueba el ciclo de Atencion de Llamadas por los Empleados
	 */
	@Test
	public void testDispatchCallConEmpleados() throws InterruptedException {
		List<Empleado> eLista = crearEmpleados();
		Dispatcher dispatcher = new Dispatcher(eLista);
		dispatcher.start();
		TimeUnit.SECONDS.sleep(1);
		ExecutorService execService = Executors.newSingleThreadExecutor();
		execService.execute(dispatcher);
		TimeUnit.SECONDS.sleep(1);

		crearLlamadas().stream().forEach(l -> {
			dispatcher.dispatchCall(l);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				fail();
			}
		});

		execService.awaitTermination(DURACION_MAXIMA * 2, TimeUnit.SECONDS);
		assertEquals(CANT_LLAMADAS, eLista.stream().mapToInt(e -> e.getAtendidas().size()).sum());
	}

	/**
	 * Crea una Lista de 10 Empleados
	 * @return Lista de Empleados con todos los Tipos
	 */
	private static List<Empleado> crearEmpleados() {
		List<Empleado> eLista = new ArrayList<Empleado>();

		for(int i = 0; i < CANT_EMPLEADOS; i++) {
			if(i < 6) {
				eLista.add(Empleado.nuevoOperador());
			} else if(i < 9) {
				eLista.add(Empleado.nuevoSupervisor());
			} else {
				eLista.add(Empleado.nuevoDirector());
			}
		}
		return eLista;
	}

	/**
	 * Crea una lista de Llamadas Aleatorias
	 * @return Lista de Llamadas de duracion Aleatoria
	 */
	private static List<Llamada> crearLlamadas() {
        List<Llamada> lLista = new ArrayList<Llamada>();
        for (int i = 0; i < CANT_LLAMADAS; i++) {
        	lLista.add(Llamada.crearAleatoria(DURACION_MINIMA, DURACION_MAXIMA).get());
        }
        return lLista;
	}

}
