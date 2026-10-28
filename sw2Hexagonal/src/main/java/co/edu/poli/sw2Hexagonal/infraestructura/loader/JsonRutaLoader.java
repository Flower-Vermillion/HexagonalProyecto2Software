package co.edu.poli.sw2Hexagonal.infraestructura.loader;

import co.edu.poli.sw2Hexagonal.dominio.modelo.strategy.PuntoGeografico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Carga una ruta (punto de origen y punto de destino) desde un archivo JSON
 * con el formato:
 * <pre>
 * {
 *   "origen":  { "nombre": "...", "latitud": 4.60971, "longitud": -74.08175 },
 *   "destino": { "nombre": "...", "latitud": 4.70159, "longitud": -74.14690 }
 * }
 * </pre>
 */
public class JsonRutaLoader {

    /** Punto de origen cargado. */
    private PuntoGeografico origen;

    /** Punto de destino cargado. */
    private PuntoGeografico destino;

    /**
     * Lee y procesa el archivo JSON de la ruta.
     *
     * @param archivo ruta del archivo JSON
     * @throws IllegalArgumentException si el archivo no se puede leer o no
     *                                  tiene el formato esperado
     */
    public void cargar(Path archivo) {
        try {
            cargarDesdeTexto(Files.readString(archivo, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo: " + archivo, e);
        }
    }

    /**
     * Procesa el contenido JSON de una ruta.
     *
     * @param json contenido del archivo
     * @throws IllegalArgumentException si falta el origen, el destino o
     *                                  alguno de sus campos
     */
    public void cargarDesdeTexto(String json) {
        this.origen = leerPunto(json, "origen");
        this.destino = leerPunto(json, "destino");
    }

    /**
     * @return punto de origen, o {@code null} si no se ha cargado una ruta
     */
    public PuntoGeografico getOrigen() {
        return origen;
    }

    /**
     * @return punto de destino, o {@code null} si no se ha cargado una ruta
     */
    public PuntoGeografico getDestino() {
        return destino;
    }

    /**
     * Extrae un punto geográfico del JSON.
     *
     * @param json  contenido completo
     * @param clave nombre del objeto: {@code origen} o {@code destino}
     * @return punto geográfico
     */
    private PuntoGeografico leerPunto(String json, String clave) {
        Matcher bloque = Pattern.compile("\"" + clave + "\"\\s*:\\s*\\{([^}]*)}").matcher(json);
        if (!bloque.find()) {
            throw new IllegalArgumentException("El JSON no contiene el objeto '" + clave + "'");
        }
        String contenido = bloque.group(1);

        Matcher nombre = Pattern.compile("\"nombre\"\\s*:\\s*\"([^\"]*)\"").matcher(contenido);
        String textoNombre = nombre.find() ? nombre.group(1) : clave;

        return new PuntoGeografico(
                textoNombre,
                leerNumero(contenido, "latitud", clave),
                leerNumero(contenido, "longitud", clave));
    }

    /**
     * Extrae el valor numérico de un campo.
     *
     * @param contenido contenido del objeto JSON
     * @param campo     nombre del campo
     * @param clave     objeto al que pertenece, para el mensaje de error
     * @return valor numérico
     */
    private double leerNumero(String contenido, String campo, String clave) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(contenido);
        if (!m.find()) {
            throw new IllegalArgumentException("Falta '" + campo + "' en '" + clave + "'");
        }
        return Double.parseDouble(m.group(1));
    }
}