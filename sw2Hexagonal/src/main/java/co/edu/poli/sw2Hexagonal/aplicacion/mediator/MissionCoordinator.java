package co.edu.poli.sw2Hexagonal.aplicacion.mediator;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.BatterySystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.CommunicationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.NavigationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.SensorSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mediador concreto del patrón Mediator: coordinador de misión.
 * <p>
 * Centraliza la comunicación entre los componentes y controla la secuencia
 * de una operación de vigilancia:
 * </p>
 * <ol>
 *   <li>Navegación prepara la ruta.</li>
 *   <li>Batería verifica la disponibilidad energética.</li>
 *   <li>Sensores se activan.</li>
 *   <li>Comunicación establece el enlace.</li>
 * </ol>
 * <p>
 * Si una condición falla (batería insuficiente o enlace fuera de alcance),
 * aborta la misión: desactiva los sensores, si estaban activos, y cancela la
 * ruta. Todo lo ocurrido queda registrado en una bitácora.
 * </p>
 */
public class MissionCoordinator implements DroneMediator {

    /** Sistema de navegación. */
    private final NavigationSystem navegacion;
    /** Sistema de sensores. */
    private final SensorSystem sensores;
    /** Sistema de batería. */
    private final BatterySystem bateria;
    /** Sistema de comunicación. */
    private final CommunicationSystem comunicacion;

    /** Registro de todo lo ocurrido en la operación. */
    private final List<String> bitacora = new ArrayList<>();

    /** Distancia hasta la zona de la misión actual, en km. */
    private double distanciaKm;
    /** Batería disponible para la misión actual, en %. */
    private double bateriaDisponible;
    /** Indica si los sensores están activos. */
    private boolean sensoresActivos;
    /** Indica si la misión quedó iniciada. */
    private boolean misionIniciada;

    /**
     * Crea el coordinador y se registra como mediador de cada componente.
     *
     * @param navegacion   sistema de navegación
     * @param sensores     sistema de sensores
     * @param bateria      sistema de batería
     * @param comunicacion sistema de comunicación
     */
    public MissionCoordinator(NavigationSystem navegacion, SensorSystem sensores,
                              BatterySystem bateria, CommunicationSystem comunicacion) {
        this.navegacion = navegacion;
        this.sensores = sensores;
        this.bateria = bateria;
        this.comunicacion = comunicacion;

        navegacion.setMediador(this);
        sensores.setMediador(this);
        bateria.setMediador(this);
        comunicacion.setMediador(this);
    }

    /**
     * Inicia la coordinación de una operación de vigilancia. El primer paso
     * es pedir la ruta; el resto de la secuencia avanza con los eventos que
     * reportan los componentes.
     *
     * @param codigoDron        código del dron
     * @param zona              zona de vigilancia
     * @param distanciaKm       distancia hasta la zona, en km
     * @param bateriaDisponible batería disponible, en %
     */
    public void iniciarMision(String codigoDron, String zona,
                              double distanciaKm, double bateriaDisponible) {
        this.distanciaKm = distanciaKm;
        this.bateriaDisponible = bateriaDisponible;
        this.sensoresActivos = false;
        this.misionIniciada = false;
        bitacora.clear();

        registrar("Coordinador", String.format(
                "Iniciando operación de vigilancia del dron %s en '%s'", codigoDron, zona));
        navegacion.prepararRuta(zona, distanciaKm);
    }

    /**
     * Recibe el evento de un componente, lo registra en la bitácora y decide
     * qué componente actúa a continuación.
     *
     * @param emisor  componente que generó el evento
     * @param evento  evento ocurrido
     * @param detalle descripción de lo ocurrido
     */
    @Override
    public void notificar(Componente emisor, Evento evento, String detalle) {
        registrar(emisor.getNombre(), detalle);

        switch (evento) {
            case RUTA_PREPARADA -> bateria.verificarDisponibilidad(distanciaKm, bateriaDisponible);
            case BATERIA_SUFICIENTE -> sensores.activar();
            case BATERIA_INSUFICIENTE -> abortar("batería insuficiente");
            case SENSORES_ACTIVADOS -> {
                sensoresActivos = true;
                comunicacion.establecerEnlace(distanciaKm);
            }
            case ENLACE_ESTABLECIDO -> {
                misionIniciada = true;
                registrar("Coordinador", "✔ MISIÓN INICIADA: todos los sistemas listos");
            }
            case ENLACE_FALLIDO -> abortar("sin enlace de comunicación");
            case SENSORES_DESACTIVADOS -> sensoresActivos = false;
            case RUTA_CANCELADA -> {
                // Fin del aborto: no hay más acciones
            }
        }
    }

    /**
     * Aborta la misión: desactiva los sensores si estaban activos y cancela
     * la ruta.
     *
     * @param motivo causa del aborto
     */
    private void abortar(String motivo) {
        if (sensoresActivos) {
            sensores.desactivar();
        }
        navegacion.cancelarRuta();
        registrar("Coordinador", "✘ MISIÓN ABORTADA: " + motivo);
    }

    /**
     * Agrega una línea a la bitácora.
     *
     * @param origen  quién genera la línea
     * @param mensaje texto a registrar
     */
    private void registrar(String origen, String mensaje) {
        bitacora.add("[" + origen + "] " + mensaje);
    }

    /**
     * @return {@code true} si la misión quedó iniciada
     */
    public boolean isMisionIniciada() {
        return misionIniciada;
    }

    /**
     * @return bitácora de solo lectura con todo lo ocurrido, en orden
     */
    public List<String> getBitacora() {
        return Collections.unmodifiableList(bitacora);
    }
}