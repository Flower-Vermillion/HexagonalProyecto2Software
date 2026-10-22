package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Eslabón base del patrón Chain of Responsibility para evaluar la viabilidad
 * de vuelo.
 * <p>
 * Cada subclase valida <b>una sola</b> condición climática:
 * </p>
 * <ul>
 *   <li>Si la condición se cumple, registra la aprobación y pasa la
 *       solicitud al siguiente handler.</li>
 *   <li>Si la condición falla, registra el motivo del rechazo en el
 *       {@link Resultado} y la cadena se detiene.</li>
 *   <li>Si es el último eslabón y aprueba, marca el clima como apto.</li>
 * </ul>
 * Orden de la cadena:
 * <pre>
 * TemperatureHandler → HumidityHandler → WindHandler → PressureHandler → VisibilityHandler
 * </pre>
 * <p>
 * La cadena es independiente del patrón Observer: no notifica a ningún
 * observador ni modifica el {@link Weather}.
 * </p>
 */
public abstract class WeatherHandler {

    /**
     * Resultado de recorrer la cadena. Se mantiene fuera de {@link Weather}
     * para que el clima solo contenga los cinco aspectos meteorológicos.
     */
    public static class Resultado {

        /** Indica si el clima es apto para volar. */
        private boolean apto;

        /** Regla que no se cumplió, o {@code null} si el clima es apto. */
        private String reglaIncumplida;

        /** Motivo del rechazo, o {@code null} si el clima es apto. */
        private String motivo;

        /** Reglas aprobadas, en el orden en que se evaluaron. */
        private final List<String> aprobaciones = new ArrayList<>();

        /**
         * Registra una regla aprobada.
         *
         * @param detalle descripción de la regla y del valor medido
         */
        private void registrarAprobacion(String detalle) {
            aprobaciones.add(detalle);
        }

        /**
         * Marca el clima como no apto.
         *
         * @param regla  regla que no se cumplió
         * @param motivo explicación del rechazo
         */
        private void rechazar(String regla, String motivo) {
            this.apto = false;
            this.reglaIncumplida = regla;
            this.motivo = motivo;
        }

        /**
         * Marca el clima como apto. Lo llama el último eslabón si todo se cumple.
         */
        private void aprobar() {
            this.apto = true;
        }

        /**
         * @return {@code true} si el clima es apto para volar
         */
        public boolean isApto() {
            return apto;
        }

        /**
         * @return regla que no se cumplió, o {@code null} si el clima es apto
         */
        public String getReglaIncumplida() {
            return reglaIncumplida;
        }

        /**
         * @return motivo del rechazo, o {@code null} si el clima es apto
         */
        public String getMotivo() {
            return motivo;
        }

        /**
         * @return lista de solo lectura con las reglas aprobadas
         */
        public List<String> getAprobaciones() {
            return Collections.unmodifiableList(aprobaciones);
        }

        /**
         * Construye un texto con el recorrido completo de la cadena: reglas
         * aprobadas, regla incumplida (si la hay) y resultado final.
         *
         * @return detalle listo para mostrar en la interfaz
         */
        public String getDetalle() {
            StringBuilder sb = new StringBuilder();
            for (String a : aprobaciones) {
                sb.append("✔ ").append(a).append('\n');
            }
            if (apto) {
                sb.append("\nResultado: APTO PARA VOLAR");
            } else {
                sb.append("✘ ").append(reglaIncumplida).append('\n')
                  .append("   ").append(motivo).append('\n')
                  .append("\nResultado: NO APTO PARA VOLAR (la cadena se detuvo)");
            }
            return sb.toString();
        }
    }

    /** Siguiente eslabón de la cadena, o {@code null} si este es el último. */
    private WeatherHandler siguiente;

    /**
     * Arma la cadena completa en el orden que exige el negocio.
     *
     * @return el primer eslabón, ya enlazado con los demás
     */
    public static WeatherHandler crearCadena() {
        WeatherHandler inicio = new TemperatureHandler();
        inicio.setSiguiente(new HumidityHandler())
              .setSiguiente(new WindHandler())
              .setSiguiente(new PressureHandler())
              .setSiguiente(new VisibilityHandler());
        return inicio;
    }

    /**
     * Arma la cadena y evalúa el clima.
     *
     * @param clima clima a evaluar
     * @return el resultado de la evaluación
     */
    public static Resultado evaluar(Weather clima) {
        Resultado resultado = new Resultado();
        crearCadena().manejar(clima, resultado);
        return resultado;
    }

    /**
     * Enlaza el siguiente handler de la cadena.
     *
     * @param siguiente handler que se ejecuta si este aprueba
     * @return el handler recibido, para encadenar llamadas
     */
    public WeatherHandler setSiguiente(WeatherHandler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * @return el siguiente handler, o {@code null} si este es el último
     */
    public WeatherHandler getSiguiente() {
        return siguiente;
    }

    /**
     * Valida la condición de este eslabón y decide si continúa la cadena.
     *
     * @param clima     clima a validar
     * @param resultado objeto donde se registra el resultado
     */
    public final void manejar(Weather clima, Resultado resultado) {
        double valor = obtenerValor(clima);

        if (Double.isNaN(valor)) {
            resultado.rechazar(getRegla(),
                    "El proveedor no entregó el dato de " + getNombreMagnitud() + ".");
            return;
        }

        if (!cumple(valor)) {
            resultado.rechazar(getRegla(), String.format(
                    "Valor medido de %s: %.1f %s.", getNombreMagnitud(), valor, getUnidad()));
            return;
        }

        resultado.registrarAprobacion(String.format(
                "%s (medido: %.1f %s)", getRegla(), valor, getUnidad()));

        if (siguiente != null) {
            siguiente.manejar(clima, resultado);
        } else {
            resultado.aprobar();
        }
    }

    /**
     * @param clima clima
     * @return el valor de la magnitud que valida este handler, en la unidad
     *         de {@link #getUnidad()}
     */
    protected abstract double obtenerValor(Weather clima);

    /**
     * @param valor valor medido
     * @return {@code true} si el valor está dentro de los límites permitidos
     */
    protected abstract boolean cumple(double valor);

    /**
     * @return descripción de la regla, por ejemplo "Temperatura entre 0 °C y 40 °C"
     */
    public abstract String getRegla();

    /**
     * @return nombre de la magnitud en minúsculas, por ejemplo "temperatura"
     */
    protected abstract String getNombreMagnitud();

    /**
     * @return unidad de la magnitud, por ejemplo "°C"
     */
    protected abstract String getUnidad();
}