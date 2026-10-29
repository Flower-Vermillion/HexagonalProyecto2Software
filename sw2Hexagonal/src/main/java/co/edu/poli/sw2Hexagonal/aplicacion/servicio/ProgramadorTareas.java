package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.dominio.modelo.command.DroneCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Invoker del patrón Command.
 * <p>
 * Guarda la secuencia de tareas programadas para una misión y las ejecuta en
 * el orden en que se registraron. No sabe cómo se realiza cada tarea: solo
 * llama a {@link DroneCommand#ejecutar()}.
 * </p>
 */
public class ProgramadorTareas {

    /** Tareas programadas, en orden de registro. */
    private final List<DroneCommand> tareas = new ArrayList<>();

    /**
     * Agrega una tarea al final de la secuencia.
     *
     * @param tarea comando a programar
     * @throws IllegalArgumentException si la tarea es nula
     */
    public void programar(DroneCommand tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }
        tareas.add(tarea);
    }

    /**
     * Elimina la tarea que está en una posición de la secuencia.
     *
     * @param indice posición de la tarea, empezando en 0
     * @throws IndexOutOfBoundsException si el índice no existe
     */
    public void eliminar(int indice) {
        tareas.remove(indice);
    }

    /**
     * Elimina todas las tareas programadas.
     */
    public void limpiar() {
        tareas.clear();
    }

    /**
     * @return lista de solo lectura con las tareas programadas, en orden
     */
    public List<DroneCommand> getTareas() {
        return Collections.unmodifiableList(tareas);
    }

    /**
     * Ejecuta todas las tareas en el orden en que se registraron. Si una
     * tarea falla, se registra el error y se continúa con la siguiente.
     *
     * @return resultados numerados, uno por tarea
     * @throws IllegalStateException si no hay tareas programadas
     */
    public List<String> ejecutarMision() {
        if (tareas.isEmpty()) {
            throw new IllegalStateException("No hay tareas programadas");
        }
        List<String> resultados = new ArrayList<>();
        int numero = 1;
        for (DroneCommand tarea : tareas) {
            String resultado;
            try {
                resultado = tarea.ejecutar();
            } catch (RuntimeException e) {
                resultado = "ERROR: " + e.getMessage();
            }
            resultados.add(numero + ". " + tarea.getNombre() + " → " + resultado);
            numero++;
        }
        return resultados;
    }
}