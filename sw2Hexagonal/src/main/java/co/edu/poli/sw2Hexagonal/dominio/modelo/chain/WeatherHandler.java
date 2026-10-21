package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

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
 *       {@link Weather} y la cadena se detiene.</li>
 *   <li>Si es el último eslabón y aprueba, marca el clima como apto.</li>
 * </ul>
 * Orden de la cadena:
 * <pre>
 * TemperatureHandler → HumidityHandler → WindHandler → PressureHandler → VisibilityHandler
 * </pre>
 */
public abstract class WeatherHandler {

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
     * Arma la cadena y evalúa el clima. El resultado queda guardado en el
     * propio {@link Weather}.
     *
     * @param clima clima a evaluar
     * @return el mismo objeto, ya evaluado
     */
    public static Weather evaluar(Weather clima) {
        clima.reiniciarEvaluacion();
        crearCadena().manejar(clima);
        return clima;
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

    public WeatherHandler getSiguiente() {
        return siguiente;
    }

    /**
     * Valida la condición de este eslabón y decide si continúa la cadena.
     *
     * @param clima clima a validar; aquí se registra el resultado
     */
    public final void manejar(Weather clima) {
        double valor = obtenerValor(clima);

        if (Double.isNaN(valor)) {
            clima.rechazar(getRegla(),
                    "El proveedor no entregó el dato de " + getNombreMagnitud() + ".");
            return;
        }

        if (!cumple(valor)) {
            clima.rechazar(getRegla(), String.format(
                    "Valor medido de %s: %.1f %s.", getNombreMagnitud(), valor, getUnidad()));
            return;
        }

        clima.registrarAprobacion(String.format(
                "%s (medido: %.1f %s)", getRegla(), valor, getUnidad()));

        if (siguiente != null) {
            siguiente.manejar(clima);
        } else {
            clima.aprobar();
        }
    }

    /**
     * @param clima clima
     * @return el valor de la magnitud que valida este handler
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
