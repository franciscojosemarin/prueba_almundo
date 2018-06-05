/**
 * 
 */
package com.franciscomarin.call_center;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.franciscomarin.call_center.Empleado.Estado;
import com.franciscomarin.call_center.Empleado.Tipo;

/**
 * Clase Dispatcher: Permite gestionar las Llamadas entre los Empleados
 * @author Francisco Marin
 *
 */
public class Dispatcher implements Runnable {
	private static final Logger logger = Logger.getLogger(Dispatcher.class.getName());
	public static final int HILOS_MAX = 10;
	private Boolean activo;
	private ExecutorService execService;
	private ConcurrentLinkedDeque<Empleado> empleados;
	private ConcurrentLinkedDeque<Llamada> entrantes;

	/**
	 * Constructor que crea el Dispatcher de Llamadas con una Lista determinada de Empleados
	 * @param empleados Lista de Empleados que atederan las llamadas
	 */
	public Dispatcher(List<Empleado> empleados) {
		if(!empleados.equals(null) && empleados.size()>0) {
			this.empleados = new ConcurrentLinkedDeque<Empleado>(empleados);
			this.entrantes = new ConcurrentLinkedDeque<>();
			this.execService = Executors.newFixedThreadPool(HILOS_MAX);
		}else
			throw new NullPointerException("Lista de Empleados Invalida");
	}

	public Boolean isActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Metodo que permite enviar la nueva Llamada a los Empleados
	 * @param l Llamada Entrante
	 */
	public synchronized void dispatchCall(Llamada l) {
		logger.info("Nueva llamada de " + l.getDuracion() + " segundos");
		this.entrantes.add(l);
	}

	/**
	 * Inicia los hilos de los Empleados y activa la ejcucion del Dispatcher
	 */
	public synchronized void start() {
		this.setActivo(true);
		for (Empleado e : this.empleados) {
			this.execService.execute(e);
		}
	}

	/**
	 * Detiene los hilos de los Empleados y desactiva el Dispatcher
	 */
	public synchronized void stop() {
		this.setActivo(false);
		this.execService.shutdown();
	}

	/**
	 * Mientras el Dispatcher este Activo y la lista de Llamadas Entrantes no este vacia, 
	 * se encargara de buscar un Empleado DISPONIBLE para atender las Llamadas Entrantes.
	 * Las Llamadas quedan en espera(Lista entrantes) mientras se desocupa algun Empleado.
	 */
	@Override
	public void run() {
		while (isActivo()) {
			if (this.entrantes.isEmpty()) {
				continue;
			} else {
				Optional<Empleado> e = this.buscarEmpleado();
				if (!e.isPresent()) {
					continue;
				}
				Llamada l = this.entrantes.poll();
				try {
					e.get().atender(l);
				} catch (Exception ex) {
					logger.warning(ex.getMessage());
					this.entrantes.addFirst(l);
				}
			}
		}
	}

	/**
	 * Busca un Empleado DISPONIBLE en la lista de empleados, ordenada por su Prioridad
	 * @return Un Empleado DISPONIBLE sino VACIO
	 */
	public Optional<Empleado> buscarEmpleado() {
		if(!empleados.equals(null)) {
			List<Empleado> eDisponibles = empleados.stream()
					.filter(e -> e.getEstado().equals(Estado.DISPONIBLE))
					.sorted(Comparator.comparingInt(Empleado::getPrioridad))
					.collect(Collectors.toList());
			if(eDisponibles.size()>0) {
				logger.info("Empleados disponibles: " + eDisponibles.size());
				Empleado eDisponible = eDisponibles.get(0);
				
				if(eDisponible.getTipo().equals(Tipo.SUPERVISOR)) {
					logger.info("Operador(es) no disponible(s)");
				} else if(eDisponible.getTipo().equals(Tipo.DIRECTOR)) {
					logger.info("Supervisor(es) no disponible(s)");
				}
				logger.info("Empleado " + eDisponible.getTipo() + " disponible");
				return Optional.of(eDisponible);

			}
			logger.info("Ningun empleado disponible");
		}
		return Optional.empty();
	}
}
