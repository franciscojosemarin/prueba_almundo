/**
 * 
 */
package com.franciscomarin.call_center;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Clase Llamada: para el manejo de los Objetos Llamada
 * @author Francisco Marin
 *
 */
public class Llamada {
	private int duracion;
	
	/**
     * Constructor que crea una nueva Llamada con duracion expresada en segundos
     *
     * @param segundos duracion en segundos, debe ser un valor mayor o igual a cero
     */
	public Llamada(Integer segundos) {
		if(!segundos.equals(null) && segundos >= 0)
			duracion = segundos;
		else
			throw new IllegalArgumentException("Valor Invalido de Duracion de la Llamada");
	}

	public int getDuracion() {
		return duracion;
	}

    /**
     * Crea un llamada aleatoria
     *
     * @param minimaDuracion minima duracion en segundos debe ser un valor mayor o igual a cero
     * @param maximaDuracion maxima duracion en segundos debe ser un valor mayor o igual a minimaDuracion
     * @return Una nueva llamada aleatoria con una duracion aleatoria entre minimaDuracion y maximaDuracion
     */
	public static Optional<Llamada> crearAleatoria(Integer minimaDuracion, Integer maximaDuracion) {
		if(maximaDuracion >= minimaDuracion && minimaDuracion >= 0)
			return Optional.of(new Llamada(ThreadLocalRandom.current().nextInt(minimaDuracion, maximaDuracion + 1)));
		else
			throw new IllegalArgumentException("Valores NO Validos para crear Llamada");
	}
}
