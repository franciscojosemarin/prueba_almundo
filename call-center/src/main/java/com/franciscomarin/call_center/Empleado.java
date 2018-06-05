package com.franciscomarin.call_center;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Clase Empleado: para el manejo de los Objetos Empleado
 * @author Francisco Marin
 *
 */
public class Empleado implements Runnable {
	/**
	 * Identifica el Tipo de Empleado al que pertenece, 
	 * y retorna su Prioridad en el grupo
	 */
	public enum Tipo {
		OPERADOR(1),
		SUPERVISOR(2),
		DIRECTOR(3);

		private int prioridad;

		Tipo(int tipo){
			this.prioridad = tipo;
		}

		public int value() {
			return prioridad;
		}
	}

	/**
	 * Identifica el Estado en que se encuentra el Empleado
	 *
	 */
	public enum Estado {
		DISPONIBLE,
		OCUPADO
	}
	private static final Logger logger = Logger.getLogger(Empleado.class.getName());
	private Tipo tipo;
	private Estado estado;
	private int prioridad;
	private ConcurrentLinkedDeque<Llamada> entrantes;
	private ConcurrentLinkedDeque<Llamada> atendidas;

	/**
	 * Constructor que crea un nuevo Empleado de un Tipo determinado, establece un prioridad al crearlo
	 * 1.- Empleado Operador
	 * 2.- Empleado Supervisor
	 * 3.- Empleado Director
	 * 
	 * @param tipo Tipo de Empleado (Operador, Supervisor o Director)
	 */
	public Empleado(Tipo tipo) {
		if(!tipo.equals(null)) {
			this.setTipo(tipo);
			this.setEstado(Estado.DISPONIBLE);
			switch (tipo) {
			case OPERADOR: this.setPrioridad(Tipo.OPERADOR.prioridad); break;
			case SUPERVISOR: this.setPrioridad(Tipo.SUPERVISOR.prioridad); break;
			case DIRECTOR: this.setPrioridad(Tipo.DIRECTOR.prioridad); break;
			}
			this.entrantes = new ConcurrentLinkedDeque<Llamada>();
			this.atendidas = new ConcurrentLinkedDeque<Llamada>();
		}
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		logger.info("Empleado " + Thread.currentThread().getName() + " cambia a " + estado);
		this.estado = estado;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}

	public synchronized List<Llamada> getAtendidas() {
		return new ArrayList<Llamada>(atendidas);
	}

	public synchronized List<Llamada> getEntrantes() {
		return new ArrayList<Llamada>(entrantes);
	}

	/**
	 * Permite atender la llamada asignada al Empleado
	 * @param l Llamada a ser atendida
	 */
	public synchronized void atender(Llamada l) {
		logger.info("Empleado " + Thread.currentThread().getName() + " atiende llamada de " + l.getDuracion() + " segundos");
		this.entrantes.add(l);
	}

	/**
	 * Crea un nuevo Empleado Tipo OPERADOR
	 * @return Un Empleado Tipo OPERADOR
	 */
	public static Empleado nuevoOperador() {
		return new Empleado(Tipo.OPERADOR);
	}

	/**
	 * Crea un nuevo Empleado SUPERVISOR
	 * @return Un Empleado Tipo SUPERVISOR
	 */
	public static Empleado nuevoSupervisor() {
		return new Empleado(Tipo.SUPERVISOR);
	}

	/**
	 * Crea un nuevo Empleado Tipo DIRECTOR
	 * @return Un Empleado Tipo DIRECTOR
	 */
	public static Empleado nuevoDirector() {
		return new Empleado(Tipo.DIRECTOR);
	}

	/**
	 * Permite gestionar las llamadas de los empleados en los hilos de ejecucion.
	 * Si la lista de llamadas Entrantes no esta vacia, cambia el Estado del Empleado a OCUPADO
	 * si esta DISPONIBLE para que atienda la Llamada. Cuando finaliza la Llamada cambia el Estado a DISPONIBLE. 
	 */
	@Override
	public void run() {
		logger.info("Empleado " + Thread.currentThread().getName() + " inicia");
		while (true) {
			if (!this.entrantes.isEmpty()) {
				Llamada l = this.entrantes.poll();
				this.setEstado(Estado.OCUPADO);
				logger.info("Empleado " + Thread.currentThread().getName() + " inicia atender llamada de " + l.getDuracion() + " segundos");
				try {
					TimeUnit.SECONDS.sleep(l.getDuracion());
				} catch (InterruptedException e) {
					logger.warning("Empleado " + Thread.currentThread().getName() + " no termino llamada de " + l.getDuracion() + " segundos");
				} finally {
					this.setEstado(Estado.DISPONIBLE);
				}
				this.atendidas.add(l);
				logger.info("Empleado " + Thread.currentThread().getName() + " termina llamada de " + l.getDuracion() + " segundos");
			}
		}
	}
}
